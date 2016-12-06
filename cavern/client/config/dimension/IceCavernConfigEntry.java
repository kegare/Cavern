package cavern.client.config.dimension;

import java.util.List;

import cavern.client.config.CaveCategoryEntry;
import cavern.config.Config;
import cavern.config.IceCavernConfig;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IceCavernConfigEntry extends CaveCategoryEntry
{
	public IceCavernConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return IceCavernConfig.config;
	}

	@Override
	protected String getEntryName()
	{
		return "dimension.iceCavern";
	}

	@Override
	protected List<IConfigElement> getConfigElements()
	{
		List<IConfigElement> ret = super.getConfigElements();

		ret.add(new DummyCategoryElement("cavern:iceCavernVeins", Config.LANG_KEY + "veins", IceCavernVeinsEntry.class));

		return ret;
	}
}