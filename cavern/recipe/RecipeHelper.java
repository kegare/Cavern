package cavern.recipe;

import cavern.util.CaveUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeHelper
{
	public static IRecipe getRecipe(String name, IRecipe recipe)
	{
		return recipe.setRegistryName(CaveUtils.getKey(name));
	}

	public static IRecipe getShapedRecipe(String group, String name, ItemStack result, Object... recipe)
	{
		return new ShapedOreRecipe(CaveUtils.getKey(group), result, recipe).setRegistryName(name);
	}

	public static IRecipe getShapedRecipe(String name, ItemStack result, Object... recipe)
	{
		return getShapedRecipe(name, name, result, recipe);
	}

	public static IRecipe getShapelessRecipe(String group, String name, ItemStack result, Object... recipe)
	{
		return new ShapelessOreRecipe(CaveUtils.getKey(group), result, recipe).setRegistryName(name);
	}

	public static IRecipe getShapelessRecipe(String name, ItemStack result, Object... recipe)
	{
		return getShapelessRecipe(name, name, result, recipe);
	}

	public static IRecipe getSquareRecipe(String group, String name, ItemStack result, Object recipe)
	{
		return getShapedRecipe(group, name, result, "XXX", "XXX", "XXX", 'X', recipe);
	}

	public static IRecipe getSquareRecipe(String name, ItemStack result, Object recipe)
	{
		return getSquareRecipe(name, name, result, recipe);
	}

	public static IRecipe getSmallSquareRecipe(String group, String name, ItemStack result, Object recipe)
	{
		return getShapedRecipe(group, name, result, "XX", "XX", 'X', recipe);
	}

	public static IRecipe getSmallSquareRecipe(String name, ItemStack result, Object recipe)
	{
		return getSquareRecipe(name, name, result, recipe);
	}

	public static IRecipe getSurroundRecipe(String group, String name, ItemStack result, Object center, Object recipe)
	{
		return getShapedRecipe(group, name, result, "XXX", "X#X", "XXX", 'X', recipe, '#', center);
	}

	public static IRecipe getSurroundRecipe(String name, ItemStack result, Object center, Object recipe)
	{
		return getSurroundRecipe(name, name, result, center, recipe);
	}

	public static IRecipe getSmallSurroundRecipe(String group, String name, ItemStack result, Object center, Object recipe)
	{
		return getShapedRecipe(group, name, result, " X ", "X#X", " X ", 'X', recipe, '#', center);
	}

	public static IRecipe getSmallSurroundRecipe(String name, ItemStack result, Object center, Object recipe)
	{
		return getSurroundRecipe(name, name, result, center, recipe);
	}

	public static IRecipe getRodRecipe(String group, String name, ItemStack result, Object recipe)
	{
		return getShapedRecipe(group, name, result, "X", "X", 'X', recipe);
	}

	public static IRecipe getRodRecipe(String name, ItemStack result, Object recipe)
	{
		return getRodRecipe(name, name, result, recipe);
	}

	public static IRecipe getSwordRecipe(String group, String name, ItemStack result, Object material, Object stick)
	{
		return getShapedRecipe(group, name, result, "#", "#", "X", '#', material, 'X', stick);
	}

	public static IRecipe getSwordRecipe(String name, ItemStack result, Object material, Object stick)
	{
		return getSwordRecipe(name, name, result, material, stick);
	}

	public static IRecipe getSwordRecipe(String group, String name, ItemStack result, Object material)
	{
		return getSwordRecipe(group, name, result, material, "stickWood");
	}

	public static IRecipe getSwordRecipe(String name, ItemStack result, Object material)
	{
		return getSwordRecipe(name, result, material, "stickWood");
	}

	public static IRecipe getPickaxeRecipe(String group, String name, ItemStack result, Object material, Object stick)
	{
		return getShapedRecipe(group, name, result, "###", " X ", " X ", '#', material, 'X', stick);
	}

	public static IRecipe getPickaxeRecipe(String name, ItemStack result, Object material, Object stick)
	{
		return getPickaxeRecipe(name, name, result, material, stick);
	}

	public static IRecipe getPickaxeRecipe(String group, String name, ItemStack result, Object material)
	{
		return getPickaxeRecipe(group, name, result, material, "stickWood");
	}

	public static IRecipe getPickaxeRecipe(String name, ItemStack result, Object material)
	{
		return getPickaxeRecipe(name, result, material, "stickWood");
	}

	public static IRecipe getAxeRecipe(String group, String name, ItemStack result, Object material, Object stick)
	{
		return getShapedRecipe(group, name, result, "##", "#X", " X", '#', material, 'X', stick);
	}

	public static IRecipe getAxeRecipe(String name, ItemStack result, Object material, Object stick)
	{
		return getAxeRecipe(name, name, result, material, stick);
	}

	public static IRecipe getAxeRecipe(String group, String name, ItemStack result, Object material)
	{
		return getAxeRecipe(group, name, result, material, "stickWood");
	}

	public static IRecipe getAxeRecipe(String name, ItemStack result, Object material)
	{
		return getAxeRecipe(name, result, material, "stickWood");
	}

	public static IRecipe getShovelRecipe(String group, String name, ItemStack result, Object material, Object stick)
	{
		return getShapedRecipe(group, name, result, "#", "X", "X", '#', material, 'X', stick);
	}

	public static IRecipe getShovelRecipe(String name, ItemStack result, Object material, Object stick)
	{
		return getShovelRecipe(name, name, result, material, stick);
	}

	public static IRecipe getShovelRecipe(String group, String name, ItemStack result, Object material)
	{
		return getShovelRecipe(group, name, result, material, "stickWood");
	}

	public static IRecipe getShovelRecipe(String name, ItemStack result, Object material)
	{
		return getShovelRecipe(name, result, material, "stickWood");
	}

	public static IRecipe getHoeRecipe(String group, String name, ItemStack result, Object material, Object stick)
	{
		return getShapedRecipe(group, name, result, "##", " X", " X", '#', material, 'X', stick);
	}

	public static IRecipe getHoeRecipe(String name, ItemStack result, Object material, Object stick)
	{
		return getHoeRecipe(name, name, result, material, stick);
	}

	public static IRecipe getHoeRecipe(String group, String name, ItemStack result, Object material)
	{
		return getHoeRecipe(group, name, result, material, "stickWood");
	}

	public static IRecipe getHoeRecipe(String name, ItemStack result, Object material)
	{
		return getHoeRecipe(name, result, material, "stickWood");
	}

	public static IRecipe getHelmetRecipe(String group, String name, ItemStack result, Object material)
	{
		return getShapedRecipe(group, name, result, "###", "# #", '#', material);
	}

	public static IRecipe getHelmetRecipe(String name, ItemStack result, Object material)
	{
		return getHelmetRecipe(name, name, result, material);
	}

	public static IRecipe getChestplateRecipe(String group, String name, ItemStack result, Object material)
	{
		return getShapedRecipe(group, name, result, "# #", "###", "###", '#', material);
	}

	public static IRecipe getChestplateRecipe(String name, ItemStack result, Object material)
	{
		return getChestplateRecipe(name, name, result, material);
	}

	public static IRecipe getLeggingsRecipe(String group, String name, ItemStack result, Object material)
	{
		return getShapedRecipe(group, name, result, "###", "# #", "# #", '#', material);
	}

	public static IRecipe getLeggingsRecipe(String name, ItemStack result, Object material)
	{
		return getLeggingsRecipe(name, name, result, material);
	}

	public static IRecipe getBootsRecipe(String group, String name, ItemStack result, Object material)
	{
		return getShapedRecipe(group, name, result, "# #", "# #", '#', material);
	}

	public static IRecipe getBootsRecipe(String name, ItemStack result, Object material)
	{
		return getBootsRecipe(name, name, result, material);
	}

	public static IRecipe getBowRecipe(String group, String name, ItemStack result, Object material, Object string)
	{
		return getShapedRecipe(group, name, result, "X# ", "X #", "X# ", '#', material, 'X', string);
	}

	public static IRecipe getBowRecipe(String name, ItemStack result, Object material, Object string)
	{
		return getBowRecipe(name, name, result, material, string);
	}

	public static IRecipe getBowRecipe(String group, String name, ItemStack result, Object material)
	{
		return getBowRecipe(group, name, result, material, new ItemStack(Items.STRING));
	}

	public static IRecipe getBowRecipe(String name, ItemStack result, Object material)
	{
		return getBowRecipe(name, result, material, new ItemStack(Items.STRING));
	}
}