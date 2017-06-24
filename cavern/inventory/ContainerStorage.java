package cavern.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerStorage extends Container
{
	private final IInventory storageInventory;
	private final int numRows;

	public ContainerStorage(IInventory playerInventory, IInventory storageInventory, EntityPlayer player)
	{
		this.storageInventory = storageInventory;
		this.numRows = storageInventory.getSizeInventory() / 9;

		storageInventory.openInventory(player);

		for (int j = 0; j < numRows; ++j)
		{
			for (int k = 0; k < 9; ++k)
			{
				addSlotToContainer(new SlotStorage(storageInventory, k + j * 9, 8 + k * 18, 18 + j * 18).setCanPut(false));
			}
		}

		int i = (numRows - 4) * 18;

		for (int j = 0; j < 3; ++j)
		{
			for (int k = 0; k < 9; ++k)
			{
				addSlotToContainer(new SlotStorage(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i).setCanTake(false));
			}
		}

		for (int j = 0; j < 9; ++j)
		{
			addSlotToContainer(new SlotStorage(playerInventory, j, 8 + j * 18, 161 + i).setCanTake(false));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return storageInventory.isUsableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index)
	{
		ItemStack result = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(index);

		if (slot != null && slot.getHasStack())
		{
			ItemStack stack = slot.getStack();
			result = stack.copy();

			if (index < numRows * 9)
			{
				if (!mergeItemStack(stack, numRows * 9, inventorySlots.size(), true))
				{
					return ItemStack.EMPTY;
				}
			}
			else if (!mergeItemStack(stack, 0, numRows * 9, false))
			{
				return ItemStack.EMPTY;
			}

			if (stack.isEmpty())
			{
				slot.putStack(ItemStack.EMPTY);
			}
			else
			{
				slot.onSlotChanged();
			}
		}

		return result;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);

		storageInventory.closeInventory(player);
	}
}