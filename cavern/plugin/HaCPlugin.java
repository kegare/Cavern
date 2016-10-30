package cavern.plugin;

import cavern.api.CavernAPI;
import cavern.config.IceCavernConfig;
import cavern.item.CaveItems;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.damage.DamageAPI;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.Optional.Method;

public class HaCPlugin
{
	public static final String LIB_MODID = "dcs_climate|lib";

	@Method(modid = LIB_MODID)
	public static void load()
	{
		DamageAPI.armorRegister.RegisterMaterial(CaveItems.HEXCITE_ARMOR, 0.5F);

		if (!CavernAPI.dimension.isIceCavernDisabled())
		{
			int dim = IceCavernConfig.dimensionId;

			for (Biome biome : BiomeDictionary.getBiomesForType(BiomeDictionary.Type.COLD))
			{
				if (biome != null)
				{
					ClimateAPI.register.addBiomeClimate(biome, dim, DCHeatTier.FROSTBITE, DCHumidity.WET, DCAirflow.NORMAL);
				}
			}
		}
	}
}