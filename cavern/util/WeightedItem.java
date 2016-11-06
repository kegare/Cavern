package cavern.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public class WeightedItem extends WeightedRandom.Item
{
	private final ItemStack item;

	public WeightedItem(ItemStack item, int weight)
	{
		super(weight);
		this.item = item;
	}

	public ItemStack getItem()
	{
		return ItemStack.copyItemStack(item);
	}
}