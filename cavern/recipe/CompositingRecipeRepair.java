package cavern.recipe;

import cavern.api.ICompositingRecipe;
import cavern.item.CaveItems;
import cavern.item.ItemCave;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CompositingRecipeRepair implements ICompositingRecipe
{
	private final NonNullList<ItemStack> materialItems = NonNullList.create();

	private ItemStack repairItem;
	private ItemStack materialItem;

	@Override
	public NonNullList<ItemStack> getMaterialItems()
	{
		return materialItems;
	}

	@Override
	public boolean isMaterialItem(ItemStack stack)
	{
		return !stack.isEmpty() && (stack.isItemDamaged() || stack.getItem() == CaveItems.CAVE_ITEM && stack.getMetadata() == ItemCave.EnumType.MANALITE.getMetadata());
	}

	@Override
	public boolean matches(IInventory inventory, World world, EntityPlayer player)
	{
		materialItems.clear();

		repairItem = ItemStack.EMPTY;
		materialItem = ItemStack.EMPTY;

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack stack = inventory.getStackInSlot(i);

			if (!stack.isEmpty())
			{
				if (repairItem.isEmpty() && stack.isItemDamaged())
				{
					repairItem = stack;
				}

				if (materialItem.isEmpty() && stack.getItem() == CaveItems.CAVE_ITEM && stack.getMetadata() == ItemCave.EnumType.MANALITE.getMetadata())
				{
					materialItem = stack;
				}
			}
		}

		if (repairItem.isEmpty() || materialItem.isEmpty())
		{
			return false;
		}

		materialItems.add(repairItem);
		materialItems.add(materialItem);

		return true;
	}

	@Override
	public ItemStack getCompositingResult(IInventory inventory, World world, EntityPlayer player)
	{
		return getRecipeOutput();
	}

	@Override
	public int getCostMP(IInventory inventory, World world, EntityPlayer player)
	{
		if (materialItems.isEmpty())
		{
			return 0;
		}

		int i = Math.min(repairItem.getItemDamage(), repairItem.getMaxDamage() / 4);

		return MathHelper.clamp(i, 10, 200);
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		if (materialItems.isEmpty())
		{
			return ItemStack.EMPTY;
		}

		ItemStack result = repairItem.copy();

		result.setItemDamage(0);

		return result;
	}
}