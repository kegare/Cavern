package cavern.config.manager;

import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.util.WeightedRandom;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.config.Configuration;

public class CaveBiomeManager
{
	private final Map<Biome, CaveBiome> CAVE_BIOMES = Maps.newHashMap();

	public Configuration config;

	private int type;

	public CaveBiomeManager(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	public boolean addCaveBiome(CaveBiome biome)
	{
		return addCaveBiome(biome, false);
	}

	public boolean addCaveBiome(CaveBiome biome, boolean absent)
	{
		if (absent)
		{
			if (getCaveBiomes().get(biome) == null)
			{
				getCaveBiomes().put(biome.getBiome(), biome);

				return true;
			}

			return false;
		}

		return getCaveBiomes().put(biome.getBiome(), biome) != biome;
	}

	public CaveBiome getCaveBiome(Biome biome)
	{
		return getCaveBiome(biome, false);
	}

	public CaveBiome getCaveBiome(Biome biome, boolean identity)
	{
		CaveBiome ret = getCaveBiomes().get(biome);

		if (identity && ret == null)
		{
			return new CaveBiome(biome, 50);
		}

		return ret;
	}

	@Nullable
	public CaveBiome getRandomCaveBiome(Random random)
	{
		return WeightedRandom.getRandomItem(random, Lists.newArrayList(getCaveBiomes().values()));
	}

	public Biome getRandomBiome(Random random, Biome defaultBiome)
	{
		CaveBiome caveBiome = getRandomCaveBiome(random);

		if (caveBiome != null)
		{
			return caveBiome.getBiome();
		}

		return defaultBiome;
	}

	public Map<Biome, CaveBiome> getCaveBiomes()
	{
		return CAVE_BIOMES;
	}
}