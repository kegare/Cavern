package cavern.block;

import java.util.Random;

import com.google.common.cache.LoadingCache;

import cavern.api.CavernAPI;
import cavern.client.gui.GuiRegeneration;
import cavern.config.CavernConfig;
import cavern.core.CaveSounds;
import cavern.core.Cavern;
import cavern.stats.IPortalCache;
import cavern.stats.PortalCache;
import cavern.world.CaveType;
import cavern.world.TeleporterCavern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {}

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
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block)
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			displayGui(world, pos, state, player, hand, heldItem, side);
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	public void displayGui(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side)
	{
		FMLClientHandler.instance().showGuiScreen(new GuiRegeneration(true, false, false));
	}

	public int getType()
	{
		return CaveType.CAVERN;
	}

	public int getDimension()
	{
		return CavernConfig.dimensionId;
	}

	public boolean isEntityInCave(Entity entity)
	{
		return CavernAPI.dimension.isEntityInCavern(entity);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity)
	{
		if (!world.isRemote && entity.isEntityAlive())
		{
			if (entity.timeUntilPortal <= 0)
			{
				IPortalCache cache = PortalCache.get(entity);
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				int dimOld = entity.dimension;
				int dimNew = isEntityInCave(entity) ? cache.getLastDim(getType()) : getDimension();

				if (dimOld == dimNew)
				{
					dimOld = getDimension();
					dimNew = 0;
				}

				WorldServer worldOld = server.worldServerForDimension(dimOld);
				WorldServer worldNew = server.worldServerForDimension(dimNew);

				if (worldOld == null || worldNew == null)
				{
					return;
				}

				Teleporter teleporter = new TeleporterCavern(worldNew, this);
				BlockPos prevPos = entity.getPosition();

				entity.timeUntilPortal = entity.getPortalCooldown();

				if (entity instanceof EntityPlayerMP)
				{
					EntityPlayerMP player = (EntityPlayerMP)entity;

					if (!player.isSneaking() && !player.isPotionActive(MobEffects.BLINDNESS))
					{
						double x = player.posX;
						double y = player.posY + player.getEyeHeight();
						double z = player.posZ;

						worldOld.playSound(player, x, y, z, CaveSounds.CAVE_PORTAL, SoundCategory.BLOCKS, 0.5F, 1.0F);

						server.getPlayerList().transferPlayerToDimension(player, dimNew, teleporter);

						x = player.posX;
						y = player.posY + player.getEyeHeight();
						z = player.posZ;

						worldNew.playSound(null, x, y, z, CaveSounds.CAVE_PORTAL, SoundCategory.BLOCKS, 0.75F, 1.0F);

						cache.setLastDim(getType(), dimOld);
						cache.setLastPos(getType(), dimOld, prevPos);
					}
				}
				else
				{
					double x = entity.posX;
					double y = entity.posY + entity.getEyeHeight();
					double z = entity.posZ;

					worldOld.playSound(null, x, y, z, CaveSounds.CAVE_PORTAL, SoundCategory.BLOCKS, 0.25F, 1.15F);

					server.getPlayerList().transferEntityToWorld(entity, dimOld, worldOld, worldNew, teleporter);

					Entity target = EntityList.createEntityByName(EntityList.getEntityString(entity), worldNew);

					if (target != null)
					{
						NBTTagCompound nbt = new NBTTagCompound();

						entity.writeToNBT(nbt);
						nbt.removeTag("Dimension");

						target.readFromNBT(nbt);

						boolean force = target.forceSpawn;

						target.forceSpawn = true;

						worldNew.spawnEntityInWorld(target);
						worldNew.updateEntityWithOptionalForce(target, false);

						x = target.posX;
						y = target.posY + target.getEyeHeight();
						z = target.posZ;

						worldNew.playSound(null, x, y, z, CaveSounds.CAVE_PORTAL, SoundCategory.BLOCKS, 0.5F, 1.15F);

						target.forceSpawn = force;

						cache.setLastDim(getType(), dimOld);
						cache.setLastPos(getType(), dimOld, prevPos);
					}

					entity.setDead();

					worldOld.resetUpdateEntityTick();
					worldNew.resetUpdateEntityTick();
				}
			}
			else
			{
				entity.timeUntilPortal = entity.getPortalCooldown();
			}
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
	public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {}

	@Override
	public ItemStack getItem(World world, BlockPos pos, IBlockState state)
	{
		return new ItemStack(this);
	}

	public class Size
	{
		private final World world;
		private final EnumFacing.Axis axis;
		private final EnumFacing rightDir;
		private final EnumFacing leftDir;
		private int portalBlockCount = 0;
		private BlockPos bottomLeft;
		private int height;
		private int width;

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

				if (!isEmptyBlock(world.getBlockState(pos1)) || world.getBlockState(pos1.down()).getBlock() != Blocks.MOSSY_COBBLESTONE)
				{
					break;
				}
			}

			Block block = world.getBlockState(pos.offset(face, i)).getBlock();

			return block == Blocks.MOSSY_COBBLESTONE ? i : 0;
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
					Block block = state.getBlock();

					if (!isEmptyBlock(state))
					{
						break outside;
					}

					if (block == BlockPortalCavern.this)
					{
						++portalBlockCount;
					}

					if (i == 0)
					{
						block = world.getBlockState(pos.offset(leftDir)).getBlock();

						if (block != Blocks.MOSSY_COBBLESTONE)
						{
							break outside;
						}
					}
					else if (i == width - 1)
					{
						block = world.getBlockState(pos.offset(rightDir)).getBlock();

						if (block != Blocks.MOSSY_COBBLESTONE)
						{
							break outside;
						}
					}
				}
			}

			for (i = 0; i < width; ++i)
			{
				if (world.getBlockState(bottomLeft.offset(rightDir, i).up(height)).getBlock() != Blocks.MOSSY_COBBLESTONE)
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