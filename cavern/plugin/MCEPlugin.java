package cavern.plugin;

import net.minecraftforge.fml.common.Optional.Method;

public class MCEPlugin
{
	public static final String MODID = "mceconomy3";

	public static int PORTAL_SHOP = -1;

	@Method(modid = MODID)
	public static void load()
	{
		MCEPluginWrapper.registerShops();
		MCEPluginWrapper.registerPurchaseItems();
	}
}