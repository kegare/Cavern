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
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler("Aqua Cavern");

	public WorldProviderAquaCavern()
	{
		this.hasNoSky = true;
		this.setDimension(AquaCavernConfig.dimensionId);

		saveHandler.setDimension(getDimension()).setWorldHeight(AquaCavernConfig.worldHeight);
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
		return CaveSounds.MUSIC_AQUA;
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderAquaCavern(worldObj);
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