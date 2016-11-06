package cavern.world;

import cavern.config.RuinsCavernConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import net.minecraft.init.Biomes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.IChunkGenerator;

public class WorldProviderRuinsCavern extends WorldProviderCavern
{
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler("Ruins Cavern");

	public WorldProviderRuinsCavern()
	{
		this.hasNoSky = true;
		this.setDimension(RuinsCavernConfig.dimensionId);

		saveHandler.setDimension(getDimension()).setWorldHeight(RuinsCavernConfig.worldHeight);
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
		return worldObj.getWorldInfo().getSeed();
	}

	@Override
	public int getActualHeight()
	{
		if (!worldObj.isRemote && saveHandler.getRawData() == null)
		{
			saveHandler.getData();
		}

		return saveHandler.getWorldHeight();
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
	protected void createBiomeProvider()
	{
		biomeProvider = new BiomeProviderSingle(Biomes.PLAINS);
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderRuinsCavern(worldObj);
	}
}