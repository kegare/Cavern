package cavern.handler;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.collect.Sets;

import cavern.api.CavernAPI;
import cavern.api.ICavenicMob;
import cavern.api.IHunterStats;
import cavern.api.IMagicianStats;
import cavern.api.IMinerStats;
import cavern.api.IPlayerData;
import cavern.block.BlockCave;
import cavern.block.BlockPortalCavern;
import cavern.block.CaveBlocks;
import cavern.config.GeneralConfig;
import cavern.core.CaveSounds;
import cavern.item.IAquaTool;
import cavern.item.IceEquipment;
import cavern.item.ItemCave;
import cavern.item.ItemElixir;
import cavern.item.ItemMagicalBook;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.CaveMusicMessage;
import cavern.network.client.LastMineMessage;
import cavern.stats.HunterStats;
import cavern.stats.MagicianStats;
import cavern.stats.MinerRank;
import cavern.stats.MinerStats;
import cavern.stats.PlayerData;
import cavern.util.BlockMeta;
import cavern.util.CaveUtils;
import cavern.util.WeightedItemStack;
import cavern.world.WorldCachedData;
import cavern.world.WorldProviderIceCavern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

public class CaveEventHooks
{
	protected static final Random RANDOM = new Random();

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (event.getEntity() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.getEntity();

			adjustPlayerStats(player);
		}
	}

	public static void adjustPlayerStats(EntityPlayer player)
	{
		IMinerStats minerStats = MinerStats.get(player, true);

		if (minerStats != null)
		{
			minerStats.adjustData();
		}

		IHunterStats hunterStats = HunterStats.get(player, true);

		if (hunterStats != null)
		{
			hunterStats.adjustData();
		}

		IMagicianStats magicianStats = MagicianStats.get(player, true);

		if (magicianStats != null)
		{
			magicianStats.adjustData();
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event)
	{
		if (!(event.player instanceof EntityPlayerMP))
		{
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP)event.player;
		WorldServer world = player.getServerWorld();

		if (GeneralConfig.cavernEscapeMission)
		{
			DimensionType fromDim = DimensionType.getById(event.fromDim);
			DimensionType toDim = DimensionType.getById(event.toDim);
			boolean fromCave = CavernAPI.dimension.isCaves(fromDim);
			boolean toCave = CavernAPI.dimension.isCaves(toDim);

			if (fromCave && !toCave && !GeneralConfig.canEscapeFromCaves(player))
			{
				MinecraftServer server = player.mcServer;
				WorldServer worldNew = server.getWorld(fromDim.getId());

				player.timeUntilPortal = player.getPortalCooldown();

				CaveUtils.transferPlayerToDimension(player, fromDim, WorldCachedData.get(worldNew).getRepatriationTeleporter());

				player.sendStatusMessage(new TextComponentTranslation("cavern.escapeMission.bad.message"), true);

				return;
			}
		}

		if (CavernAPI.dimension.isEntityInCaves(player))
		{
			IPlayerData playerData = PlayerData.get(player);
			DimensionType type = world.provider.getDimensionType();
			long time = playerData.getLastTeleportTime(type);

			if (time <= 0L || time + 18000L < world.getTotalWorldTime())
			{
				SoundEvent music = null;

				if (CavernAPI.dimension.isEntityInCavern(player) || CavernAPI.dimension.isEntityInHugeCavern(player))
				{
					if (world.rand.nextInt(2) == 0)
					{
						music = CaveSounds.MUSIC_CAVE;
					}
					else
					{
						music = CaveSounds.MUSIC_UNREST;
					}
				}
				else if (CavernAPI.dimension.isEntityInAquaCavern(player))
				{
					music = CaveSounds.MUSIC_AQUA;
				}
				else if (CavernAPI.dimension.isEntityInCaveland(player))
				{
					music = CaveSounds.MUSIC_HOPE;
				}
				else if (CavernAPI.dimension.isEntityInIceCavern(player) || CavernAPI.dimension.isEntityInCavenia(player))
				{
					music = CaveSounds.MUSIC_UNREST;
				}
				else if (CavernAPI.dimension.isEntityInRuinsCavern(player))
				{
					music = CaveSounds.MUSIC_CAVE;
				}

				if (music != null)
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage(music), player);
				}
			}

			playerData.setLastTeleportTime(type, world.getTotalWorldTime());
		}
	}

	@SubscribeEvent
	public void onTravelToDimension(EntityTravelToDimensionEvent event)
	{
		if (GeneralConfig.cavernEscapeMission)
		{
			Entity entity = event.getEntity();

			if (!(entity instanceof EntityPlayerMP))
			{
				return;
			}

			EntityPlayerMP player = (EntityPlayerMP)entity;
			boolean fromCave = CavernAPI.dimension.isCaves(DimensionType.getById(entity.dimension));
			boolean toCave = CavernAPI.dimension.isCaves(DimensionType.getById(event.getDimension()));

			if (fromCave && !toCave && !GeneralConfig.canEscapeFromCaves(player))
			{
				player.sendStatusMessage(new TextComponentTranslation("cavern.escapeMission.bad.message"), true);

				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		ItemStack stack = event.getItemStack();

		if (stack.isEmpty())
		{
			return;
		}

		World world = event.getWorld();
		BlockPos pos = event.getPos();
		IBlockState state = world.getBlockState(pos);

		if (state.getBlock() != Blocks.MOSSY_COBBLESTONE && (state.getBlock() != Blocks.STONEBRICK || state.getBlock().getMetaFromState(state) != BlockStoneBrick.MOSSY_META))
		{
			return;
		}

		EntityPlayer player = event.getEntityPlayer();
		Set<BlockPortalCavern> portals = Sets.newHashSet();

		portals.add(CaveBlocks.CAVERN_PORTAL);
		portals.add(CaveBlocks.AQUA_CAVERN_PORTAL);
		portals.add(CaveBlocks.CAVELAND_PORTAL);
		portals.add(CaveBlocks.ICE_CAVERN_PORTAL);
		portals.add(CaveBlocks.RUINS_CAVERN_PORTAL);
		portals.add(CaveBlocks.HUGE_CAVERN_PORTAL);

		Item portalItem = Items.AIR;

		for (BlockPortalCavern portal : portals)
		{
			if (portal.isTriggerItem(stack))
			{
				portalItem = Item.getItemFromBlock(portal);

				break;
			}
		}

		if (portalItem != Items.AIR)
		{
			EnumFacing facing = ObjectUtils.defaultIfNull(event.getFace(), EnumFacing.UP);
			Vec3d hit = event.getHitVec();
			EnumActionResult result = portalItem.onItemUse(player, world, pos, event.getHand(), facing, (float)hit.x, (float)hit.y, (float)hit.z);

			if (result == EnumActionResult.SUCCESS)
			{
				event.setCancellationResult(result);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event)
	{
		EntityPlayer entityPlayer = event.getPlayer();

		if (entityPlayer == null || !(entityPlayer instanceof EntityPlayerMP))
		{
			return;
		}

		EntityPlayerMP player = (EntityPlayerMP)entityPlayer;

		if (!CavernAPI.dimension.isEntityInCaves(player))
		{
			return;
		}

		if (GeneralConfig.isMiningPointItem(player.getHeldItemMainhand()))
		{
			IBlockState state = event.getState();
			int amount = MinerStats.getPointAmount(state);

			if (amount != 0)
			{
				IMinerStats stats = MinerStats.get(player);

				if (player.inventory.hasItemStack(ItemCave.EnumType.MINER_ORB.getItemStack()))
				{
					if (RANDOM.nextDouble() < 0.3D)
					{
						amount += Math.max(amount / 2, 1);
					}
				}

				stats.addPoint(amount);

				CaveNetworkRegistry.sendTo(new LastMineMessage(new BlockMeta(state), amount), player);
			}
		}

		if (CavernAPI.dimension.isEntityInIceCavern(player))
		{
			if (player.capabilities.isCreativeMode)
			{
				return;
			}

			IBlockState state = event.getState();

			if (state.getBlock() == Blocks.PACKED_ICE)
			{
				World world = event.getWorld();
				BlockPos pos = event.getPos();

				if (RANDOM.nextDouble() < 0.05D)
				{
					Block.spawnAsEntity(world, pos, new ItemStack(Blocks.ICE));
				}
				else if (RANDOM.nextDouble() < 0.0325D)
				{
					WeightedItemStack randomItem = WeightedRandom.getRandomItem(RANDOM, WorldProviderIceCavern.HIBERNATE_ITEMS);

					Block.spawnAsEntity(world, pos, randomItem.getItemStack());
				}
				else if (RANDOM.nextDouble() < 0.0085D)
				{
					WeightedItemStack randomItem = WeightedRandom.getRandomItem(RANDOM, BlockCave.RANDOMITE_ITEMS);

					Block.spawnAsEntity(world, pos, randomItem.getItemStack());
				}
				else if (IceEquipment.isIceEquipment(player.getHeldItemMainhand()) && RANDOM.nextDouble() < 0.03D || RANDOM.nextDouble() < 0.01D)
				{
					Block.spawnAsEntity(world, pos, new ItemStack(Blocks.PACKED_ICE));
				}
			}
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		float original = event.getOriginalSpeed();
		EntityPlayer player = event.getEntityPlayer();
		ItemStack held = player.getHeldItemMainhand();
		boolean miner = CavernAPI.dimension.isEntityInCaves(player) && CaveUtils.isItemPickaxe(held);

		if (!held.isEmpty() && player.isInsideOfMaterial(Material.WATER))
		{
			if (miner && MinerStats.get(player).getRank() >= MinerRank.AQUA_MINER.getRank())
			{
				if (player.onGround)
				{
					event.setNewSpeed(original * 10.0F);
				}
				else
				{
					event.setNewSpeed(original * 7.0F);
				}
			}
			else if (held.getItem() instanceof IAquaTool)
			{
				IAquaTool tool = (IAquaTool)held.getItem();
				float speed = tool.getAquaBreakSpeed(held, player, event.getPos(), event.getState(), original);

				if (speed > 0.0F)
				{
					event.setNewSpeed(speed);
				}
			}
		}

		if (miner)
		{
			int rank = MinerStats.get(player).getRank();
			float boost = MinerRank.get(rank).getBoost();

			event.setNewSpeed(event.getNewSpeed() * boost);
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (!(entity instanceof EntityPlayer))
		{
			return;
		}

		EntityPlayer player = (EntityPlayer)entity;

		if (CavernAPI.dimension.isEntityInCaves(entity))
		{
			if (player.isInsideOfMaterial(Material.WATER))
			{
				IMinerStats stats = MinerStats.get(player);

				if (stats.getRank() < MinerRank.AQUA_MINER.getRank())
				{
					return;
				}

				if (!player.canBreatheUnderwater() && !player.isPotionActive(MobEffects.WATER_BREATHING) && player.ticksExisted % 20 == 0)
				{
					player.setAir(300);
				}

				if (!player.capabilities.isFlying && EnchantmentHelper.getDepthStriderModifier(player) <= 1)
				{
					double posY = player.posY;
					float motion = 1.165F;

					player.motionX *= motion;
					player.motionZ *= motion;

					if (player.collidedHorizontally && player.isOffsetPositionInLiquid(player.motionX, player.motionY + 0.6000000238418579D - player.posY + posY, player.motionZ))
					{
						player.motionY = 0.30000001192092896D;
					}

					if (player.isSneaking())
					{
						player.motionY = 0.0D;
					}
				}
			}
		}

		if (entity instanceof EntityPlayer)
		{
			MagicianStats.get(player).onUpdate();
		}
	}

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();
		World world = entity.world;

		if (CavernAPI.dimension.isEntityInCaves(entity) && world.getGameRules().getBoolean("doMobLoot"))
		{
			double bookChance = 0.0D;
			double elixirChance = 0.01D;

			if (entity instanceof EntityWitch)
			{
				bookChance = 0.35D;
				elixirChance = 1.0D;
			}
			else if (entity instanceof ICavenicMob)
			{
				if (entity.isNonBoss())
				{
					bookChance = 0.035D;
					elixirChance = 0.35D;
				}
				else
				{
					bookChance = 0.45D;
					elixirChance = 0.5D;
				}
			}

			if (bookChance <= 0.0D && elixirChance <= 0.0D)
			{
				return;
			}

			int looting = event.getLootingLevel();
			List<EntityItem> drops = event.getDrops();
			double posX = entity.posX;
			double posY = entity.posY + 0.5D;
			double posZ = entity.posZ;

			for (int i = 0; i < looting + 1; ++i)
			{
				if (bookChance >= 1.0D || bookChance > 0.0D && RANDOM.nextDouble() < bookChance)
				{
					drops.add(new EntityItem(world, posX, posY, posZ, ItemMagicalBook.EnumType.UNKNOWN.getItemStack()));
				}
				else if (elixirChance >= 1.0D || elixirChance > 0.0D && RANDOM.nextDouble() < elixirChance)
				{
					ItemElixir.EnumType type = ItemElixir.EnumType.NORMAL;

					if (RANDOM.nextDouble() < 0.45D)
					{
						type = ItemElixir.EnumType.MEDIUM;
					}

					if (RANDOM.nextDouble() < 0.15D)
					{
						type = type == ItemElixir.EnumType.MEDIUM ? ItemElixir.EnumType.HIGH : ItemElixir.EnumType.NORMAL;
					}

					if (RANDOM.nextDouble() < 0.03D)
					{
						type = ItemElixir.EnumType.AWAKEN;
					}

					drops.add(new EntityItem(world, posX, posY, posZ, type.getItemStack()));
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event)
	{
		EntityLivingBase target = event.getTarget();

		if (target == null || !(target instanceof EntityPlayer))
		{
			return;
		}

		EntityPlayer player = (EntityPlayer)target;

		if (!MagicianStats.get(player).isInvisible())
		{
			return;
		}

		EntityLivingBase entity = event.getEntityLiving();

		entity.setRevengeTarget(null);

		if (entity instanceof EntityLiving)
		{
			((EntityLiving)entity).setAttackTarget(null);
		}
	}

	@SubscribeEvent
	public void onSleepInBed(PlayerSleepInBedEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();

		if (CavernAPI.dimension.isEntityInCaves(player))
		{
			SleepResult result = null;
			World world = player.world;

			if (!world.isRemote)
			{
				IPlayerData playerData = PlayerData.get(player);
				long worldTime = world.getTotalWorldTime();
				long sleepTime = playerData.getLastSleepTime();

				if (sleepTime <= 0L)
				{
					sleepTime = worldTime;

					playerData.setLastSleepTime(sleepTime);
				}

				long requireTime = GeneralConfig.sleepWaitTime * 20;

				if (sleepTime + requireTime > worldTime)
				{
					result = SleepResult.OTHER_PROBLEM;

					long remainTime = requireTime - (worldTime - sleepTime);
					int min = MathHelper.ceil(remainTime / 20 / 60 + 1);

					player.sendStatusMessage(new TextComponentTranslation("cavern.message.sleep.still", min), true);
				}
			}

			if (result == null)
			{
				result = CaveUtils.trySleep(player, event.getPos());
			}

			if (!world.isRemote && result == SleepResult.OK)
			{
				if (GeneralConfig.sleepRefresh)
				{
					if (player.shouldHeal())
					{
						player.heal(player.getMaxHealth() * 0.5F);
					}

					MagicianStats.get(player).addMP(100);
				}

				PlayerData.get(player).setLastSleepTime(world.getTotalWorldTime());
			}

			event.setResult(result);
		}
	}

	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent event)
	{
		EntityPlayer player = event.player;
		ItemStack stack = event.crafting;
		World world = player.world;

		if (!world.isRemote && !stack.isEmpty())
		{
			if (IceEquipment.isIceEquipment(stack))
			{
				int charge = IceEquipment.get(stack).getCharge();

				if (charge > 0)
				{
					CaveUtils.grantAdvancement(player, "ice_charge");
				}
			}
		}
	}
}