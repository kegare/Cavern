package cavern.world;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.block.bonus.WeightedItem;
import cavern.config.IceCavernConfig;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.property.ConfigBiomeType;
import net.minecraft.init.Biomes;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.IChunkGenerator;

public class WorldProviderIceCavern extends WorldProviderCavern
{
	public static final CaveSaveHandler saveHandler = new CaveSaveHandler("Ice Cavern");

	public static final List<WeightedItem> HIBERNATE_ITEMS = Lists.newArrayList();

	public WorldProviderIceCavern()
	{
		this.hasNoSky = true;
		this.setDimension(IceCavernConfig.dimensionId);

		saveHandler.setDimension(getDimension()).setWorldHeight(IceCavernConfig.worldHeight);
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
	public long getSeed()
	{
		if (!IceCavernConfig.randomSeed)
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
		return IceCavernConfig.monsterSpawn;
	}

	@Override
	public double getBrightness()
	{
		return IceCavernConfig.caveBrightness;
	}

	@Override
	protected void createBiomeProvider()
	{
		biomeProvider = new BiomeProviderSingle(Biomes.ICE_PLAINS);
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderIceCavern(worldObj);
	}
}