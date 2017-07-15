package cavern.recipe;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import cavern.api.ICompositingManager;
import cavern.api.ICompositingRecipe;
import cavern.item.CaveItems;
import cavern.item.ItemAcresia;
import cavern.item.ItemCave;
import cavern.item.ItemElixir;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
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
		manager.addRecipe(new CompositingRecipeRepair());

		manager.addRecipe(ItemElixir.EnumType.ELIXIR_NORMAL.getItemStack(), 1.0D, 50,
			PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER),
			ItemAcresia.EnumType.FRUITS.getItemStack(), ItemCave.EnumType.AQUAMARINE.getItemStack());
		manager.addRecipe(ItemElixir.EnumType.ELIXIR_MEDIUM.getItemStack(), 0.65D, 100,
			ItemElixir.EnumType.ELIXIR_NORMAL.getItemStack(),
			ItemAcresia.EnumType.FRUITS.getItemStack(3), new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage()));
		manager.addRecipe(ItemElixir.EnumType.ELIXIR_HIGH.getItemStack(), 0.35D, 250,
			ItemElixir.EnumType.ELIXIR_MEDIUM.getItemStack(),
			ItemAcresia.EnumType.FRUITS.getItemStack(5), ItemCave.EnumType.MAGNITE_INGOT.getItemStack());

		manager.addRecipe(new ItemStack(CaveItems.CAVENIC_BOW), 0.7D, 100,
			new ItemStack(Items.BOW), ItemCave.EnumType.CAVENIC_ORB.getItemStack(4));

		List<Pair<Item, Pair<Item, Item>>> compositeRecipes = Lists.newArrayList();

		compositeRecipes.add(Pair.of(CaveItems.COMPOSITE_SWORD, Pair.of(CaveItems.HEXCITE_SWORD, Items.DIAMOND_SWORD)));
		compositeRecipes.add(Pair.of(CaveItems.COMPOSITE_PICKAXE, Pair.of(CaveItems.HEXCITE_PICKAXE, Items.DIAMOND_PICKAXE)));
		compositeRecipes.add(Pair.of(CaveItems.COMPOSITE_AXE, Pair.of(CaveItems.HEXCITE_AXE, Items.DIAMOND_AXE)));
		compositeRecipes.add(Pair.of(CaveItems.COMPOSITE_SHOVEL, Pair.of(CaveItems.HEXCITE_SHOVEL, Items.DIAMOND_SHOVEL)));
		compositeRecipes.add(Pair.of(CaveItems.COMPOSITE_HOE, Pair.of(CaveItems.HEXCITE_HOE, Items.DIAMOND_HOE)));
		compositeRecipes.add(Pair.of(CaveItems.COMPOSITE_HELMET, Pair.of(CaveItems.HEXCITE_HELMET, Items.DIAMOND_HELMET)));
		compositeRecipes.add(Pair.of(CaveItems.COMPOSITE_CHESTPLATE, Pair.of(CaveItems.HEXCITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE)));
		compositeRecipes.add(Pair.of(CaveItems.COMPOSITE_LEGGINGS, Pair.of(CaveItems.HEXCITE_LEGGINGS, Items.DIAMOND_LEGGINGS)));
		compositeRecipes.add(Pair.of(CaveItems.COMPOSITE_BOOTS, Pair.of(CaveItems.HEXCITE_BOOTS, Items.DIAMOND_BOOTS)));

		for (Pair<Item, Pair<Item, Item>> entry : compositeRecipes)
		{
			manager.addRecipe(new ItemStack(entry.getLeft()), 0.55D, 100,
				new ItemStack(entry.getRight().getLeft()), new ItemStack(entry.getRight().getRight()));
		}

		manager.addRecipe(new ItemStack(Items.DIAMOND), 0.05D, 150, new ItemStack(Items.COAL, 64, 0));
		manager.addRecipe(new ItemStack(Blocks.PACKED_ICE), 1.0D, 50, new ItemStack(Blocks.ICE, 9));
	}
}