package cavern.world;

import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CaveniaConfig;
import cavern.config.CavernConfig;
import cavern.config.HugeCavernConfig;
import cavern.config.IceCavernConfig;
import cavern.config.RuinsCavernConfig;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public class CaveType
{
	public static final int CAVERN = 0;
	public static final int RUINS_CAVERN = 1;
	public static final int AQUA_CAVERN = 2;
	public static final int CAVELAND = 3;
	public static final int ICE_CAVERN = 4;
	public static final int CAVENIA = 5;
	public static final int HUGE_CAVERN = 6;

	public static DimensionType DIM_CAVERN;
	public static DimensionType DIM_RUINS_CAVERN;
	public static DimensionType DIM_AQUA_CAVERN;
	public static DimensionType DIM_CAVELAND;
	public static DimensionType DIM_ICE_CAVERN;
	public static DimensionType DIM_CAVENIA;
	public static DimensionType DIM_HUGE_CAVERN;

	public static void registerDimensions()
	{
		DIM_CAVERN = DimensionType.register("Cavern", "_cavern", CavernConfig.dimensionId, WorldProviderCavern.class, false);

		DimensionManager.registerDimension(DIM_CAVERN.getId(), DIM_CAVERN);

		if (!AquaCavernConfig.dimensionDisabled)
		{
			DIM_AQUA_CAVERN = DimensionType.register("Aqua Cavern", "_aqua_cavern", AquaCavernConfig.dimensionId, WorldProviderAquaCavern.class, false);

			DimensionManager.registerDimension(DIM_AQUA_CAVERN.getId(), DIM_AQUA_CAVERN);
		}

		if (!CavelandConfig.dimensionDisabled)
		{
			DIM_CAVELAND = DimensionType.register("Caveland", "_caveland", CavelandConfig.dimensionId, WorldProviderCaveland.class, false);

			DimensionManager.registerDimension(DIM_CAVELAND.getId(), DIM_CAVELAND);
		}

		if (!IceCavernConfig.dimensionDisabled)
		{
			DIM_ICE_CAVERN = DimensionType.register("Ice Cavern", "_ice_cavern", IceCavernConfig.dimensionId, WorldProviderIceCavern.class, false);

			DimensionManager.registerDimension(DIM_ICE_CAVERN.getId(), DIM_ICE_CAVERN);
		}

		if (!RuinsCavernConfig.dimensionDisabled)
		{
			DIM_RUINS_CAVERN = DimensionType.register("Ruins Cavern", "_ruins_cavern", RuinsCavernConfig.dimensionId, WorldProviderRuinsCavern.class, false);

			DimensionManager.registerDimension(DIM_RUINS_CAVERN.getId(), DIM_RUINS_CAVERN);
		}

		if (!CaveniaConfig.dimensionDisabled)
		{
			DIM_CAVENIA = DimensionType.register("Cavenia", "_cavenia", CaveniaConfig.dimensionId, WorldProviderCavenia.class, false);

			DimensionManager.registerDimension(DIM_CAVENIA.getId(), DIM_CAVENIA);
		}

		if (!HugeCavernConfig.dimensionDisabled)
		{
			DIM_HUGE_CAVERN = DimensionType.register("Huge Cavern", "_huge_cavern", HugeCavernConfig.dimensionId, WorldProviderHugeCavern.class, false);

			DimensionManager.registerDimension(DIM_HUGE_CAVERN.getId(), DIM_HUGE_CAVERN);
		}
	}
}