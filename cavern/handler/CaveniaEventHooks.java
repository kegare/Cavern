package cavern.handler;

import cavern.api.CavernAPI;
import cavern.api.ICavenicMob;
import cavern.api.IHunterStats;
import cavern.item.ItemCave;
import cavern.stats.HunterRank;
import cavern.stats.HunterStats;
import cavern.util.CaveUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class CaveniaEventHooks
{
	public static final String NBT_INVENTORY = "Cavenia:Inventory";
	public static final String NBT_BUFFTIME = "Cavenia:BuffTime";

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

				player.getEntityData().setTag(NBT_INVENTORY, player.inventory.writeToNBT(new NBTTagList()));
			}
			else if (entity instanceof ICavenicMob)
			{
				int point = ((ICavenicMob)entity).getHuntingPoint();

				if (point <= 0)
				{
					return;
				}

				CaveUtils.getSourceEntities(EntityPlayer.class, event.getSource(), false).forEach(player -> HunterStats.get(player).addPoint(point));
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
			if (old.getEntityData().hasKey(NBT_INVENTORY))
			{
				player.inventory.readFromNBT(old.getEntityData().getTagList(NBT_INVENTORY, NBT.TAG_COMPOUND));
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
				ItemStack stack = ItemCave.EnumType.CAVENIC_ORB.getItemStack();

				if (!player.inventory.hasItemStack(stack))
				{
					player.entityDropItem(stack, 0.5F);
				}
			}
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

			EntityPlayer player = CaveUtils.getSourceEntity(EntityPlayer.class, source);

			if (player == null)
			{
				return;
			}

			IHunterStats hunterStats = HunterStats.get(player);
			HunterRank hunterRank = HunterRank.get(hunterStats.getRank());
			float boost = hunterRank.getBoost();
			int superCritical = hunterRank.getSuperCritical();

			if (superCritical > 0 && CaveEventHooks.RANDOM.nextInt(100) + 1 <= superCritical)
			{
				boost *= 2.5F;

				entity.world.newExplosion(player, entity.posX, entity.posY, entity.posZ, 1.75F, false, false);
			}

			event.setAmount(amount * boost);
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
				float health = player.getHealth();

				if (health <= 10.0F)
				{
					NBTTagCompound data = player.getEntityData();
					World world = player.world;

					if (!data.hasKey(NBT_BUFFTIME, NBT.TAG_ANY_NUMERIC) || data.getLong(NBT_BUFFTIME) + 3600L < world.getTotalWorldTime())
					{
						Potion potion;

						if (health <= 2.0F)
						{
							potion = MobEffects.REGENERATION;
						}
						else switch (CaveEventHooks.RANDOM.nextInt(4))
						{
							case 1:
								potion = MobEffects.RESISTANCE;
								break;
							case 2:
								potion = MobEffects.STRENGTH;
								break;
							case 3:
								potion = MobEffects.SPEED;
								break;
							default:
								potion = MobEffects.REGENERATION;
						}

						if (player.isPotionActive(potion))
						{
							return;
						}

						int sec;

						switch (world.getDifficulty())
						{
							case EASY:
								sec = 120;
								break;
							case HARD:
								sec = 60;
								break;
							default:
								sec = 90;
						}

						player.addPotionEffect(new PotionEffect(potion, sec * 20, CaveEventHooks.RANDOM.nextInt(2) + 1));

						data.setLong(NBT_BUFFTIME, world.getTotalWorldTime());
					}
				}
			}
		}
	}
}