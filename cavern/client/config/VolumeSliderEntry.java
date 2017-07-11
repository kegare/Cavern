package cavern.client.config;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.NumberSliderEntry;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VolumeSliderEntry extends NumberSliderEntry
{
	public VolumeSliderEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
	{
		super(owningScreen, owningEntryList, configElement);
		this.updateSlider();
	}

	public void updateSlider()
	{
		GuiSlider slider = (GuiSlider)btnValue;

		slider.dispString = I18n.format("gui.volume") + ": ";
		slider.suffix = "%";
		slider.updateSlider();
	}
}