package cavern.world;

import cavern.config.CavelandConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import cavern.core.CaveSounds;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderCaveland extends WorldProviderCavern
{
	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkGeneratorCaveland(world);
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
	public int getWorldHeight()
	{
		return CavelandConfig.worldHeight;
	}

	@Override
	public boolean isRandomSeed()
	{
		return CavelandConfig.randomSeed;
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
		return CaveSounds.MUSIC_HOPE;
	}
}