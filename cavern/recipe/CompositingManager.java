package cavern.recipe;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import cavern.api.ICompositingManager;
import cavern.api.ICompositingRecipe;
import cavern.item.CaveItems;
import cavern.item.ItemAcresia;
import cavern.item.ItemCave;
import cavern.item.ItemElixir;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;

public class CompositingManager implements ICompositingManager
{
	private final List<ICompositingRecipe> recipes = Lists.newArrayList();

	@Override
	public void addRecipe(ICompositingRecipe recipe)
	{
		recipes.add(recipe);
	}

	@Override
	public void addRecipe(ItemStack result, double chance, int mp, ItemStack... materials)
	{
		recipes.add(new CompositingRecipeBasic(result, chance, mp, materials));
	}

	@Override
	public void addRecipe(ItemStack result, double chance, int mp, NonNullList<ItemStack> materials)
	{
		recipes.add(new CompositingRecipeBasic(result, chance, mp, materials));
	}

	@Override
	public void removeRecipes(ItemStack result)
	{
		Iterator<ICompositingRecipe> iterator = recipes.iterator();

		while (iterator.hasNext())
		{
			ICompositingRecipe recipe = iterator.next();
			ItemStack output = recipe.getRecipeOutput();

			if (!output.isEmpty() && ItemStack.areItemStacksEqual(output, result))
			{
				iterator.remove();
			}
		}
	}

	@Override
	public List<ICompositingRecipe> getRecipes()
	{
		return recipes;
	}

	public static void registerRecipes(ICompositingManager manager)
	{
		manager.addRecipe(new CompositingRecipeUpgradeMagicalBook());

		manager.addRecipe(ItemElixir.EnumType.ELIXIR_NORMAL.getItemStack(), 1.0D, 50,
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER),
			ItemAcresia.EnumType.FRUITS.getItemStack(), ItemCave.EnumType.AQUAMARINE.getItemStack());
		manager.addRecipe(ItemElixir.EnumType.ELIXIR_MEDIUM.getItemStack(), 0.55D, 100,
			ItemElixir.EnumType.ELIXIR_NORMAL.getItemStack(),
			ItemAcresia.EnumType.FRUITS.getItemStack(3), new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()));
		manager.addRecipe(ItemElixir.EnumType.ELIXIR_HIGH.getItemStack(), 0.2D, 250,
			ItemElixir.EnumType.ELIXIR_MEDIUM.getItemStack(),
			ItemAcresia.EnumType.FRUITS.getItemStack(5), ItemCave.EnumType.MAGNITE_INGOT.getItemStack());

		manager.addRecipe(new ItemStack(CaveItems.CAVENIC_BOW), 0.5D, 100,
			new ItemStack(Items.BOW), ItemCave.EnumType.CAVENIC_ORB.getItemStack(4));
	}
}