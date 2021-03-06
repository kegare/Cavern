package cavern.block;

import java.util.Random;

import com.google.common.cache.LoadingCache;

import cavern.api.CavernAPI;
import cavern.api.IPortalCache;
import cavern.client.gui.GuiRegeneration;
import cavern.config.GeneralConfig;
import cavern.core.CaveSounds;
import cavern.core.Cavern;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.RegenerationGuiMessage;
import cavern.stats.PortalCache;
import cavern.util.CaveUtils;
import cavern.world.CaveType;
import cavern.world.WorldCachedData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class BlockPortalCavern extends BlockPortal
{
	public BlockPortalCavern()
	{
		super();
		this.setUnlocalizedName("portal.cavern");
		this.setSoundType(SoundType.GLASS);
		this.setTickRandomly(false);
		this.setBlockUnbreakable();
		this.disableStats();
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {}

	@Override
	public boolean trySpawnPortal(World world, BlockPos pos)
	{
		Size size = new Size(world, pos, EnumFacing.Axis.X);

		if (size.isValid() && size.portalBlockCount == 0)
		{
			size.placePortalBlocks();

			return true;
		}
		else
		{
			Size size1 = new Size(world, pos, EnumFacing.Axis.Z);

			if (size1.isValid() && size1.portalBlockCount == 0)
			{
				size1.placePortalBlocks();

				return true;
			}
			else return false;
		}
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos pos2)
	{
		EnumFacing.Axis axis = state.getValue(AXIS);
		Size size;

		if (axis == EnumFacing.Axis.X)
		{
			size = new Size(world, pos, EnumFacing.Axis.X);

			if (!size.isValid() || size.portalBlockCount < size.width * size.height)
			{
				world.setBlockToAir(pos);
			}
		}
		else if (axis == EnumFacing.Axis.Z)
		{
			size = new Size(world, pos, EnumFacing.Axis.Z);

			if (!size.isValid() || size.portalBlockCount < size.width * size.height)
			{
				world.setBlockToAir(pos);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!GeneralConfig.portalMenu)
		{
			return true;
		}

		if (Cavern.proxy.isSinglePlayer() && world.isRemote)
		{
			displayGui(world, pos, state, player, hand, side);
		}
		else if (player instanceof EntityPlayerMP)
		{
			EntityPlayerMP playerMP = (EntityPlayerMP)player;

			if (playerMP.mcServer.getPlayerList().canSendCommands(playerMP.getGameProfile()))
			{
				CaveNetworkRegistry.sendTo(new RegenerationGuiMessage(RegenerationGuiMessage.EnumType.OPEN), playerMP);
			}
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	public void displayGui(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side)
	{
		GuiRegeneration regeneration = new GuiRegeneration();
		regeneration.cavern = true;

		FMLClientHandler.instance().showGuiScreen(regeneration);
	}

	public DimensionType getDimension()
	{
		return CaveType.DIM_CAVERN;
	}

	public boolean isEntityInCave(Entity entity)
	{
		return CavernAPI.dimension.isEntityInCavern(entity);
	}

	public boolean isDimensionDisabled()
	{
		return false;
	}

	public boolean isTriggerItem(ItemStack stack)
	{
		if (!stack.isEmpty() && stack.getItem() == Items.EMERALD)
		{
			return true;
		}

		for (ItemStack dictStack : OreDictionary.getOres("gemEmerald", false))
		{
			if (CaveUtils.isItemEqual(stack, dictStack))
			{
				return true;
			}
		}

		return false;
	}

	public Teleporter getTeleporter(WorldServer world)
	{
		return WorldCachedData.get(world).getPortalTeleporter(this);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		if (world.isRemote || isDimensionDisabled())
		{
			return;
		}

		if (entity.isDead || entity.isRiding() || entity.isBeingRidden() || !entity.isNonBoss() || entity instanceof IProjectile)
		{
			return;
		}

		if (entity.timeUntilPortal <= 0)
		{
			ResourceLocation key = getRegistryName();
			IPortalCache cache = PortalCache.get(entity);
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			DimensionType dimOld = world.provider.getDimensionType();
			DimensionType dimNew = isEntityInCave(entity) ? cache.getLastDim(key) : getDimension();
			WorldServer worldOld = server.getWorld(dimOld.getId());
			WorldServer worldNew = server.getWorld(dimNew.getId());
			Teleporter teleporter = getTeleporter(worldNew);
			BlockPos prevPos = entity.getPosition();

			entity.timeUntilPortal = entity.getPortalCooldown();

			if (entity instanceof EntityPlayerMP)
			{
				EntityPlayerMP player = (EntityPlayerMP)entity;

				if (!player.isSneaking() && !player.isPotionActive(MobEffects.BLINDNESS))
				{
					if (GeneralConfig.cavernEscapeMission)
					{
						boolean fromCave = CavernAPI.dimension.isCaves(dimOld);
						boolean toCave = CavernAPI.dimension.isCaves(dimNew);

						if (fromCave && !toCave && !GeneralConfig.canEscapeFromCaves(player))
						{
							player.sendStatusMessage(new TextComponentTranslation("cavern.escapeMission.bad.message"), true);

							return;
						}
					}

					double x = player.posX;
					double y = player.posY + player.getEyeHeight();
					double z = player.posZ;

					worldOld.playSound(player, x, y, z, CaveSounds.CAVE_PORTAL, SoundCategory.BLOCKS, 0.5F, 1.0F);

					CaveUtils.transferPlayerToDimension(player, dimNew, teleporter);

					x = player.posX;
					y = player.posY + player.getEyeHeight();
					z = player.posZ;

					worldNew.playSound(null, x, y, z, CaveSounds.CAVE_PORTAL, SoundCategory.BLOCKS, 0.75F, 1.0F);

					cache.setLastDim(key, dimOld);
					cache.setLastPos(key, dimOld, prevPos);
				}
			}
			else
			{
				double x = entity.posX;
				double y = entity.posY + entity.getEyeHeight();
				double z = entity.posZ;

				worldOld.playSound(null, x, y, z, CaveSounds.CAVE_PORTAL, SoundCategory.BLOCKS, 0.25F, 1.15F);

				entity.dimension = dimNew.getId();
				world.removeEntityDangerously(entity);

				entity.isDead = false;

				server.getPlayerList().transferEntityToWorld(entity, dimOld.getId(), worldOld, worldNew, teleporter);

				x = entity.posX;
				y = entity.posY + entity.getEyeHeight();
				z = entity.posZ;

				worldNew.playSound(null, x, y, z, CaveSounds.CAVE_PORTAL, SoundCategory.BLOCKS, 0.5F, 1.15F);

				cache.setLastDim(key, dimOld);
				cache.setLastPos(key, dimOld, prevPos);
			}
		}
		else
		{
			entity.timeUntilPortal = entity.getPortalCooldown();
		}
	}

	@Override
	public PatternHelper createPatternHelper(World world, BlockPos pos)
	{
		EnumFacing.Axis axis = EnumFacing.Axis.Z;
		Size size = new Size(world, pos, EnumFacing.Axis.X);
		LoadingCache<BlockPos, BlockWorldState> cache = BlockPattern.createLoadingCache(world, true);

		if (!size.isValid())
		{
			axis = EnumFacing.Axis.X;
			size = new Size(world, pos, EnumFacing.Axis.Z);
		}

		if (!size.isValid())
		{
			return new PatternHelper(pos, EnumFacing.NORTH, EnumFacing.UP, cache, 1, 1, 1);
		}
		else
		{
			int[] values = new int[EnumFacing.AxisDirection.values().length];
			EnumFacing facing = size.rightDir.rotateYCCW();
			BlockPos blockpos = size.bottomLeft.up(size.getHeight() - 1);

			for (EnumFacing.AxisDirection direction : EnumFacing.AxisDirection.values())
			{
				PatternHelper pattern = new PatternHelper(facing.getAxisDirection() == direction ? blockpos : blockpos.offset(size.rightDir, size.getWidth() - 1), EnumFacing.getFacingFromAxis(direction, axis), EnumFacing.UP, cache, size.getWidth(), size.getHeight(), 1);

				for (int i = 0; i < size.getWidth(); ++i)
				{
					for (int j = 0; j < size.getHeight(); ++j)
					{
						BlockWorldState state = pattern.translateOffset(i, j, 1);

						if (state.getBlockState() != null && state.getBlockState().getMaterial() != Material.AIR)
						{
							++values[direction.ordinal()];
						}
					}
				}
			}

			EnumFacing.AxisDirection axis1 = EnumFacing.AxisDirection.POSITIVE;

			for (EnumFacing.AxisDirection direction : EnumFacing.AxisDirection.values())
			{
				if (values[direction.ordinal()] < values[axis1.ordinal()])
				{
					axis1 = direction;
				}
			}

			return new PatternHelper(facing.getAxisDirection() == axis1 ? blockpos : blockpos.offset(size.rightDir, size.getWidth() - 1), EnumFacing.getFacingFromAxis(axis1, axis), EnumFacing.UP, cache, size.getWidth(), size.getHeight(), 1);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {}

	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state)
	{
		return new ItemStack(this);
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		if (!isDimensionDisabled())
		{
			super.getSubBlocks(tab, list);
		}
	}

	public class Size
	{
		private final World world;
		private final EnumFacing.Axis axis;
		private final EnumFacing rightDir;
		private final EnumFacing leftDir;

		private int portalBlockCount;
		private BlockPos bottomLeft;
		private int height;
		private int width;
		private IBlockState portalFrame;

		public Size(World world, BlockPos pos, EnumFacing.Axis axis)
		{
			this.world = world;
			this.axis = axis;

			if (axis == EnumFacing.Axis.X)
			{
				this.leftDir = EnumFacing.EAST;
				this.rightDir = EnumFacing.WEST;
			}
			else
			{
				this.leftDir = EnumFacing.NORTH;
				this.rightDir = EnumFacing.SOUTH;
			}

			for (BlockPos blockpos = pos; pos.getY() > blockpos.getY() - 21 && pos.getY() > 0 && isEmptyBlock(world.getBlockState(pos.down())); pos = pos.down())
			{
				;
			}

			int i = getDistanceUntilEdge(pos, leftDir) - 1;

			if (i >= 0)
			{
				this.bottomLeft = pos.offset(leftDir, i);
				this.width = getDistanceUntilEdge(bottomLeft, rightDir);

				if (width < 2 || width > 21)
				{
					this.bottomLeft = null;
					this.width = 0;
				}
			}

			if (bottomLeft != null)
			{
				this.height = calculatePortalHeight();
			}
		}

		protected int getDistanceUntilEdge(BlockPos pos, EnumFacing face)
		{
			int i;

			for (i = 0; i < 22; ++i)
			{
				BlockPos pos1 = pos.offset(face, i);

				if (!isEmptyBlock(world.getBlockState(pos1)) || !isFrameBlock(world.getBlockState(pos1.down())))
				{
					break;
				}
			}

			return isFrameBlock(world.getBlockState(pos.offset(face, i))) ? i : 0;
		}

		public int getHeight()
		{
			return height;
		}

		public int getWidth()
		{
			return width;
		}

		protected int calculatePortalHeight()
		{
			int i;

			outside: for (height = 0; height < 21; ++height)
			{
				for (i = 0; i < width; ++i)
				{
					BlockPos pos = bottomLeft.offset(rightDir, i).up(height);
					IBlockState state = world.getBlockState(pos);

					if (!isEmptyBlock(state))
					{
						break outside;
					}

					if (state.getBlock() == BlockPortalCavern.this)
					{
						++portalBlockCount;
					}

					if (i == 0)
					{
						if (!isFrameBlock(world.getBlockState(pos.offset(leftDir))))
						{
							break outside;
						}
					}
					else if (i == width - 1)
					{
						if (!isFrameBlock(world.getBlockState(pos.offset(rightDir))))
						{
							break outside;
						}
					}
				}
			}

			for (i = 0; i < width; ++i)
			{
				if (!isFrameBlock(world.getBlockState(bottomLeft.offset(rightDir, i).up(height))))
				{
					height = 0;

					break;
				}
			}

			if (height <= 21 && height >= 3)
			{
				return height;
			}
			else
			{
				bottomLeft = null;
				width = 0;
				height = 0;

				return 0;
			}
		}

		protected boolean isEmptyBlock(IBlockState state)
		{
			return state.getMaterial() == Material.AIR || state.getBlock() == BlockPortalCavern.this;
		}

		protected boolean isFrameBlock(IBlockState state)
		{
			if (portalFrame == null)
			{
				if (state.getBlock() == Blocks.MOSSY_COBBLESTONE)
				{
					portalFrame = Blocks.MOSSY_COBBLESTONE.getDefaultState();
				}
				else if (state.getBlock() == Blocks.STONEBRICK && state.getBlock().getMetaFromState(state) == BlockStoneBrick.MOSSY_META)
				{
					portalFrame = Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
				}
			}

			return CaveUtils.areBlockStatesEqual(portalFrame, state);
		}

		public boolean isValid()
		{
			return bottomLeft != null && width >= 2 && width <= 21 && height >= 3 && height <= 21;
		}

		public void placePortalBlocks()
		{
			for (int i = 0; i < width; ++i)
			{
				BlockPos pos = bottomLeft.offset(rightDir, i);

				for (int j = 0; j < height; ++j)
				{
					world.setBlockState(pos.up(j), getDefaultState().withProperty(AXIS, axis), 2);
				}
			}
		}
	}
}