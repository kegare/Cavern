package cavern.api;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

public interface IInventoryEquipment
{
	@Nullable
	public IInventory getInventory();

	public void setInventory(@Nullable IInventory inventory);

	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);
}