package cavern.world;

import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CavernConfig;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;

public final class CaveType
{
	public static final int CAVERN = 0;
	public static final int AQUA_CAVERN = 2;
	public static final int CAVELAND = 3;

	public static DimensionType DIM_CAVERN;
	public static DimensionType DIM_AQUA_CAVERN;
	public static DimensionType DIM_CAVELAND;

	public static void registerDimensions()
	{
		DIM_CAVERN = DimensionType.register("Cavern", "_cavern", CavernConfig.dimensionId, WorldProviderCavern.class, true);
		DIM_AQUA_CAVERN = DimensionType.register("Aqua Cavern", "_aqua_cavern", AquaCavernConfig.dimensionId, WorldProviderAquaCavern.class, false);
		DIM_CAVELAND = DimensionType.register("Caveland", "_caveland", CavelandConfig.dimensionId, WorldProviderCaveland.class, true);

		DimensionManager.registerDimension(DIM_CAVERN.getId(), DIM_CAVERN);
		DimensionManager.registerDimension(DIM_AQUA_CAVERN.getId(), DIM_AQUA_CAVERN);
		DimensionManager.registerDimension(DIM_CAVELAND.getId(), DIM_CAVELAND);
	}
}