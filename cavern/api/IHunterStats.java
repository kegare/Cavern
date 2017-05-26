package cavern.api;

import net.minecraft.nbt.NBTTagCompound;

public interface IHunterStats
{
	public int getPoint();

	public void setPoint(int value);

	public void setPoint(int value, boolean adjust);

	public void addPoint(int value);

	public void addPoint(int value, boolean adjust);

	public int getRank();

	public void setRank(int value);

	public void setRank(int value, boolean adjust);

	public boolean isClientAdjusted();

	public void adjustData();

	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);
}