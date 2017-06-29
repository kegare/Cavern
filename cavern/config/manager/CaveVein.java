package cavern.config.manager;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import cavern.util.BlockMeta;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
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
		this.biomes = new int[0];
	}

	public CaveVein(BlockMeta block, int weight, int size, int min, int max, Object... biomes)
	{
		this(block, new BlockMeta(Blocks.STONE.getDefaultState()), weight, 1.0D, size, min, max);
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
			if (element instanceof Biome)
			{
				biomes.add(Biome.getIdForBiome((Biome)element));
			}
			else if (element instanceof Integer)
			{
				Biome biome = Biome.getBiome((Integer)element);

				if (biome != null)
				{
					biomes.add(Biome.getIdForBiome(biome));
				}
			}
			else if (element instanceof Type)
			{
				Type type = (Type)element;

				for (Biome biome : BiomeDictionary.getBiomes(type))
				{
					biomes.add(Biome.getIdForBiome(biome));
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

	public List<Biome> getBiomeList()
	{
		if (biomes == null || biomes.length <= 0)
		{
			return Collections.emptyList();
		}

		List<Biome> ret = Lists.newArrayList();

		for (int id : biomes)
		{
			Biome biome = Biome.getBiome(id);

			if (biome != null)
			{
				ret.add(biome);
			}
		}

		return ret;
	}
}