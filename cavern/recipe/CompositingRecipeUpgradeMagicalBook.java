package cavern.recipe;

import cavern.api.ICompositingRecipe;
import cavern.item.ItemMagicalBook;
import cavern.item.ItemMagicalBook.EnumType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CompositingRecipeUpgradeMagicalBook implements ICompositingRecipe
{
	private final NonNullList<ItemStack> materialItems = NonNullList.create();

	private EnumType bookType;
	private int resultLevel;

	@Override
	public NonNullList<ItemStack> getMaterialItems()
	{
		if (bookType == null)
		{
			materialItems.clear();
		}

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
		materialItems.clear();
		bookType = null;

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

				if (bookType == null)
				{
					bookType = type;

					materialItems.add(stack);
				}
				else if (bookType == type)
				{
					materialItems.add(stack);
				}
				else return false;
			}
		}

		if (bookType == null)
		{
			return false;
		}

		resultLevel = 0;

		for (ItemStack stack : materialItems)
		{
			resultLevel += MathHelper.clamp(ItemMagicalBook.getMagicLevel(stack), 1, bookType.getMaxLevel());
		}

		if (resultLevel > bookType.getMaxLevel())
		{
			bookType = null;

			return false;
		}

		return true;
	}

	@Override
	public ItemStack getCompositingResult(IInventory inventory, World world, EntityPlayer player)
	{
		if (bookType == null || resultLevel <= 1)
		{
			return ItemStack.EMPTY;
		}

		if (resultLevel > 2)
		{
			int max = bookType.getMaxLevel();

			if (max >= 3 && resultLevel >= max - 1)
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
		}

		return bookType.getItemStack(resultLevel);
	}

	@Override
	public int getCostMP(IInventory inventory, World world, EntityPlayer player)
	{
		return 25 * resultLevel;
	}
}