package cavern.block.bonus;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public class RandomiteItem extends WeightedRandom.Item
{
	private final ItemStack item;

	public RandomiteItem(ItemStack item, int weight)
	{
		super(weight);
		this.item = item;
	}

	public ItemStack getItem()
	{
		return ItemStack.copyItemStack(item);
	}
}