package cavern.api;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface ICompositingManager
{
	public void addRecipe(ICompositingRecipe recipe);

	public void addRecipe(ItemStack result, double chance, int mp, ItemStack... materials);

	public void addRecipe(ItemStack result, double chance, int mp, NonNullList<ItemStack> materials);

	public void removeRecipes(ItemStack result);

	public List<ICompositingRecipe> getRecipes();
}