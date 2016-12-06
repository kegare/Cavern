package cavern.client.config.dimension;

import cavern.client.config.CaveCategoryEntry;
import cavern.config.RuinsCavernConfig;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RuinsCavernConfigEntry extends CaveCategoryEntry
{
	public RuinsCavernConfigEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop)
	{
		super(owningScreen, owningEntryList, prop);
	}

	@Override
	protected Configuration getConfig()
	{
		return RuinsCavernConfig.config;
	}

	@Override
	protected String getEntryName()
	{
		return "dimension.ruinsCavern";
	}
}