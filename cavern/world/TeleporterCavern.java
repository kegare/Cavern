package cavern.world;

import java.util.Random;

import cavern.api.CavernAPI;
import cavern.block.BlockPortalCavern;
import cavern.block.CaveBlocks;
import cavern.config.GeneralConfig;
import cavern.stats.IPortalCache;
import cavern.stats.PortalCache;
import cavern.util.CaveUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterCavern extends Teleporter
{
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState MOSSY_COBBLESTONE = Blocks.MOSSY_COBBLESTONE.getDefaultState();

	private final WorldServer worldObj;
	private final Random random;
	private final BlockPortalCavern portal;

	private final Long2ObjectMap<PortalPosition> coordCache = new Long2ObjectOpenHashMap<>(4096);

	public TeleporterCavern(WorldServer worldServer, BlockPortalCavern portal)
	{
		super(worldServer);
		this.worldObj = worldServer;
		this.random = new Random(worldServer.getSeed());
		this.portal = portal;
	}

	public TeleporterCavern(WorldServer worldServer)
	{
		this(worldServer, CaveBlocks.CAVERN_PORTAL);
	}

	public int getType()
	{
		return portal.getType();
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw)
	{
		if (entity instanceof EntityPlayerMP)
		{
			CaveUtils.setDimensionChange((EntityPlayerMP)entity);
		}

		boolean flag = false;

		if (GeneralConfig.portalCache)
		{
			IPortalCache cache = PortalCache.get(entity);
			double posX = entity.posX;
			double posY = entity.posY;
			double posZ = entity.posZ;

			if (cache.hasLastPos(getType(), entity.dimension))
			{
				BlockPos pos = cache.getLastPos(getType(), entity.dimension);

				setLocationAndAngles(entity, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

				if (placeInExistingPortal(entity, rotationYaw))
				{
					flag = true;
				}
				else
				{
					setLocationAndAngles(entity, posX, posY, posZ);
				}
			}
		}

		if (!flag && !placeInExistingPortal(entity, rotationYaw))
		{
			makePortal(entity);

			placeInExistingPortal(entity, rotationYaw);
		}

		if (entity instanceof EntityLivingBase)
		{
			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 25, 0, false, false));
		}

		if (entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)entity;

			player.addExperienceLevel(0);

			if (CavernAPI.dimension.isEntityInCaves(player) && player.getBedLocation() == null)
			{
				player.setSpawnPoint(player.getPosition(), true);
			}
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, float par2)
	{
		double d0 = -1.0D;
		int x = MathHelper.floor_double(entity.posX);
		int z = MathHelper.floor_double(entity.posZ);
		boolean flag = true;
		BlockPos pos = BlockPos.ORIGIN;
		long coord = ChunkPos.chunkXZ2Int(x, z);

		if (coordCache.containsKey(coord))
		{
			PortalPosition portalpos = coordCache.get(coord);
			d0 = 0.0D;
			pos = portalpos;
			portalpos.lastUpdateTime = worldObj.getTotalWorldTime();
			flag = false;
		}
		else
		{
			BlockPos pos1 = new BlockPos(entity);

			for (int px = -128; px <= 128; ++px)
			{
				BlockPos current;

				for (int pz = -128; pz <= 128; ++pz)
				{
					for (BlockPos blockpos = pos1.add(px, worldObj.getActualHeight() - 1 - pos1.getY(), pz); blockpos.getY() >= 0; blockpos = current)
					{
						current = blockpos.down();

						if (worldObj.getBlockState(blockpos).getBlock() == portal)
						{
							while (worldObj.getBlockState(current = blockpos.down()).getBlock() == portal)
							{
								blockpos = current;
							}

							double dist = blockpos.distanceSq(pos1);

							if (d0 < 0.0D || dist < d0)
							{
								d0 = dist;
								pos = blockpos;
							}
						}
					}
				}
			}
		}

		if (d0 >= 0.0D)
		{
			if (flag)
			{
				coordCache.put(coord, new PortalPosition(pos, worldObj.getTotalWorldTime()));
			}

			double posX = pos.getX() + 0.5D;
			double posY = pos.getY() + 0.5D;
			double posZ = pos.getZ() + 0.5D;
			EnumFacing face = null;

			if (worldObj.getBlockState(pos.west()).getBlock() == portal)
			{
				face = EnumFacing.NORTH;
			}

			if (worldObj.getBlockState(pos.east()).getBlock() == portal)
			{
				face = EnumFacing.SOUTH;
			}

			if (worldObj.getBlockState(pos.north()).getBlock() == portal)
			{
				face = EnumFacing.EAST;
			}

			if (worldObj.getBlockState(pos.south()).getBlock() == portal)
			{
				face = EnumFacing.WEST;
			}

			EnumFacing face0 = EnumFacing.getHorizontal(0);

			if (face != null)
			{
				EnumFacing face1 = face.rotateYCCW();
				BlockPos pos1 = pos.offset(face);
				boolean flag1 = isNotAir(pos1);
				boolean flag2 = isNotAir(pos1.offset(face1));

				if (flag2 && flag1)
				{
					pos = pos.offset(face1);
					face = face.getOpposite();
					face1 = face1.getOpposite();
					BlockPos blockpos3 = pos.offset(face);
					flag1 = isNotAir(blockpos3);
					flag2 = isNotAir(blockpos3.offset(face1));
				}

				float f0 = 0.5F;
				float f1 = 0.5F;

				if (!flag2 && flag1)
				{
					f0 = 1.0F;
				}
				else if (flag2 && !flag1)
				{
					f0 = 0.0F;
				}
				else if (flag2)
				{
					f1 = 0.0F;
				}

				posX = pos.getX() + 0.5D;
				posY = pos.getY() + 0.5D;
				posZ = pos.getZ() + 0.5D;
				posX += face1.getFrontOffsetX() * f0 + face.getFrontOffsetX() * f1;
				posZ += face1.getFrontOffsetZ() * f0 + face.getFrontOffsetZ() * f1;
				float f2 = 0.0F;
				float f3 = 0.0F;
				float f4 = 0.0F;
				float f5 = 0.0F;

				if (face == face0)
				{
					f2 = 1.0F;
					f3 = 1.0F;
				}
				else if (face == face0.getOpposite())
				{
					f2 = -1.0F;
					f3 = -1.0F;
				}
				else if (face == face0.rotateY())
				{
					f4 = 1.0F;
					f5 = -1.0F;
				}
				else
				{
					f4 = -1.0F;
					f5 = 1.0F;
				}

				double d2 = entity.motionX;
				double d3 = entity.motionZ;
				entity.motionX = d2 * f2 + d3 * f5;
				entity.motionZ = d2 * f4 + d3 * f3;
				entity.rotationYaw = par2 - face0.getHorizontalIndex() * 90 + face.getHorizontalIndex() * 90;
			}

			setLocationAndAngles(entity, posX, posY, posZ);

			return true;
		}

		return false;
	}

	protected boolean isNotAir(BlockPos pos)
	{
		return !worldObj.isAirBlock(pos) || !worldObj.isAirBlock(pos.up());
	}

	public void setLocationAndAngles(Entity entity, double posX, double posY, double posZ)
	{
		if (entity instanceof EntityPlayerMP)
		{
			((EntityPlayerMP)entity).connection.setPlayerLocation(posX, posY, posZ, entity.rotationYaw, entity.rotationPitch);
		}
		else
		{
			entity.setLocationAndAngles(posX, posY, posZ, entity.rotationYaw, entity.rotationPitch);
		}
	}

	@Override
	public boolean makePortal(Entity entity)
	{
		int range = 16;
		double d0 = -1.0D;
		int x = MathHelper.floor_double(entity.posX);
		int y = MathHelper.floor_double(entity.posY);
		int z = MathHelper.floor_double(entity.posZ);
		int x1 = x;
		int y1 = y;
		int z1 = z;
		int i = 0;
		int j = random.nextInt(4);
		MutableBlockPos pos = new MutableBlockPos();

		for (int px = x - range; px <= x + range; ++px)
		{
			double xSize = px + 0.5D - entity.posX;

			for (int pz = z - range; pz <= z + range; ++pz)
			{
				double zSize = pz + 0.5D - entity.posZ;

				outside: for (int py = worldObj.getActualHeight() - 1; py >= 0; --py)
				{
					if (worldObj.isAirBlock(pos.setPos(px, py, pz)))
					{
						while (py > 0 && worldObj.isAirBlock(pos.setPos(px, py - 1, pz)))
						{
							--py;
						}

						for (int k = j; k < j + 4; ++k)
						{
							int i1 = k % 2;
							int j1 = 1 - i1;

							if (k % 4 >= 2)
							{
								i1 = -i1;
								j1 = -j1;
							}

							for (int i2 = 0; i2 < 3; ++i2)
							{
								for (int j2 = 0; j2 < 4; ++j2)
								{
									for (int k2 = -1; k2 < 4; ++k2)
									{
										int px1 = px + (j2 - 1) * i1 + i2 * j1;
										int py1 = py + k2;
										int pz1 = pz + (j2 - 1) * j1 - i2 * i1;

										pos.setPos(px1, py1, pz1);

										if (k2 < 0 && !worldObj.getBlockState(pos).getMaterial().isSolid() || k2 >= 0 && !worldObj.isAirBlock(pos))
										{
											continue outside;
										}
									}
								}
							}

							double ySize = py + 0.5D - entity.posY;
							double size = xSize * xSize + ySize * ySize + zSize * zSize;

							if (d0 < 0.0D || size < d0)
							{
								d0 = size;
								x1 = px;
								y1 = py;
								z1 = pz;
								i = k % 4;
							}
						}
					}
				}
			}
		}

		if (d0 < 0.0D)
		{
			for (int px = x - range; px <= x + range; ++px)
			{
				double xSize = px + 0.5D - entity.posX;

				for (int pz = z - range; pz <= z + range; ++pz)
				{
					double zSize = pz + 0.5D - entity.posZ;

					outside: for (int py = worldObj.getActualHeight() - 1; py >= 0; --py)
					{
						if (worldObj.isAirBlock(pos.setPos(px, py, pz)))
						{
							while (py > 0 && worldObj.isAirBlock(pos.setPos(px, py - 1, pz)))
							{
								--py;
							}

							for (int k = j; k < j + 2; ++k)
							{
								int i1 = k % 2;
								int j1 = 1 - i1;

								for (int i2 = 0; i2 < 4; ++i2)
								{
									for (int j2 = -1; j2 < 4; ++j2)
									{
										int px1 = px + (i2 - 1) * i1;
										int py1 = py + j2;
										int pz1 = pz + (i2 - 1) * j1;

										pos.setPos(px1, py1, pz1);

										if (j2 < 0 && !worldObj.getBlockState(pos).getMaterial().isSolid() || j2 >= 0 && !worldObj.isAirBlock(pos))
										{
											continue outside;
										}
									}
								}

								double ySize = py + 0.5D - entity.posY;
								double size = xSize * xSize + ySize * ySize + zSize * zSize;

								if (d0 < 0.0D || size < d0)
								{
									d0 = size;
									x1 = px;
									y1 = py;
									z1 = pz;
									i = k % 2;
								}
							}
						}
					}
				}
			}
		}

		int x2 = x1;
		int y2 = y1;
		int z2 = z1;
		int i1 = i % 2;
		int j1 = 1 - i1;

		if (i % 4 >= 2)
		{
			i1 = -i1;
			j1 = -j1;
		}

		if (d0 < 0.0D)
		{
			y1 = MathHelper.clamp_int(y1, CavernAPI.dimension.isEntityInCaves(entity) ? 10 : 70, worldObj.getActualHeight() - 10);
			y2 = y1;

			for (int i2 = -1; i2 <= 1; ++i2)
			{
				for (int j2 = 1; j2 < 3; ++j2)
				{
					for (int k2 = -1; k2 < 3; ++k2)
					{
						int blockX = x2 + (j2 - 1) * i1 + i2 * j1;
						int blockY = y2 + k2;
						int blockZ = z2 + (j2 - 1) * j1 - i2 * i1;
						boolean flag = k2 < 0;

						worldObj.setBlockState(pos.setPos(blockX, blockY, blockZ), flag ? MOSSY_COBBLESTONE : AIR);
					}
				}
			}
		}

		IBlockState state = portal.getDefaultState().withProperty(BlockPortal.AXIS, i1 != 0 ? EnumFacing.Axis.X : EnumFacing.Axis.Z);

		for (int i2 = 0; i2 < 4; ++i2)
		{
			for (int j2 = 0; j2 < 4; ++j2)
			{
				for (int k2 = -1; k2 < 4; ++k2)
				{
					int blockX = x2 + (j2 - 1) * i1;
					int blockY = y2 + k2;
					int blockZ = z2 + (j2 - 1) * j1;
					boolean flag1 = j2 == 0 || j2 == 3 || k2 == -1 || k2 == 3;

					worldObj.setBlockState(pos.setPos(blockX, blockY, blockZ), flag1 ? MOSSY_COBBLESTONE : state, 2);
				}
			}

			for (int j2 = 0; j2 < 4; ++j2)
			{
				for (int k2 = -1; k2 < 4; ++k2)
				{
					int blockX = x2 + (j2 - 1) * i1;
					int blockY = y2 + k2;
					int blockZ = z2 + (j2 - 1) * j1;

					worldObj.notifyNeighborsOfStateChange(pos.setPos(blockX, blockY, blockZ), worldObj.getBlockState(pos).getBlock());
				}
			}
		}

		return true;
	}

	@Override
	public void removeStalePortalLocations(long time)
	{
		if (time % 100L == 0L)
		{
			ObjectIterator<PortalPosition> iterator = coordCache.values().iterator();
			long i = time - 300L;

			while (iterator.hasNext())
			{
				PortalPosition pos = iterator.next();

				if (pos == null || pos.lastUpdateTime < i)
				{
					iterator.remove();
				}
			}
		}
	}
}