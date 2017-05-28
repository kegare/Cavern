package cavern.handler;

import java.util.Random;

import com.google.common.base.Strings;

import cavern.api.CavernAPI;
import cavern.api.IHunterStats;
import cavern.api.IIceEquipment;
import cavern.api.IMagicianStats;
import cavern.api.IMinerStats;
import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.config.GeneralConfig;
import cavern.core.CaveAchievements;
import cavern.core.CaveSounds;
import cavern.core.Cavern;
import cavern.item.CaveItems;
import cavern.item.IAquaTool;
import cavern.item.IceEquipment;
import cavern.item.ItemCave;
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
import cavern.util.WeightedItem;
import cavern.world.TeleporterRepatriation;
import cavern.world.WorldProviderIceCavern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;

public class CaveEventHooks
{
	protected static final Random RANDOM = new Random();

	private static final String NBT_LOST_ORB = "Cavern:LostOrb";

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

			if (GeneralConfig.cavernEscapeMission && !GeneralConfig.canEscapeFromCaves(player))
			{
				boolean fromCave = CavernAPI.dimension.isCaves(event.fromDim);
				boolean toCave = CavernAPI.dimension.isCaves(event.toDim);

				if (fromCave && !toCave)
				{
					MinecraftServer server = player.mcServer;
					WorldServer worldNew = server.worldServerForDimension(event.fromDim);

					if (worldNew != null)
					{
						player.timeUntilPortal = player.getPortalCooldown();

						server.getPlayerList().transferPlayerToDimension(player, event.fromDim, new TeleporterRepatriation(worldNew));

						return;
					}
				}

				if (!fromCave && toCave)
				{
					ItemStack stack = CaveItems.getBookEscapeMission();

					if (!player.inventory.hasItemStack(stack))
					{
						player.inventory.addItemStackToInventory(stack);
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

				player.addStat(CaveAchievements.CAVERN);
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

				player.addStat(CaveAchievements.AQUA_CAVERN);
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

				player.addStat(CaveAchievements.CAVELAND);
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

				player.addStat(CaveAchievements.ICE_CAVERN);
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

				player.addStat(CaveAchievements.RUINS_CAVERN);
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

				player.addStat(CaveAchievements.CAVENIA);
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
				EnumActionResult result = portal.onItemUse(player, world, pos, hand, side, (float)hit.xCoord, (float)hit.yCoord, (float)hit.zCoord);

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
							WeightedItem randomItem = WeightedRandom.getRandomItem(RANDOM, WorldProviderIceCavern.HIBERNATE_ITEMS);

							Block.spawnAsEntity(world, pos, randomItem.getItem());
						}
						else if (RANDOM.nextDouble() < 0.0085D)
						{
							WeightedItem randomItem = WeightedRandom.getRandomItem(RANDOM, BlockCave.RANDOMITE_ITEMS);

							Block.spawnAsEntity(world, pos, randomItem.getItem());
						}
						else if (IceEquipment.isIceEquipment(thePlayer.getHeldItemMainhand()) && RANDOM.nextDouble() < 0.03D || RANDOM.nextDouble() < 0.01D)
						{
							Block.spawnAsEntity(world, pos, new ItemStack(Blocks.PACKED_ICE));
						}
						else if (RANDOM.nextDouble() < 0.01D)
						{
							WeightedItem randomItem = WeightedRandom.getRandomItem(RANDOM, ItemMagicalBook.MAGIC_ITEMS);
							ItemStack stack = randomItem.getItem();

							if (stack.getItem() instanceof ItemMagicalBook)
							{
								ItemMagicalBook magicalBook = (ItemMagicalBook)stack.getItem();
								int rank = MagicianStats.get(thePlayer).getRank();

								if (magicalBook.getMagicLevel(stack) > rank + 1)
								{
									magicalBook.setMagicLevel(stack, 1);
								}
							}

							Block.spawnAsEntity(world, pos, stack);
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
						if (!player.canBreatheUnderwater() && player.ticksExisted % 20 == 0)
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

				if (player.ticksExisted % 100 == 0 && player.getEntityData().hasKey(NBT_LOST_ORB))
				{
					player.addStat(CaveAchievements.LOST_ORB);

					player.getEntityData().removeTag(NBT_LOST_ORB);
				}
			}
		}

		if (entity instanceof EntityPlayer)
		{
			MagicianStats.get((EntityPlayer)entity).onUpdate();
		}
	}

	@SubscribeEvent
	public void onItemPickup(ItemPickupEvent event)
	{
		EntityPlayer player = event.player;
		EntityItem entityItem = event.pickedUp;
		World world = entityItem.world;

		if (!world.isRemote)
		{
			EntityPlayer thrower = null;

			if (!Strings.isNullOrEmpty(entityItem.getThrower()))
			{
				thrower = world.getPlayerEntityByName(entityItem.getThrower());
			}

			if (thrower == null)
			{
				ItemStack itemstack = entityItem.getEntityItem();

				if (!itemstack.isEmpty())
				{
					if (itemstack.getItem() == CaveItems.CAVE_ITEM)
					{
						switch (ItemCave.EnumType.byItemStack(itemstack))
						{
							case AQUAMARINE:
								player.addStat(CaveAchievements.AQUAMARINE);
								break;
							case HEXCITE:
								player.addStat(CaveAchievements.HEXCITE);
								break;
							case MINER_ORB:
								player.addStat(CaveAchievements.MINER_ORB);
								break;
							default:
						}
					}
					else if (itemstack.getItem() == Item.getItemFromBlock(CaveBlocks.PERVERTED_LOG))
					{
						player.addStat(AchievementList.MINE_WOOD);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent event)
	{
		EntityPlayer player = event.player;
		ItemStack itemstack = event.crafting;
		World world = player.world;

		if (!world.isRemote && !itemstack.isEmpty())
		{
			if (IceEquipment.isIceEquipment(itemstack))
			{
				int charge = IceEquipment.get(itemstack).getCharge();

				if (charge > 0)
				{
					player.addStat(CaveAchievements.ICE_CHARGE);
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemSmelted(ItemSmeltedEvent event)
	{
		EntityPlayer player = event.player;
		ItemStack itemstack = event.smelting;
		World world = player.world;

		if (!world.isRemote && !itemstack.isEmpty())
		{
			if (itemstack.getItem() == CaveItems.CAVE_ITEM)
			{
				switch (ItemCave.EnumType.byItemStack(itemstack))
				{
					case MAGNITE_INGOT:
						player.addStat(CaveAchievements.MAGNITE);
						break;
					default:
				}
			}
		}
	}

	@SubscribeEvent
	public void onItemTooltip(ItemTooltipEvent event)
	{
		ItemStack itemstack = event.getItemStack();

		if (IceEquipment.isIceEquipment(itemstack))
		{
			IIceEquipment equip = IceEquipment.get(itemstack);

			event.getToolTip().add(Cavern.proxy.translateFormat("tooltip.iceEquipment.charge", equip.getCharge()));
		}
	}

	@SubscribeEvent
	public void onAchievement(AchievementEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		Achievement achievement = event.getAchievement();

		if (achievement == CaveAchievements.RUINS_CAVERN && !player.hasAchievement(CaveAchievements.LOST_ORB))
		{
			player.getEntityData().setBoolean(NBT_LOST_ORB, true);
		}
	}
}