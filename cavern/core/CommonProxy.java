package cavern.core;

import net.minecraft.util.text.translation.I18n;

public class CommonProxy
{
	public void initConfigEntries() {}

	public void registerRenderers() {}

	public String translate(String key)
	{
		return I18n.translateToLocal(key);
	}

	public String translateFormat(String key, Object... format)
	{
		return I18n.translateToLocalFormatted(key, format);
	}
}