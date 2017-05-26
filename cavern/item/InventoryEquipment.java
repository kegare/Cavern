package cavern.item;

import cavern.api.IInventoryEquipment;
import cavern.capability.CaveCapabilities;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class InventoryEquipment implements IInventoryEquipment
{
	private String inventoryTitle;
	private IInventory inventory;

	private int inventorySize;

	public InventoryEquipment(String title)
	{
		this.inventoryTitle = title;
	}

	@Override
	public IInventory getInventory()
	{
		return inventory;
	}

	@Override
	public void setSize(int size)
	{
		if (inventory == null)
		{
			inventory = new InventoryBasic(inventoryTitle, false, 9 * size);
			inventorySize = size;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		if (inventory == null)
		{
			return;
		}

		nbt.setInteger("Size", inventorySize);

		NBTTagList list = new NBTTagList();

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack itemstack = inventory.getStackInSlot(i);

			if (!itemstack.isEmpty())
			{
				NBTTagCompound compound = new NBTTagCompound();
				compound.setByte("Slot", (byte)i);
				itemstack.writeToNBT(compound);
				list.appendTag(compound);
			}
		}

		nbt.setTag("Items", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		if (nbt.hasKey("Size", NBT.TAG_ANY_NUMERIC))
		{
			setSize(nbt.getInteger("Size"));
		}
		else return;

		NBTTagList list = nbt.getTagList("Items", NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound compound = list.getCompoundTagAt(i);
			int j = compound.getByte("Slot") & 255;

			if (j >= 0 && j < inventory.getSizeInventory())
			{
				inventory.setInventorySlotContents(j, new ItemStack(compound));
			}
		}
	}

	public static IInventoryEquipment get(ItemStack item)
	{
		IInventoryEquipment equip = CaveCapabilities.getCapability(item, CaveCapabilities.INVENTORY_EQUIP);

		if (equip == null)
		{
			return new InventoryEquipment(item.getUnlocalizedName());
		}

		return equip;
	}

	public static IInventory getInventory(ItemStack item, int size)
	{
		IInventoryEquipment equip = get(item);

		equip.setSize(size);

		return equip.getInventory();
	}
}