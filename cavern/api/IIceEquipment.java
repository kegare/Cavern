package cavern.api;

import net.minecraft.nbt.NBTTagCompound;

public interface IIceEquipment
{
	public int getCharge();

	public void setCharge(int amount);

	public void addCharge(int amount);

	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);
}