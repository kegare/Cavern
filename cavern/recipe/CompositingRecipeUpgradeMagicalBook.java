package cavern.recipe;

import java.util.Set;

import com.google.common.collect.Sets;

import cavern.api.ICompositingRecipe;
import cavern.item.CaveItems;
import cavern.item.ItemMagicalBook;
import cavern.item.ItemMagicalBook.EnumType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class CompositingRecipeUpgradeMagicalBook implements ICompositingRecipe
{
	private final NonNullList<ItemStack> materialItems = NonNullList.create();

	private EnumType targetType;

	@Override
	public NonNullList<ItemStack> getMaterialItems()
	{
		return materialItems;
	}

	@Override
	public boolean isMaterialItem(ItemStack stack)
	{
		return !stack.isEmpty() && stack.getItem() instanceof ItemMagicalBook;
	}

	@Override
	public boolean matches(IInventory inventory, World world, EntityPlayer player)
	{
		Set<EnumType> checkSet = Sets.newHashSet();

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack stack = inventory.getStackInSlot(i);

			if (!stack.isEmpty() && stack.getItem() instanceof ItemMagicalBook)
			{
				EnumType type = EnumType.byItemStack(stack);

				if (type.getMagicRarity() <= 0.0D || type.getMaxLevel() <= 1)
				{
					continue;
				}

				if (checkSet.contains(type))
				{
					targetType = type;

					return true;
				}

				checkSet.add(type);
			}
		}

		return false;
	}

	@Override
	public ItemStack getCompositingResult(IInventory inventory, World world, EntityPlayer player)
	{
		if (targetType == null)
		{
			return ItemStack.EMPTY;
		}

		materialItems.clear();

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack stack = inventory.getStackInSlot(i);

			if (!stack.isEmpty() && stack.getItem() instanceof ItemMagicalBook)
			{
				if (targetType == EnumType.byItemStack(stack))
				{
					materialItems.add(stack);
				}
			}
		}

		int resultLevel = 0;

		for (ItemStack stack : materialItems)
		{
			int level = CaveItems.MAGICAL_BOOK.getMagicLevel(stack);

			if (level >= 2 || resultLevel < 3)
			{
				++resultLevel;
			}
		}

		if (resultLevel <= 0)
		{
			return ItemStack.EMPTY;
		}

		int maxLevel = targetType.getMaxLevel();

		resultLevel = Math.min(resultLevel, maxLevel);

		if (maxLevel >= 3 && resultLevel >= maxLevel - 1)
		{
			if (Math.random() < 0.6D)
			{
				return ItemStack.EMPTY;
			}
		}
		else if (Math.random() < 0.3D)
		{
			return ItemStack.EMPTY;
		}

		return targetType.getItemStack(resultLevel);
	}

	@Override
	public int getCostMP(IInventory inventory, World world, EntityPlayer player)
	{
		int cost = 0;

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack stack = inventory.getStackInSlot(i);

			if (!stack.isEmpty() && stack.getItem() instanceof ItemMagicalBook)
			{
				if (targetType == EnumType.byItemStack(stack))
				{
					cost += 15 * CaveItems.MAGICAL_BOOK.getMagicLevel(stack);
				}
			}
		}

		return Math.min(cost, 300);
	}
}