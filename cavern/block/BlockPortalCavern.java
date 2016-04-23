package cavern.block;

import java.util.Random;

import com.google.common.cache.LoadingCache;

import cavern.api.CavernAPI;
import cavern.client.gui.GuiRegeneration;
import cavern.config.CavernConfig;
import cavern.core.CaveSounds;
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
		this.setStepSound(SoundType.GLASS);
		this.setTickRandomly(false);
		this.setBlockUnbreakable();
		this.disableStats();
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {}

	@Override
	public boolean func_176548_d(World worldIn, BlockPos pos)
	{
		Size size = new Size(worldIn, pos, EnumFacing.Axis.X);

		if (size.func_150860_b() && size.field_150864_e == 0)
		{
			size.func_150859_c();

			return true;
		}
		else
		{
			Size size1 = new Size(worldIn, pos, EnumFacing.Axis.Z);

			if (size1.func_150860_b() && size1.field_150864_e == 0)
			{
				size1.func_150859_c();

				return true;
			}
			else return false;
		}
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		EnumFacing.Axis axis = state.getValue(AXIS);
		Size size;

		if (axis == EnumFacing.Axis.X)
		{
			size = new Size(worldIn, pos, EnumFacing.Axis.X);

			if (!size.func_150860_b() || size.field_150864_e < size.field_150868_h * size.field_150862_g)
			{
				worldIn.setBlockState(pos, Blocks.air.getDefaultState());
			}
		}
		else if (axis == EnumFacing.Axis.Z)
		{
			size = new Size(worldIn, pos, EnumFacing.Axis.Z);

			if (!size.func_150860_b() || size.field_150864_e < size.field_150868_h * size.field_150862_g)
			{
				worldIn.setBlockState(pos, Blocks.air.getDefaultState());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			FMLClientHandler.instance().showGuiScreen(new GuiRegeneration(true, false, false));
		}

		return true;
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

				entity.timeUntilPortal = entity.getPortalCooldown();

				if (entity instanceof EntityPlayerMP)
				{
					EntityPlayerMP player = (EntityPlayerMP)entity;

					if (!player.isSneaking() && !player.isPotionActive(MobEffects.blindness))
					{
						double x = player.posX;
						double y = player.posY + player.getEyeHeight();
						double z = player.posZ;

						worldOld.playSound(player, x, y, z, CaveSounds.cave_portal, SoundCategory.BLOCKS, 0.5F, 1.0F);

						server.getPlayerList().transferPlayerToDimension(player, dimNew, teleporter);

						x = player.posX;
						y = player.posY + player.getEyeHeight();
						z = player.posZ;

						worldNew.playSound(null, x, y, z, CaveSounds.cave_portal, SoundCategory.BLOCKS, 0.75F, 1.0F);

						cache.setLastDim(getType(), dimOld);
						cache.setLastPos(getType(), dimOld, pos);
					}
				}
				else
				{
					double x = entity.posX;
					double y = entity.posY + entity.getEyeHeight();
					double z = entity.posZ;

					worldOld.playSound(null, x, y, z, CaveSounds.cave_portal, SoundCategory.BLOCKS, 0.25F, 1.15F);

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

						worldNew.playSound(null, x, y, z, CaveSounds.cave_portal, SoundCategory.BLOCKS, 0.5F, 1.15F);

						target.forceSpawn = force;

						cache.setLastDim(getType(), dimOld);
						cache.setLastPos(getType(), dimOld, pos);
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
	public PatternHelper func_181089_f(World world, BlockPos pos)
	{
		EnumFacing.Axis axis = EnumFacing.Axis.Z;
		Size size = new Size(world, pos, EnumFacing.Axis.X);
		LoadingCache<BlockPos, BlockWorldState> cache = BlockPattern.func_181627_a(world, true);

		if (!size.func_150860_b())
		{
			axis = EnumFacing.Axis.X;
			size = new Size(world, pos, EnumFacing.Axis.Z);
		}

		if (!size.func_150860_b())
		{
			return new PatternHelper(pos, EnumFacing.NORTH, EnumFacing.UP, cache, 1, 1, 1);
		}
		else
		{
			int[] values = new int[EnumFacing.AxisDirection.values().length];
			EnumFacing facing = size.field_150866_c.rotateYCCW();
			BlockPos blockpos = size.field_150861_f.up(size.func_181100_a() - 1);

			for (EnumFacing.AxisDirection direction : EnumFacing.AxisDirection.values())
			{
				PatternHelper pattern = new PatternHelper(facing.getAxisDirection() == direction ? blockpos : blockpos.offset(size.field_150866_c, size.func_181101_b() - 1), EnumFacing.getFacingFromAxis(direction, axis), EnumFacing.UP, cache, size.func_181101_b(), size.func_181100_a(), 1);

				for (int i = 0; i < size.func_181101_b(); ++i)
				{
					for (int j = 0; j < size.func_181100_a(); ++j)
					{
						BlockWorldState blockworldstate = pattern.translateOffset(i, j, 1);

						if (blockworldstate.getBlockState() != null && blockworldstate.getBlockState().getMaterial() != Material.air)
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

			return new PatternHelper(facing.getAxisDirection() == axis1 ? blockpos : blockpos.offset(size.field_150866_c, size.func_181101_b() - 1), EnumFacing.getFacingFromAxis(axis1, axis), EnumFacing.UP, cache, size.func_181101_b(), size.func_181100_a(), 1);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(IBlockState state, World worldIn, BlockPos pos, Random rand) {}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
	{
		return new ItemStack(this);
	}

	public class Size
	{
		private final World world;
		private final EnumFacing.Axis axis;
		private final EnumFacing field_150866_c;
		private final EnumFacing field_150863_d;
		private int field_150864_e = 0;
		private BlockPos field_150861_f;
		private int field_150862_g;
		private int field_150868_h;

		public Size(World worldIn, BlockPos pos, EnumFacing.Axis axis)
		{
			this.world = worldIn;
			this.axis = axis;

			if (axis == EnumFacing.Axis.X)
			{
				this.field_150863_d = EnumFacing.EAST;
				this.field_150866_c = EnumFacing.WEST;
			}
			else
			{
				this.field_150863_d = EnumFacing.NORTH;
				this.field_150866_c = EnumFacing.SOUTH;
			}

			for (BlockPos blockpos1 = pos; pos.getY() > blockpos1.getY() - 21 && pos.getY() > 0 && func_150857_a(worldIn.getBlockState(pos.down())); pos = pos.down())
			{
				;
			}

			int i = func_180120_a(pos, field_150863_d) - 1;

			if (i >= 0)
			{
				this.field_150861_f = pos.offset(field_150863_d, i);
				this.field_150868_h = func_180120_a(field_150861_f, field_150866_c);

				if (field_150868_h < 2 || field_150868_h > 21)
				{
					this.field_150861_f = null;
					this.field_150868_h = 0;
				}
			}

			if (field_150861_f != null)
			{
				this.field_150862_g = func_150858_a();
			}
		}

		protected int func_180120_a(BlockPos pos, EnumFacing face)
		{
			int i;

			for (i = 0; i < 22; ++i)
			{
				BlockPos pos1 = pos.offset(face, i);

				if (!func_150857_a(world.getBlockState(pos1)) || world.getBlockState(pos1.down()).getBlock() != Blocks.mossy_cobblestone)
				{
					break;
				}
			}

			Block block = world.getBlockState(pos.offset(face, i)).getBlock();

			return block == Blocks.mossy_cobblestone ? i : 0;
		}

		public int func_181100_a()
		{
			return field_150862_g;
		}

		public int func_181101_b()
		{
			return field_150868_h;
		}

		protected int func_150858_a()
		{
			int i;

			outside: for (field_150862_g = 0; field_150862_g < 21; ++field_150862_g)
			{
				for (i = 0; i < field_150868_h; ++i)
				{
					BlockPos pos = field_150861_f.offset(field_150866_c, i).up(field_150862_g);
					IBlockState state = world.getBlockState(pos);
					Block block = state.getBlock();

					if (!func_150857_a(state))
					{
						break outside;
					}

					if (block == BlockPortalCavern.this)
					{
						++field_150864_e;
					}

					if (i == 0)
					{
						block = world.getBlockState(pos.offset(field_150863_d)).getBlock();

						if (block != Blocks.mossy_cobblestone)
						{
							break outside;
						}
					}
					else if (i == field_150868_h - 1)
					{
						block = world.getBlockState(pos.offset(field_150866_c)).getBlock();

						if (block != Blocks.mossy_cobblestone)
						{
							break outside;
						}
					}
				}
			}

			for (i = 0; i < field_150868_h; ++i)
			{
				if (world.getBlockState(field_150861_f.offset(field_150866_c, i).up(field_150862_g)).getBlock() != Blocks.mossy_cobblestone)
				{
					field_150862_g = 0;
					break;
				}
			}

			if (field_150862_g <= 21 && field_150862_g >= 3)
			{
				return field_150862_g;
			}
			else
			{
				field_150861_f = null;
				field_150868_h = 0;
				field_150862_g = 0;

				return 0;
			}
		}

		protected boolean func_150857_a(IBlockState state)
		{
			return state.getMaterial() == Material.air || state.getBlock() == BlockPortalCavern.this;
		}

		public boolean func_150860_b()
		{
			return field_150861_f != null && field_150868_h >= 2 && field_150868_h <= 21 && field_150862_g >= 3 && field_150862_g <= 21;
		}

		public void func_150859_c()
		{
			for (int i = 0; i < field_150868_h; ++i)
			{
				BlockPos pos = field_150861_f.offset(field_150866_c, i);

				for (int j = 0; j < field_150862_g; ++j)
				{
					world.setBlockState(pos.up(j), getDefaultState().withProperty(AXIS, axis), 2);
				}
			}
		}
	}
}