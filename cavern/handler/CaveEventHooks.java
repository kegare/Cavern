package cavern.handler;

import java.util.List;
import java.util.Random;

import cavern.api.CavernAPI;
import cavern.api.ICavenicMob;
import cavern.api.IHunterStats;
import cavern.api.IIceEquipment;
import cavern.api.IMagicianStats;
import cavern.api.IMinerStats;
import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.config.GeneralConfig;
import cavern.core.CaveSounds;
import cavern.core.Cavern;
import cavern.item.CaveItems;
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
import cavern.util.BlockMeta;
import cavern.util.CaveUtils;
import cavern.util.WeightedItemStack;
import cavern.world.TeleporterRepatriation;
import cavern.world.WorldProviderIceCavern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			WorldServer world = player.getServerWorld();

			if (GeneralConfig.cavernEscapeMission)
			{
				boolean fromCave = CavernAPI.dimension.isCaves(DimensionType.getById(event.fromDim));
				boolean toCave = CavernAPI.dimension.isCaves(DimensionType.getById(event.toDim));

				if (fromCave && !toCave && !GeneralConfig.canEscapeFromCaves(player))
				{
					MinecraftServer server = player.mcServer;
					WorldServer worldNew = server.getWorld(event.fromDim);

					if (worldNew != null)
					{
						player.timeUntilPortal = player.getPortalCooldown();

						server.getPlayerList().transferPlayerToDimension(player, event.fromDim, new TeleporterRepatriation(worldNew));

						player.sendStatusMessage(new TextComponentTranslation("cavern.escapeMission.bad.message"), true);

						return;
					}
				}
			}

			String suffix = ".LastTeleportTime";

			if (CavernAPI.dimension.isEntityInCavern(player))
			{
				NBTTagCompound data = player.getEntityData();
				String key = "Cavern" + suffix;

				if (!data.hasKey(key, NBT.TAG_ANY_NUMERIC) || data.getLong(key) + 18000L < world.getTotalWorldTime())
				{
					SoundEvent music;

					if (world.rand.nextInt(2) == 0)
					{
						music = CaveSounds.MUSIC_CAVE;
					}
					else
					{
						music = CaveSounds.MUSIC_UNREST;
					}

					CaveNetworkRegistry.sendTo(new CaveMusicMessage(music), player);
				}

				data.setLong(key, world.getTotalWorldTime());
			}
			else if (CavernAPI.dimension.isEntityInAquaCavern(player))
			{
				NBTTagCompound data = player.getEntityData();
				String key = "AquaCavern" + suffix;

				if (!data.hasKey(key, NBT.TAG_ANY_NUMERIC) || data.getLong(key) + 18000L < world.getTotalWorldTime())
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage(CaveSounds.MUSIC_AQUA), player);
				}

				data.setLong(key, world.getTotalWorldTime());
			}
			else if (CavernAPI.dimension.isEntityInCaveland(player))
			{
				NBTTagCompound data = player.getEntityData();
				String key = "Caveland" + suffix;

				if (!data.hasKey(key, NBT.TAG_ANY_NUMERIC) || data.getLong(key) + 18000L < world.getTotalWorldTime())
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage(CaveSounds.MUSIC_HOPE), player);
				}

				data.setLong(key, world.getTotalWorldTime());
			}
			else if (CavernAPI.dimension.isEntityInIceCavern(player))
			{
				NBTTagCompound data = player.getEntityData();
				String key = "IceCavern" + suffix;

				if (!data.hasKey(key, NBT.TAG_ANY_NUMERIC) || data.getLong(key) + 18000L < world.getTotalWorldTime())
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage(CaveSounds.MUSIC_UNREST), player);
				}

				data.setLong(key, world.getTotalWorldTime());
			}
			else if (CavernAPI.dimension.isEntityInRuinsCavern(player))
			{
				NBTTagCompound data = player.getEntityData();
				String key = "RuinsCavern" + suffix;

				if (!data.hasKey(key, NBT.TAG_ANY_NUMERIC) || data.getLong(key) + 18000L < world.getTotalWorldTime())
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage(CaveSounds.MUSIC_CAVE), player);
				}

				data.setLong(key, world.getTotalWorldTime());
			}
			else if (CavernAPI.dimension.isEntityInCavenia(player))
			{
				NBTTagCompound data = player.getEntityData();
				String key = "Cavenia" + suffix;

				if (!data.hasKey(key, NBT.TAG_ANY_NUMERIC) || data.getLong(key) + 18000L < world.getTotalWorldTime())
				{
					CaveNetworkRegistry.sendTo(new CaveMusicMessage(CaveSounds.MUSIC_UNREST), player);
				}

				data.setLong(key, world.getTotalWorldTime());
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		EntityPlayer player = event.getEntityPlayer();
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		EnumFacing face = event.getFace();
		EnumFacing side = face == null ? EnumFacing.UP : face;
		Vec3d hit = event.getHitVec();
		EnumHand hand = event.getHand();
		ItemStack held = event.getItemStack();

		if (!held.isEmpty())
		{
			Item portal = Items.AIR;

			if (held.getItem() == Items.EMERALD)
			{
				portal = Item.getItemFromBlock(CaveBlocks.CAVERN_PORTAL);
			}
			else if (held.getItem() == CaveItems.CAVE_ITEM)
			{
				switch (ItemCave.EnumType.byItemStack(held))
				{
					case AQUAMARINE:
						portal = Item.getItemFromBlock(CaveBlocks.AQUA_CAVERN_PORTAL);
						break;
					case MINER_ORB:
						portal = Item.getItemFromBlock(CaveBlocks.RUINS_CAVERN_PORTAL);
						break;
					default:
				}
			}
			else
			{
				Block block = Block.getBlockFromItem(held.getItem());

				if (block != null && block instanceof BlockSapling)
				{
					portal = Item.getItemFromBlock(CaveBlocks.CAVELAND_PORTAL);
				}
				else if (block == Blocks.PACKED_ICE)
				{
					portal = Item.getItemFromBlock(CaveBlocks.ICE_CAVERN_PORTAL);
				}
			}

			if (portal != Items.AIR)
			{
				EnumActionResult result = portal.onItemUse(player, world, pos, hand, side, (float)hit.x, (float)hit.y, (float)hit.z);

				if (result == EnumActionResult.SUCCESS)
				{
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BreakEvent event)
	{
		EntityPlayer player = event.getPlayer();

		if (player != null && player instanceof EntityPlayerMP)
		{
			EntityPlayerMP thePlayer = (EntityPlayerMP)player;

			if (CavernAPI.dimension.isEntityInCaves(thePlayer))
			{
				if (GeneralConfig.isMiningPointItem(thePlayer.getHeldItemMainhand()))
				{
					IBlockState state = event.getState();
					int amount = MinerStats.getPointAmount(state);

					if (amount != 0)
					{
						IMinerStats stats = MinerStats.get(thePlayer);

						if (player.inventory.hasItemStack(ItemCave.EnumType.MINER_ORB.getItemStack()))
						{
							if (RANDOM.nextDouble() < 0.3D)
							{
								amount += Math.max(amount / 2, 1);
							}
						}

						stats.addPoint(amount);

						MinerStats.setLastMine(new BlockMeta(state), amount);

						CaveNetworkRegistry.sendTo(new LastMineMessage(MinerStats.lastMine, MinerStats.lastMinePoint), thePlayer);
					}
				}

				if (CavernAPI.dimension.isEntityInIceCavern(thePlayer))
				{
					IBlockState state = event.getState();

					if (!thePlayer.capabilities.isCreativeMode && state != null && state.getBlock() == Blocks.PACKED_ICE)
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
						else if (IceEquipment.isIceEquipment(thePlayer.getHeldItemMainhand()) && RANDOM.nextDouble() < 0.03D || RANDOM.nextDouble() < 0.01D)
						{
							Block.spawnAsEntity(world, pos, new ItemStack(Blocks.PACKED_ICE));
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		float original = event.getOriginalSpeed();
		EntityPlayer player = event.getEntityPlayer();
		ItemStack heldMain = player.getHeldItemMainhand();
		boolean miner = CavernAPI.dimension.isEntityInCaves(player) && CaveUtils.isItemPickaxe(heldMain);

		if (!heldMain.isEmpty() && player.isInsideOfMaterial(Material.WATER))
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
			else if (heldMain.getItem() instanceof IAquaTool)
			{
				IAquaTool tool = (IAquaTool)heldMain.getItem();
				float speed = tool.getAquaBreakSpeed(heldMain, player, event.getPos(), event.getState(), original);

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

			if (boost != 1.0F)
			{
				event.setNewSpeed(event.getNewSpeed() * boost);
			}
		}
	}

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();

		if (CavernAPI.dimension.isEntityInCaves(entity))
		{
			if (entity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)entity;

				if (player.isInsideOfMaterial(Material.WATER))
				{
					IMinerStats stats = MinerStats.get(player);

					if (stats.getRank() >= MinerRank.AQUA_MINER.getRank())
					{
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

							if (player.isCollidedHorizontally && player.isOffsetPositionInLiquid(player.motionX, player.motionY + 0.6000000238418579D - player.posY + posY, player.motionZ))
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
			}
		}

		if (entity instanceof EntityPlayer)
		{
			MagicianStats.get((EntityPlayer)entity).onUpdate();
		}
	}

	@SubscribeEvent
	public void onLivingDrops(LivingDropsEvent event)
	{
		EntityLivingBase entity = event.getEntityLiving();
		World world = entity.world;

		if (CavernAPI.dimension.isEntityInCaves(entity) && world.getGameRules().getBoolean("doMobLoot"))
		{
			double bookChance = 0.001D;
			double elixirChance = 0.01D;

			if (entity instanceof EntityWitch)
			{
				bookChance = 0.7D;
				elixirChance = 1.0D;
			}
			else if (entity instanceof ICavenicMob)
			{
				if (entity.isNonBoss())
				{
					bookChance = 0.1D;
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
					int elixirRank = 1;

					if (RANDOM.nextDouble() < 0.5D)
					{
						++elixirRank;
					}

					if (RANDOM.nextDouble() < 0.125D)
					{
						++elixirRank;
					}

					drops.add(new EntityItem(world, posX, posY, posZ, ItemElixir.EnumType.byMetadata(elixirRank - 1).getItemStack()));
				}
			}
		}
	}

	@SubscribeEvent
	public void onSleepInBed(PlayerSleepInBedEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();

		if (CavernAPI.dimension.isEntityInCaves(player))
		{
			SleepResult result = null;
			NBTTagCompound data = player.getEntityData();
			World world = player.world;

			if (!world.isRemote)
			{
				long worldTime = world.getTotalWorldTime();
				long sleepTime;

				if (data.hasKey("Cavern:SleepTime", NBT.TAG_ANY_NUMERIC))
				{
					sleepTime = data.getLong("Cavern:SleepTime");
				}
				else
				{
					sleepTime = worldTime;

					data.setLong("Cavern:SleepTime", worldTime);
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

				data.setLong("Cavern:SleepTime", world.getTotalWorldTime());
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

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();

		if (IceEquipment.isIceEquipment(stack))
		{
			IIceEquipment equip = IceEquipment.get(stack);

			if (!equip.isHiddenTooltip())
			{
				event.getToolTip().add(Cavern.proxy.translateFormat("tooltip.iceEquipment.charge", equip.getCharge()));
			}
		}
	}
}