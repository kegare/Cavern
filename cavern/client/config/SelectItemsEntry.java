package cavern.client.config;

import java.util.Collection;

import cavern.client.gui.GuiSelectItem;
import cavern.client.gui.IItemSelector;
import cavern.util.ItemMeta;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ArrayEntry;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SelectItemsEntry extends ArrayEntry implements IItemSelector
{
	public SelectItemsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
	{
		super(owningScreen, owningEntryList, configElement);
	}

	@Override
	public void valueButtonPressed(int index)
	{
		if (GuiScreen.isShiftKeyDown())
		{
			super.valueButtonPressed(index);
		}
		else if (btnValue.enabled)
		{
			btnValue.playPressSound(mc.getSoundHandler());

			mc.displayGuiScreen(new GuiSelectItem(owningScreen, this, this, 0));
		}
	}

	@Override
	public void onItemSelected(int id, Collection<ItemMeta> selected) {}

	@Override
	public boolean canSelectItem(int id, ItemMeta itemMeta)
	{
		return Block.getBlockFromItem(itemMeta.getItem()) == null;
	}
}