package cavern.handler;

import java.util.Random;

import com.google.common.base.Strings;

import cavern.api.CavernAPI;
import cavern.api.IIceEquipment;
import cavern.api.IMinerStats;
import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.block.bonus.WeightedItem;
import cavern.config.GeneralConfig;
import cavern.core.CaveAchievements;
import cavern.core.CaveSounds;
import cavern.core.Cavern;
import cavern.item.CaveItems;
import cavern.item.IAquaTool;
import cavern.item.IceEquipment;
import cavern.item.ItemCave;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.CaveMusicMessage;
import cavern.network.client.LastMineMessage;
import cavern.stats.MinerRank;
import cavern.stats.MinerStats;
import cavern.util.BlockMeta;
import cavern.util.CaveUtils;
import cavern.world.WorldProviderAquaCavern;
import cavern.world.WorldProviderCaveland;
import cavern.world.WorldProviderCavern;
import cavern.world.WorldProviderIceCavern;
import cavern.world.WorldProviderRuinsCavern;
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
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemSmeltedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class CaveEventHooks
{
	protected static final Random RANDOM = new Random();

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if (event.getEntity() instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.getEntity();

			MinerStats.get(player).adjustData();
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;

			MinerStats.get(player).adjustData();
		}
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			WorldServer world = player.getServerWorld();
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

		if (held != null)
		{
			Item portal = null;

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

			if (portal != null)
			{
				EnumActionResult result = portal.onItemUse(held, player, world, pos, hand, side, (float)hit.xCoord, (float)hit.yCoord, (float)hit.zCoord);

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

		if (heldMain != null && player.isInsideOfMaterial(Material.WATER))
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
			float boost = MinerRank.getRank(rank).getBoost();

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

						if (!player.capabilities.isFlying || EnchantmentHelper.getDepthStriderModifier(player) <= 1)
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
	}

	@SubscribeEvent
	public void onItemPickup(ItemPickupEvent event)
	{
		EntityPlayer player = event.player;
		EntityItem entityItem = event.pickedUp;
		World world = entityItem.worldObj;

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

				if (itemstack != null)
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
	public void onItemSmelted(ItemSmeltedEvent event)
	{
		EntityPlayer player = event.player;
		ItemStack itemstack = event.smelting;
		World world = player.worldObj;

		if (!world.isRemote)
		{
			if (itemstack != null)
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
	public void onWorldUnload(WorldEvent.Unload event)
	{
		World world = event.getWorld();
		int dim = world.provider.getDimension();

		if (!world.isRemote)
		{
			if (CavernAPI.dimension.isCavern(dim))
			{
				WorldProviderCavern.saveHandler.writeToFile();
			}
			else if (CavernAPI.dimension.isAquaCavern(dim))
			{
				WorldProviderAquaCavern.saveHandler.writeToFile();
			}
			else if (CavernAPI.dimension.isCaveland(dim))
			{
				WorldProviderCaveland.saveHandler.writeToFile();
			}
			else if (CavernAPI.dimension.isIceCavern(dim))
			{
				WorldProviderIceCavern.saveHandler.writeToFile();
			}
			else if (CavernAPI.dimension.isRuinsCavern(dim))
			{
				WorldProviderRuinsCavern.saveHandler.writeToFile();
			}
		}
	}
}