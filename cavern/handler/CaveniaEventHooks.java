package cavern.handler;

import cavern.api.CavernAPI;
import cavern.api.ICavenicMob;
import cavern.api.IHunterStats;
import cavern.item.ItemCave;
import cavern.stats.HunterRank;
import cavern.stats.HunterStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class CaveniaEventHooks
{
	@SubscribeEvent
	public void onLivingSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (CavernAPI.dimension.isEntityInCavenia(entity) && !(entity instanceof ICavenicMob))
		{
			event.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void onLivingDeathEvent(LivingDeathEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (CavernAPI.dimension.isEntityInCavenia(entity))
		{
			if (entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)entity;

				player.getEntityData().setTag("Cavenia:Inventory", player.inventory.writeToNBT(new NBTTagList()));
			}
			else if (entity instanceof ICavenicMob)
			{
				int point = ((ICavenicMob)entity).getHuntingPoint();
				DamageSource source = event.getSource();
				Entity sourceEntity = source.getEntity();

				if (sourceEntity == null || !(sourceEntity instanceof EntityPlayer))
				{
					sourceEntity = source.getSourceOfDamage();
				}

				if (sourceEntity != null && sourceEntity instanceof EntityPlayer)
				{
					EntityPlayer player = (EntityPlayer)sourceEntity;

					if (point > 0)
					{
						HunterStats.get(player).addPoint(point);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event)
	{
		EntityPlayer player = event.getEntityPlayer();
		EntityPlayer old = event.getOriginal();

		if (event.isWasDeath() && CavernAPI.dimension.isEntityInCavenia(player))
		{
			if (old.getEntityData().hasKey("Cavenia:Inventory"))
			{
				player.inventory.readFromNBT(old.getEntityData().getTagList("Cavenia:Inventory", NBT.TAG_COMPOUND));
			}

			player.experienceLevel = old.experienceLevel;
			player.experienceTotal = old.experienceTotal;
			player.experience = old.experience;
			player.setScore(old.getScore());
		}
	}

	@SubscribeEvent
	public void onPlayerDrops(PlayerDropsEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();

		if (CavernAPI.dimension.isEntityInCavenia(player))
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		EntityPlayer player = event.player;

		if (CavernAPI.dimension.isEntityInCavenia(player))
		{
			World world = player.world;

			if (!world.isRemote)
			{
				ItemStack item = ItemCave.EnumType.CAVENIC_ORB.getItemStack();

				if (!player.inventory.hasItemStack(item))
				{
					player.entityDropItem(item, 0.5F);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;

			HunterStats.get(player).adjustData();
		}
	}

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (CavernAPI.dimension.isEntityInCavenia(entity) && entity instanceof IMob)
		{
			float amount = event.getAmount();
			DamageSource source = event.getSource();

			if (source.isExplosion())
			{
				return;
			}

			Entity sourceEntity = source.getEntity();

			if (sourceEntity == null || !(sourceEntity instanceof EntityPlayer))
			{
				sourceEntity = source.getSourceOfDamage();
			}

			if (sourceEntity != null && sourceEntity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)sourceEntity;
				IHunterStats hunterStats = HunterStats.get(player);
				HunterRank hunterRank = HunterRank.get(hunterStats.getRank());
				float boost = hunterRank.getBoost();
				int superCritical = hunterRank.getSuperCritical();

				if (superCritical > 0 && CaveEventHooks.RANDOM.nextInt(100) + 1 <= superCritical)
				{
					boost *= 2.5F;

					entity.world.newExplosion(player, entity.posX, entity.posY, entity.posZ, 1.75F, false, true);
				}

				event.setAmount(amount * boost);
			}
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (CavernAPI.dimension.isEntityInCavenia(entity))
		{
			if (entity.ticksExisted % 200 == 0 && entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)entity;

				if (player.getHealth() <= 10.0F)
				{
					NBTTagCompound data = player.getEntityData();
					World world = player.world;

					if (!data.hasKey("Cavenia:BuffTime", NBT.TAG_ANY_NUMERIC) || data.getLong("Cavenia:BuffTime") + 3600L < world.getTotalWorldTime())
					{
						Potion potion;

						switch (CaveEventHooks.RANDOM.nextInt(4))
						{
							case 1:
								potion = MobEffects.RESISTANCE;
							case 2:
								potion = MobEffects.STRENGTH;
							case 3:
								potion = MobEffects.SPEED;
							default:
								potion = MobEffects.REGENERATION;
						}

						player.addPotionEffect(new PotionEffect(potion, 90 * 20, CaveEventHooks.RANDOM.nextInt(2) + 1));

						data.setLong("Cavenia:BuffTime", world.getTotalWorldTime());
					}
				}
			}
		}
	}
}