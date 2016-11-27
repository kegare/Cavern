package cavern.world;

import cavern.config.AquaCavernConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import cavern.core.CaveSounds;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkGenerator;

public class WorldProviderAquaCavern extends WorldProviderCavern
{
	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderAquaCavern(worldObj);
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
	public int getWorldHeight()
	{
		return AquaCavernConfig.worldHeight;
	}

	@Override
	public boolean isRandomSeed()
	{
		return AquaCavernConfig.randomSeed;
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
		return CaveSounds.MUSIC_AQUA;
	}

	@Override
	public void onWorldUpdateEntities()
	{
		if (worldObj instanceof WorldServer)
		{
			WorldServer world = (WorldServer)worldObj;

			if (world.getWorldInfo().getTerrainType() != WorldType.DEBUG_WORLD)
			{
				entitySpawner.findChunksForSpawning(world, false, true, world.getWorldInfo().getWorldTotalTime() % 400L == 0L);
			}
		}
	}

	@Override
	public Integer getMaxNumberOfCreature(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, EnumCreatureType type)
	{
		if (type == EnumCreatureType.WATER_CREATURE)
		{
			return Integer.valueOf(100);
		}

		return null;
	}
}