package cavern.config.manager;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import cavern.util.BlockMeta;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

public class CaveVein
{
	private BlockMeta blockMeta;
	private BlockMeta targetBlockMeta;
	private int veinWeight;
	private double veinChance;
	private int veinSize;
	private int minHeight;
	private int maxHeight;
	private int[] biomes;

	public CaveVein() {}

	public CaveVein(BlockMeta block, BlockMeta target, int weight, double chance, int size, int min, int max)
	{
		this.blockMeta = block;
		this.targetBlockMeta = target;
		this.veinWeight = weight;
		this.veinChance = chance;
		this.veinSize = size;
		this.minHeight = min;
		this.maxHeight = max;
	}

	public CaveVein(BlockMeta block, int weight, int size, int min, int max, Object... biomes)
	{
		this(block, new BlockMeta(Blocks.stone.getDefaultState()), weight, 1.0D, size, min, max);
		this.biomes = getBiomes(biomes);
	}

	public CaveVein(CaveVein vein)
	{
		this(vein.blockMeta, vein.targetBlockMeta, vein.veinWeight, vein.veinChance, vein.veinSize, vein.minHeight, vein.maxHeight);
		this.biomes = vein.biomes;
	}

	private int[] getBiomes(Object... objects)
	{
		Set<Integer> biomes = Sets.newTreeSet();

		for (Object element : objects)
		{
			if (element instanceof BiomeGenBase)
			{
				biomes.add(BiomeGenBase.getIdForBiome((BiomeGenBase)element));
			}
			else if (element instanceof Integer)
			{
				BiomeGenBase biome = BiomeGenBase.getBiome((Integer)element);

				if (biome != null)
				{
					biomes.add(BiomeGenBase.getIdForBiome(biome));
				}
			}
			else if (element instanceof Type)
			{
				Type type = (Type)element;

				for (BiomeGenBase biome : BiomeDictionary.getBiomesForType(type))
				{
					biomes.add(BiomeGenBase.getIdForBiome(biome));
				}
			}
		}

		return Ints.toArray(biomes);
	}

	public BlockMeta getBlockMeta()
	{
		return blockMeta;
	}

	public void setBlockMeta(BlockMeta block)
	{
		blockMeta = block;
	}

	public BlockMeta getTarget()
	{
		return targetBlockMeta;
	}

	public void setTarget(BlockMeta block)
	{
		targetBlockMeta = block;
	}

	public int getWeight()
	{
		return veinWeight;
	}

	public void setWeight(int weight)
	{
		veinWeight = weight;
	}

	public double getChance()
	{
		return veinChance;
	}

	public void setChance(double chance)
	{
		veinChance = chance;
	}

	public int getSize()
	{
		return veinSize;
	}

	public void setSize(int size)
	{
		veinSize = size;
	}

	public int getMinHeight()
	{
		return minHeight;
	}

	public void setMinHeight(int height)
	{
		minHeight = height;
	}

	public int getMaxHeight()
	{
		return maxHeight;
	}

	public void setMaxHeight(int height)
	{
		maxHeight = height;
	}

	public int[] getBiomes()
	{
		return biomes;
	}

	public void setBiomes(int[] target)
	{
		biomes = target;
	}

	public List<BiomeGenBase> getBiomeList()
	{
		if (biomes == null || biomes.length <= 0)
		{
			return Collections.emptyList();
		}

		List<BiomeGenBase> ret = Lists.newArrayList();

		for (int id : biomes)
		{
			BiomeGenBase biome = BiomeGenBase.getBiome(id);

			if (biome != null)
			{
				ret.add(biome);
			}
		}

		return ret;
	}

	public void generateVeins(World world, Random random, BlockPos pos)
	{
		int worldHeight = world.getActualHeight();
		int weight = getWeight();
		int min = getMinHeight();
		int max = Math.min(getMaxHeight(), worldHeight - 2);

		if (weight > 0 && min < max)
		{
			BlockMeta blockMeta = getBlockMeta();
			int size = getSize();
			double chance = getChance();
			BlockMeta target = getTarget();
			int[] biomes = getBiomes();

			for (int i = 0; i < weight; ++i)
			{
				if (chance >= 1.0D || random.nextDouble() <= chance)
				{
					int x = pos.getX() + random.nextInt(16);
					int y = random.nextInt(max - min) + min;
					int z = pos.getZ() + random.nextInt(16);
					float var1 = random.nextFloat() * (float)Math.PI;
					double var2 = x + 8 + MathHelper.sin(var1) * size / 8.0F;
					double var3 = x + 8 - MathHelper.sin(var1) * size / 8.0F;
					double var4 = z + 8 + MathHelper.cos(var1) * size / 8.0F;
					double var5 = z + 8 - MathHelper.cos(var1) * size / 8.0F;
					double var6 = y + random.nextInt(3) - 2;
					double var7 = y + random.nextInt(3) - 2;
					int gen = 0;

					for (int j = 0; gen <= size && j <= size; ++j)
					{
						double var8 = var2 + (var3 - var2) * j / size;
						double var9 = var6 + (var7 - var6) * j / size;
						double var10 = var4 + (var5 - var4) * j / size;
						double var11 = random.nextDouble() * size / 16.0D;
						double var12 = (MathHelper.sin(j * (float)Math.PI / size) + 1.0F) * var11 + 1.0D;
						double var13 = (MathHelper.sin(j * (float)Math.PI / size) + 1.0F) * var11 + 1.0D;

						for (x = MathHelper.floor_double(var8 - var12 / 2.0D); gen <= size && x <= MathHelper.floor_double(var8 + var12 / 2.0D); ++x)
						{
							double xScale = (x + 0.5D - var8) / (var12 / 2.0D);

							if (xScale * xScale < 1.0D)
							{
								for (y = MathHelper.floor_double(var9 - var13 / 2.0D); gen <= size && y <= MathHelper.floor_double(var9 + var13 / 2.0D); ++y)
								{
									double yScale = (y + 0.5D - var9) / (var13 / 2.0D);

									if (xScale * xScale + yScale * yScale < 1.0D)
									{
										for (z = MathHelper.floor_double(var10 - var12 / 2.0D); gen < size && z <= MathHelper.floor_double(var10 + var12 / 2.0D); ++z)
										{
											double zScale = (z + 0.5D - var10) / (var12 / 2.0D);

											if (xScale * xScale + yScale * yScale + zScale * zScale < 1.0D)
											{
												BlockPos blockpos = new BlockPos(x, y, z);
												IBlockState state = world.getBlockState(blockpos);

												if (state.getBlock() == target.getBlock() && state.getBlock().getMetaFromState(state) == target.getMeta())
												{
													if (biomes == null || biomes.length <= 0 || ArrayUtils.contains(biomes, BiomeGenBase.getIdForBiome(world.getBiomeGenForCoords(blockpos))))
													{
														if (world.setBlockState(blockpos, blockMeta.getBlockState(), 2))
														{
															++gen;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}