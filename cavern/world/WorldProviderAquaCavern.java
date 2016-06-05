package cavern.world;

import cavern.config.AquaCavernConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import cavern.core.CaveSounds;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.IChunkGenerator;

public class WorldProviderAquaCavern extends WorldProviderCavern
{
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler("Aqua Cavern");

	public WorldProviderAquaCavern()
	{
		this.hasNoSky = true;
		this.setDimension(AquaCavernConfig.dimensionId);

		saveHandler.setDimension(getDimension());
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveType.DIM_AQUA_CAVERN;
	}

	@Override
	public ConfigBiomeType.Type getBiomeType()
	{
		return ConfigBiomeType.Type.NATURAL;
	}

	@Override
	public long getSeed()
	{
		if (!AquaCavernConfig.randomSeed)
		{
			return worldObj.getWorldInfo().getSeed();
		}

		if (!worldObj.isRemote && saveHandler.getRawData() == null)
		{
			saveHandler.getData();
		}

		return saveHandler.getWorldSeed();
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
		return AquaCavernConfig.caveBrightness;
	}

	@Override
	public SoundEvent getMusicSound()
	{
		return CaveSounds.music_aqua;
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderAquaCavern(worldObj);
	}
}