package cavern.plugin;

import cavern.item.CaveItems;
import defeatedcrow.hac.api.damage.DamageAPI;
import net.minecraftforge.fml.common.Optional.Method;

public class HaCPlugin
{
	public static final String LIB_MODID = "dcs_climate|lib";

	@Method(modid = LIB_MODID)
	public static void load()
	{
		DamageAPI.armorRegister.RegisterMaterial(CaveItems.HEXCITE_ARMOR, 0.75F);
	}
}