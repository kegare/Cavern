package cavern.client.gui;

import java.util.Collection;

import cavern.util.ItemMeta;

public interface IItemSelector
{
	public void onItemSelected(int id, Collection<ItemMeta> selected);

	public default boolean canSelectItem(int id, ItemMeta itemMeta)
	{
		return true;
	}
}