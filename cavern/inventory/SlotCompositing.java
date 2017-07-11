package cavern.inventory;

import cavern.api.CavernAPI;
import cavern.api.ICompositingRecipe;
import cavern.item.ItemMagicalBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCompositing extends Slot
{
	private boolean canTake = true;
	private boolean canPut = true;

	public SlotCompositing(IInventory inventory, int index, int xPosition, int yPosition)
	{
		super(inventory, index, xPosition, yPosition);
	}

	public SlotCompositing setCanTake(boolean take)
	{
		canTake = take;

		return this;
	}

	public SlotCompositing setCanPut(boolean put)
	{
		canPut = put;

		return this;
	}

	public boolean isValidStack(ItemStack stack)
	{
		if (!stack.isEmpty() && stack.getItem() instanceof ItemMagicalBook)
		{
			if (stack.getMetadata() == ItemMagicalBook.EnumType.COMPOSITING.getMetadata())
			{
				return false;
			}
		}

		return true;
	}

	public boolean isMaterialItem(ItemStack stack)
	{
		for (ICompositingRecipe recipe : CavernAPI.compositing.getRecipes())
		{
			if (recipe.isMaterialItem(stack))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		return (canTake || isValidStack(getStack())) && super.canTakeStack(player);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return (canPut || isValidStack(stack) && isMaterialItem(stack)) && super.isItemValid(stack);
	}
}