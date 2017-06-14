package cavern.recipe;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.api.ICompositingRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class CompositingRecipeBasic implements ICompositingRecipe
{
	private final ItemStack resultItem;
	private final double compositingChance;
	private final int costMP;
	private final NonNullList<ItemStack> materialItems;

	private List<ItemStack> checkItems;

	public CompositingRecipeBasic(ItemStack result, double chance, int mp, NonNullList<ItemStack> materials)
	{
		this.resultItem = result;
		this.compositingChance = chance;
		this.costMP = mp;
		this.materialItems = materials;
	}

	public CompositingRecipeBasic(ItemStack result, double chance, int mp, ItemStack... materials)
	{
		this(result, chance, mp, NonNullList.create());

		for (ItemStack stack : materials)
		{
			if (!stack.isEmpty())
			{
				materialItems.add(stack);
			}
		}
	}

	@Override
	public NonNullList<ItemStack> getMaterialItems()
	{
		return materialItems;
	}

	@Override
	public boolean matches(IInventory inventory, World world, EntityPlayer player)
	{
		checkItems = Lists.newArrayList(materialItems);

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack stack = inventory.getStackInSlot(i);
			ItemStack check = checkMatch(stack);

			if (!check.isEmpty())
			{
				checkItems.remove(check);
			}
		}

		boolean match = checkItems.isEmpty();

		checkItems = null;

		return match;
	}

	protected ItemStack checkMatch(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return ItemStack.EMPTY;
		}

		List<ItemStack> list;

		if (checkItems != null)
		{
			list = checkItems;
		}
		else
		{
			list = materialItems;
		}

		for (ItemStack material : list)
		{
			if (isItemMatch(material, stack))
			{
				return material;
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack getCompositingResult(IInventory inventory, World world, EntityPlayer player)
	{
		return compositingChance >= 1.0D || Math.random() < compositingChance ? resultItem.copy() : ItemStack.EMPTY;
	}

	@Override
	public int getCostMP(IInventory inventory, World world, EntityPlayer player)
	{
		return costMP;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return resultItem;
	}

	public static boolean isItemMatch(ItemStack material, ItemStack stack)
	{
		if (material.isEmpty() || stack.isEmpty())
		{
			return false;
		}

		if (!ItemStack.areItemsEqual(material, stack) || !ItemStack.areItemStackTagsEqual(material, stack))
		{
			return false;
		}

		return stack.getCount() >= material.getCount();
	}
}