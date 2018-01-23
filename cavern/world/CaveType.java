package cavern.world;

import com.google.common.base.Strings;

import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CaveniaConfig;
import cavern.config.CavernConfig;
import cavern.config.HugeCavernConfig;
import cavern.config.IceCavernConfig;
import cavern.config.RuinsCavernConfig;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

public class CaveType
{
	public static DimensionType DIM_CAVERN;
	public static DimensionType DIM_RUINS_CAVERN;
	public static DimensionType DIM_AQUA_CAVERN;
	public static DimensionType DIM_CAVELAND;
	public static DimensionType DIM_ICE_CAVERN;
	public static DimensionType DIM_CAVENIA;
	public static DimensionType DIM_HUGE_CAVERN;

	public static DimensionType register(String name, int id, Class<? extends WorldProvider> provider)
	{
		if (Strings.isNullOrEmpty(name) || id == 0 || DimensionManager.isDimensionRegistered(id))
		{
			return null;
		}

		DimensionType type = DimensionType.register(name, "_" + name.replace(" ", "_").toLowerCase(), id, provider, false);

		DimensionManager.registerDimension(id, type);

		return type;
	}

	public static void registerDimensions()
	{
		DIM_CAVERN = register("Cavern", CavernConfig.dimensionId, WorldProviderCavern.class);

		if (!AquaCavernConfig.dimensionDisabled)
		{
			DIM_AQUA_CAVERN = register("Aqua Cavern", AquaCavernConfig.dimensionId, WorldProviderAquaCavern.class);
		}

		if (!CavelandConfig.dimensionDisabled)
		{
			DIM_CAVELAND = register("Caveland", CavelandConfig.dimensionId, WorldProviderCaveland.class);
		}

		if (!IceCavernConfig.dimensionDisabled)
		{
			DIM_ICE_CAVERN = register("Ice Cavern", IceCavernConfig.dimensionId, WorldProviderIceCavern.class);
		}

		if (!RuinsCavernConfig.dimensionDisabled)
		{
			DIM_RUINS_CAVERN = register("Ruins Cavern", RuinsCavernConfig.dimensionId, WorldProviderRuinsCavern.class);
		}

		if (!CaveniaConfig.dimensionDisabled)
		{
			DIM_CAVENIA = register("Cavenia", CaveniaConfig.dimensionId, WorldProviderCavenia.class);
		}

		if (!HugeCavernConfig.dimensionDisabled)
		{
			DIM_HUGE_CAVERN = register("Huge Cavern", HugeCavernConfig.dimensionId, WorldProviderHugeCavern.class);
		}
	}
}