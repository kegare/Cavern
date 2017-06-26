package cavern.world;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.config.RuinsCavernConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import cavern.util.WeightedItemStack;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderRuinsCavern extends WorldProviderCavern
{
	public static final List<WeightedItemStack> RUINS_CHEST_ITEMS = Lists.newArrayList();

	@Override
	protected void init()
	{
		super.init();

		biomeProvider = new BiomeProviderSingle(Biomes.PLAINS);
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorRuinsCavern(world);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveType.DIM_RUINS_CAVERN;
	}

	@Override
	public ConfigBiomeType.Type getBiomeType()
	{
		return ConfigBiomeType.Type.NATURAL;
	}

	@Override
	public long getSeed()
	{
		return world.getWorldInfo().getSeed();
	}

	@Override
	public int getWorldHeight()
	{
		return RuinsCavernConfig.worldHeight;
	}

	@Override
	public boolean isRandomSeed()
	{
		return false;
	}

	@Override
	public CaveBiomeManager getBiomeManager()
	{
		return null;
	}

	@Override
	public int getMonsterSpawn()
	{
		return 0;
	}

	@Override
	public double getBrightness()
	{
		return RuinsCavernConfig.caveBrightness;
	}

	@Override
	public BlockPos getSpawnPoint()
	{
		return BlockPos.ORIGIN.up(80);
	}
}