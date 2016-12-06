package cavern.world;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.config.IceCavernConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import cavern.util.WeightedItem;
import net.minecraft.init.Biomes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.IChunkGenerator;

public class WorldProviderIceCavern extends WorldProviderCavern
{
	public static final List<WeightedItem> HIBERNATE_ITEMS = Lists.newArrayList();

	@Override
	protected void init()
	{
		super.init();

		biomeProvider = new BiomeProviderSingle(Biomes.ICE_PLAINS);
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderIceCavern(world);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveType.DIM_ICE_CAVERN;
	}

	@Override
	public ConfigBiomeType.Type getBiomeType()
	{
		return ConfigBiomeType.Type.NATURAL;
	}

	@Override
	public int getWorldHeight()
	{
		return IceCavernConfig.worldHeight;
	}

	@Override
	public boolean isRandomSeed()
	{
		return IceCavernConfig.randomSeed;
	}

	@Override
	public CaveBiomeManager getBiomeManager()
	{
		return null;
	}

	@Override
	public int getMonsterSpawn()
	{
		return IceCavernConfig.monsterSpawn;
	}

	@Override
	public double getBrightness()
	{
		return IceCavernConfig.caveBrightness;
	}
}