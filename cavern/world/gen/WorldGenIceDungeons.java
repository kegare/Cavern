package cavern.world.gen;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import cavern.util.CaveLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.storage.loot.LootTableList;

public class WorldGenIceDungeons extends WorldGenDungeons
{
	private static final List<ResourceLocation> MOBS = Lists.newArrayList();

	public static boolean addDungeonMob(ResourceLocation name)
	{
		return name != null && EntityList.isRegistered(name) && MOBS.add(name);
	}

	public static boolean addDungeonMob(Class<? extends Entity> clazz)
	{
		ResourceLocation name = EntityList.getKey(clazz);

		return name != null && MOBS.add(name);
	}

	public static void addDungeonMobs(Collection<String> names)
	{
		Set<ResourceLocation> nameSet = EntityList.getEntityNameList();

		for (String name : names)
		{
			if (!Strings.isNullOrEmpty(name))
			{
				ResourceLocation entryName = new ResourceLocation(name);

				if (nameSet.contains(entryName))
				{
					MOBS.add(entryName);
				}
			}
		}
	}

	public static boolean removeDungeonMob(ResourceLocation name)
	{
		return name != null && MOBS.remove(name);
	}

	public static boolean removeDungeonMob(Class<? extends Entity> clazz)
	{
		return removeDungeonMob(EntityList.getKey(clazz));
	}

	public static void clearDungeonMobs()
	{
		MOBS.clear();
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		int i1 = rand.nextInt(2) + 2;
		int j1 = -i1 - 1;
		int k1 = i1 + 1;
		int i2 = rand.nextInt(2) + 2;
		int j2 = -i2 - 1;
		int k2 = i2 + 1;
		int count = 0;

		for (int x = j1; x <= k1; ++x)
		{
			for (int y = -1; y <= 4; ++y)
			{
				for (int z = j2; z <= k2; ++z)
				{
					BlockPos blockpos = pos.add(x, y, z);
					Material material = world.getBlockState(blockpos).getMaterial();
					boolean flag = material.isSolid();

					if (y == -1 && !flag)
					{
						return false;
					}

					if (y == 4 && !flag)
					{
						return false;
					}

					if ((x == j1 || x == k1 || z == j2 || z == k2) && y == 0 && world.isAirBlock(blockpos) && world.isAirBlock(blockpos.up()))
					{
						++count;
					}
				}
			}
		}

		if (count >= 1 && count <= 5)
		{
			int type = rand.nextInt(2);
			IBlockState state;

			switch (type)
			{
				case 1:
					state = Blocks.SNOW.getDefaultState();
				default:
					state = Blocks.PACKED_ICE.getDefaultState();
			}

			for (int x = j1; x <= k1; ++x)
			{
				for (int y = 3; y >= -1; --y)
				{
					for (int z = j2; z <= k2; ++z)
					{
						BlockPos blockpos = pos.add(x, y, z);

						if (x != j1 && y != -1 && z != j2 && x != k1 && y != 4 && z != k2)
						{
							if (world.getBlockState(blockpos).getBlock() != Blocks.CHEST)
							{
								world.setBlockToAir(blockpos);
							}
						}
						else if (blockpos.getY() >= 0 && !world.getBlockState(blockpos.down()).getMaterial().isSolid())
						{
							world.setBlockToAir(blockpos);
						}
						else if (world.getBlockState(blockpos).getMaterial().isSolid() && world.getBlockState(blockpos).getBlock() != Blocks.CHEST)
						{
							world.setBlockState(blockpos, state, 2);
						}
					}
				}
			}

			for (int i = 0; i < 2; ++i)
			{
				for (int j = 0; j < 3; ++j)
				{
					int x = pos.getX() + rand.nextInt(i1 * 2 + 1) - i1;
					int y = pos.getY();
					int z = pos.getZ() + rand.nextInt(i2 * 2 + 1) - i2;
					BlockPos blockpos = new BlockPos(x, y, z);

					if (world.isAirBlock(blockpos))
					{
						count = 0;

						for (EnumFacing face : EnumFacing.Plane.HORIZONTAL)
						{
							if (world.getBlockState(blockpos.offset(face)).getMaterial().isSolid())
							{
								++count;
							}
						}

						if (count == 1)
						{
							world.setBlockState(blockpos, Blocks.CHEST.correctFacing(world, blockpos, Blocks.CHEST.getDefaultState()), 2);

							TileEntity tile = world.getTileEntity(blockpos);

							if (tile != null && tile instanceof TileEntityChest)
							{
								((TileEntityChest)tile).setLootTable(LootTableList.CHESTS_SIMPLE_DUNGEON, rand.nextLong());
							}

							break;
						}
					}
				}
			}

			world.setBlockState(pos, Blocks.MOB_SPAWNER.getDefaultState(), 2);

			TileEntity tile = world.getTileEntity(pos);

			if (tile != null && tile instanceof TileEntityMobSpawner)
			{
				((TileEntityMobSpawner)tile).getSpawnerBaseLogic().setEntityId(pickMobSpawner(rand));
			}
			else
			{
				CaveLog.warning("Failed to fetch mob spawner entity at (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");
			}

			return true;
		}

		return false;
	}

	public ResourceLocation pickMobSpawner(Random random)
	{
		if (!MOBS.isEmpty())
		{
			if (MOBS.size() > 1)
			{
				return MOBS.get(random.nextInt(MOBS.size() - 1));
			}
			else
			{
				return MOBS.get(0);
			}
		}

		return null;
	}
}