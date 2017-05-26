package cavern.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

public interface IInventoryEquipment
{
	public IInventory getInventory();

	public void setSize(int size);

	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);
}