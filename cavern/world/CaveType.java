package cavern.world;

import cavern.api.CavernAPI;
import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CavernConfig;
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

	public static DimensionType DIM_CAVERN;
	public static DimensionType DIM_RUINS_CAVERN;
	public static DimensionType DIM_AQUA_CAVERN;
	public static DimensionType DIM_CAVELAND;
	public static DimensionType DIM_ICE_CAVERN;

	public static void registerDimensions()
	{
		DIM_CAVERN = DimensionType.register("Cavern", "_cavern", CavernConfig.dimensionId, WorldProviderCavern.class, false);

		DimensionManager.registerDimension(DIM_CAVERN.getId(), DIM_CAVERN);

		if (!CavernAPI.dimension.isAquaCavernDisabled())
		{
			DIM_AQUA_CAVERN = DimensionType.register("Aqua Cavern", "_aqua_cavern", AquaCavernConfig.dimensionId, WorldProviderAquaCavern.class, false);

			DimensionManager.registerDimension(DIM_AQUA_CAVERN.getId(), DIM_AQUA_CAVERN);
		}

		if (!CavernAPI.dimension.isCavelandDisabled())
		{
			DIM_CAVELAND = DimensionType.register("Caveland", "_caveland", CavelandConfig.dimensionId, WorldProviderCaveland.class, false);

			DimensionManager.registerDimension(DIM_CAVELAND.getId(), DIM_CAVELAND);
		}

		if (!CavernAPI.dimension.isIceCavernDisabled())
		{
			DIM_ICE_CAVERN = DimensionType.register("Ice Cavern", "_ice_cavern", IceCavernConfig.dimensionId, WorldProviderIceCavern.class, false);

			DimensionManager.registerDimension(DIM_ICE_CAVERN.getId(), DIM_ICE_CAVERN);
		}

		if (!CavernAPI.dimension.isRuinsCavernDisabled())
		{
			DIM_RUINS_CAVERN = DimensionType.register("Ruins Cavern", "_ruins_cavern", RuinsCavernConfig.dimensionId, WorldProviderRuinsCavern.class, false);

			DimensionManager.registerDimension(DIM_RUINS_CAVERN.getId(), DIM_RUINS_CAVERN);
		}
	}
}