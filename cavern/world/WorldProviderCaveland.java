package cavern.world;

import cavern.config.CavelandConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import cavern.core.CaveSounds;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.chunk.IChunkGenerator;

public class WorldProviderCaveland extends WorldProviderCavern
{
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler("Caveland");

	public WorldProviderCaveland()
	{
		this.hasNoSky = true;
		this.setDimension(CavelandConfig.dimensionId);

		saveHandler.setDimension(getDimension());
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveType.DIM_CAVELAND;
	}

	@Override
	public ConfigBiomeType.Type getBiomeType()
	{
		return ConfigBiomeType.Type.NATURAL;
	}

	@Override
	public long getSeed()
	{
		if (!CavelandConfig.randomSeed)
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
		return CavelandConfig.monsterSpawn;
	}

	@Override
	public double getBrightness()
	{
		return CavelandConfig.caveBrightness;
	}

	@Override
	public SoundEvent getMusicSound()
	{
		return CaveSounds.music_hope;
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderCaveland(worldObj);
	}
}