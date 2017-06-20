package cavern.world;

import java.util.Random;

import cavern.client.renderer.EmptyRenderer;
import cavern.config.CavernConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import cavern.core.CaveSounds;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.CaveMusicMessage;
import cavern.world.CaveEntitySpawner.IWorldEntitySpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderCavern extends WorldProviderSurface implements IWorldEntitySpawner
{
	protected static final Random RANDOM = new Random();

	protected int musicTime = 0;

	protected CaveDataManager dataManager;
	protected CaveEntitySpawner entitySpawner = new CaveEntitySpawner(this);

	@Override
	protected void init()
	{
		hasSkyLight = false;
		dataManager = new CaveDataManager(world.getWorldInfo().getDimensionData(getDimensionType().getId()).getCompoundTag("WorldData"));

		CaveBiomeManager manager = getBiomeManager();

		if (manager != null)
		{
			switch (getBiomeType())
			{
				case SQUARE:
					biomeProvider = new BiomeProviderCavern(world, 1, manager);
					return;
				case LARGE_SQUARE:
					biomeProvider = new BiomeProviderCavern(world, 5, manager);
					return;
				default:
			}
		}

		super.init();
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderCavern(world);
	}

	@Override
	public DimensionType getDimensionType()
	{
		return CaveType.DIM_CAVERN;
	}

	public ConfigBiomeType.Type getBiomeType()
	{
		return CavernConfig.biomeType.getType();
	}

	@Override
	public long getSeed()
	{
		if (!isRandomSeed())
		{
			return world.getWorldInfo().getSeed();
		}

		if (dataManager != null)
		{
			return dataManager.getWorldSeed(RANDOM.nextLong());
		}

		return super.getSeed();
	}

	@Override
	public int getActualHeight()
	{
		if (dataManager != null)
		{
			return dataManager.getWorldHeight(getWorldHeight());
		}

		return super.getActualHeight();
	}

	public int getWorldHeight()
	{
		return CavernConfig.worldHeight;
	}

	public boolean isRandomSeed()
	{
		return CavernConfig.randomSeed;
	}

	public CaveBiomeManager getBiomeManager()
	{
		return CavernConfig.biomeManager;
	}

	public int getMonsterSpawn()
	{
		return CavernConfig.monsterSpawn;
	}

	public double getBrightness()
	{
		return CavernConfig.caveBrightness;
	}

	public SoundEvent getMusicSound()
	{
		if (world.rand.nextInt(3) == 0)
		{
			return CaveSounds.MUSIC_CAVE;
		}
		else
		{
			return CaveSounds.MUSIC_UNREST;
		}
	}

	@Override
	protected void generateLightBrightnessTable()
	{
		float f = (float)getBrightness();

		for (int i = 0; i <= 15; ++i)
		{
			float f1 = 1.0F - i / 15.0F;

			lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
		}
	}

	@Override
	public boolean isSurfaceWorld()
	{
		return false;
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float[] calcSunriseSunsetColors(float angle, float ticks)
	{
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3d getFogColor(float angle, float ticks)
	{
		return new Vec3d(0.01D, 0.01D, 0.01D);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isSkyColored()
	{
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3d getSkyColor(Entity cameraEntity, float partialTicks)
	{
		return new Vec3d(0.01D, 0.01D, 0.01D);
	}

	@Override
	public int getAverageGroundLevel()
	{
		return 10;
	}

	@Override
	public String getWelcomeMessage()
	{
		return "Entering the " + getDimensionType().getName();
	}

	@Override
	public String getDepartMessage()
	{
		return "Leaving the " + getDimensionType().getName();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getSkyRenderer()
	{
		if (super.getSkyRenderer() == null)
		{
			setSkyRenderer(EmptyRenderer.INSTANCE);
		}

		return super.getSkyRenderer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getCloudRenderer()
	{
		if (super.getCloudRenderer() == null)
		{
			setCloudRenderer(EmptyRenderer.INSTANCE);
		}

		return super.getCloudRenderer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getWeatherRenderer()
	{
		if (super.getWeatherRenderer() == null)
		{
			setWeatherRenderer(EmptyRenderer.INSTANCE);
		}

		return super.getWeatherRenderer();
	}

	@Override
	public boolean shouldMapSpin(String entity, double posX, double posY, double posZ)
	{
		return posY < 0 || posY >= getActualHeight();
	}

	@Override
	public BlockPos getSpawnPoint()
	{
		return BlockPos.ORIGIN.up(50);
	}

	@Override
	public BlockPos getRandomizedSpawnPoint()
	{
		return getSpawnPoint();
	}

	@Override
	public boolean isDaytime()
	{
		return false;
	}

	@Override
	public void calculateInitialWeather()
	{
		if (!world.isRemote)
		{
			musicTime = world.rand.nextInt(4000) + 8000;
		}
	}

	@Override
	public void updateWeather()
	{
		if (!world.isRemote)
		{
			if (--musicTime <= 0)
			{
				musicTime = world.rand.nextInt(5000) + 10000;

				SoundEvent music = getMusicSound();

				if (music != null)
				{
					CaveNetworkRegistry.sendToDimension(new CaveMusicMessage(music), getDimension());
				}
			}
		}
	}

	@Override
	public double getHorizon()
	{
		return getActualHeight();
	}

	@Override
	public boolean hasSkyLight()
	{
		return false;
	}

	@Override
	public boolean canDoLightning(Chunk chunk)
	{
		return false;
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk)
	{
		return false;
	}

	@Override
	public boolean canDropChunk(int x, int z)
	{
		return true;
	}

	@Override
	public void onWorldSave()
	{
		NBTTagCompound compound = new NBTTagCompound();

		if (dataManager != null)
		{
			compound.setTag("WorldData", dataManager.getCompound());
		}

		world.getWorldInfo().setDimensionData(getDimensionType().getId(), compound);
	}

	@Override
	public void onWorldUpdateEntities()
	{
		if (getMonsterSpawn() > 0 && world instanceof WorldServer)
		{
			WorldServer worldServer = (WorldServer)world;

			if (worldServer.getGameRules().getBoolean("doMobSpawning") && worldServer.getWorldInfo().getTerrainType() != WorldType.DEBUG_ALL_BLOCK_STATES)
			{
				MinecraftServer server = worldServer.getMinecraftServer();
				boolean spawnHostileMobs = worldServer.getDifficulty() != EnumDifficulty.PEACEFUL;

				if (server != null && !server.isSinglePlayer() && server.isDedicatedServer() && server instanceof DedicatedServer)
				{
					spawnHostileMobs = ((DedicatedServer)server).allowSpawnMonsters();
				}

				entitySpawner.findChunksForSpawning(worldServer, spawnHostileMobs, false, worldServer.getWorldInfo().getWorldTotalTime() % 400L == 0L);
			}
		}
	}

	@Override
	public Integer getMaxNumberOfCreature(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, EnumCreatureType type)
	{
		if (!type.getPeacefulCreature())
		{
			return Integer.valueOf(getMonsterSpawn());
		}

		return null;
	}
}