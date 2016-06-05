package cavern.world;

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
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler("Cavern");

	protected int musicTime = 0;

	protected CaveEntitySpawner entitySpawner = new CaveEntitySpawner(this);

	public WorldProviderCavern()
	{
		this.hasNoSky = true;
		this.setDimension(CavernConfig.dimensionId);

		saveHandler.setDimension(getDimension());
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
		if (!CavernConfig.randomSeed)
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
		if (worldObj.rand.nextInt(3) == 0)
		{
			return CaveSounds.music_cave;
		}
		else
		{
			return CaveSounds.music_unrest;
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
	protected void createBiomeProvider()
	{
		CaveBiomeManager manager = getBiomeManager();

		if (manager != null)
		{
			switch (getBiomeType())
			{
				case SQUARE:
					biomeProvider = new BiomeProviderCavern(worldObj, 1, manager);
					return;
				case LARGE_SQUARE:
					biomeProvider = new BiomeProviderCavern(worldObj, 5, manager);
					return;
				default:
			}
		}

		super.createBiomeProvider();
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderCavern(worldObj);
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
			setSkyRenderer(EmptyRenderer.instance);
		}

		return super.getSkyRenderer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getCloudRenderer()
	{
		if (super.getCloudRenderer() == null)
		{
			setCloudRenderer(EmptyRenderer.instance);
		}

		return super.getCloudRenderer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getWeatherRenderer()
	{
		if (super.getWeatherRenderer() == null)
		{
			setWeatherRenderer(EmptyRenderer.instance);
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
		if (!worldObj.isRemote)
		{
			musicTime = worldObj.rand.nextInt(4000) + 8000;
		}

		worldObj.prevRainingStrength = 0.0F;
		worldObj.rainingStrength = 0.0F;
		worldObj.prevThunderingStrength = 0.0F;
		worldObj.thunderingStrength = 0.0F;
	}

	@Override
	public void updateWeather()
	{
		if (!worldObj.isRemote)
		{
			if (--musicTime <= 0)
			{
				musicTime = worldObj.rand.nextInt(5000) + 10000;

				SoundEvent music = getMusicSound();

				if (music != null)
				{
					CaveNetworkRegistry.sendToDimension(new CaveMusicMessage(music), getDimension());
				}
			}
		}

		worldObj.prevRainingStrength = 0.0F;
		worldObj.rainingStrength = 0.0F;
		worldObj.prevThunderingStrength = 0.0F;
		worldObj.thunderingStrength = 0.0F;
	}

	@Override
	public double getHorizon()
	{
		return getActualHeight();
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
	public void onWorldUpdateEntities()
	{
		if (getMonsterSpawn() > 0 && worldObj instanceof WorldServer)
		{
			WorldServer world = (WorldServer)worldObj;

			if (world.getGameRules().getBoolean("doMobSpawning") && world.getWorldInfo().getTerrainType() != WorldType.DEBUG_WORLD)
			{
				MinecraftServer server = world.getMinecraftServer();
				boolean spawnHostileMobs = world.getDifficulty() != EnumDifficulty.PEACEFUL;

				if (server != null && !server.isSinglePlayer() && server.isDedicatedServer() && server instanceof DedicatedServer)
				{
					spawnHostileMobs = ((DedicatedServer)server).allowSpawnMonsters();
				}

				entitySpawner.findChunksForSpawning(world, spawnHostileMobs, false, world.getWorldInfo().getWorldTotalTime() % 400L == 0L);
			}
		}
	}

	@Override
	public Boolean canSpawnCreature(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, EnumCreatureType type)
	{
		return null;
	}

	@Override
	public Integer getMaxNumberOfCreature(WorldServer world, boolean spawnHostileMobs, boolean spawnPeacefulMobs, boolean spawnOnSetTickRate, EnumCreatureType type)
	{
		if (!type.getPeacefulCreature())
		{
			return getMonsterSpawn();
		}

		return null;
	}
}