package cavern.api;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public interface ICompositingRecipe
{
	public NonNullList<ItemStack> getMaterialItems();

	public boolean isMaterialItem(ItemStack stack);

	public default boolean isItemMatch(ItemStack material, ItemStack stack)
	{
		if (material.isEmpty() || stack.isEmpty())
		{
			return false;
		}

		if (stack.getCount() < material.getCount())
		{
			return false;
		}

		if (material.getHasSubtypes())
		{
			return OreDictionary.itemMatches(material, stack, false);
		}

		return stack.getItem() == material.getItem();
	}

	public boolean matches(IInventory inventory, World world, @Nullable EntityPlayer player);

	public ItemStack getCompositingResult(IInventory inventory, World world, @Nullable EntityPlayer player);

	public int getCostMP(IInventory inventory, World world, @Nullable EntityPlayer player);

	public default ItemStack getRecipeOutput()
	{
		return ItemStack.EMPTY;
	}
}