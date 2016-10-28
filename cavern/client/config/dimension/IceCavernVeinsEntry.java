package cavern.client.config.dimension;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import cavern.client.gui.GuiVeinsEditor;
import cavern.config.IceCavernConfig;
import cavern.config.manager.CaveVeinManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.CategoryEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IceCavernVeinsEntry extends CategoryEntry
{
	public IceCavernVeinsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
	{
		super(owningScreen, owningEntryList, configElement);
	}

	@Override
	protected GuiScreen buildChildScreen()
	{
		return new GuiVeinsEditor(owningScreen, IceCavernConfig.veinManager);
	}

	@Override
	public boolean isDefault()
	{
		return false;
	}

	@Override
	public void setToDefault()
	{
		CaveVeinManager manager = IceCavernConfig.veinManager;

		try
		{
			FileUtils.forceDelete(new File(manager.config.toString()));
		}
		catch (IOException e)
		{
			e.printStackTrace();

			return;
		}

		manager.getCaveVeins().clear();

		manager.config = null;
		IceCavernConfig.syncVeinsConfig();

		if (childScreen != null && childScreen instanceof GuiVeinsEditor)
		{
			((GuiVeinsEditor)childScreen).refreshVeins(manager.getCaveVeins());
		}
	}
}