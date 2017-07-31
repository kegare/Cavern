package cavern.api;

import net.minecraft.nbt.NBTTagCompound;

public interface IMagicianStats
{
	public int getPoint();

	public void setPoint(int value);

	public void setPoint(int value, boolean adjust);

	public void addPoint(int value);

	public void addPoint(int value, boolean adjust);

	public int getRank();

	public void setRank(int value);

	public void setRank(int value, boolean adjust);

	public int getMP();

	public void setMP(int value);

	public void setMP(int value, boolean adjust);

	public void addMP(int value);

	public void addMP(int value, boolean adjust);

	public int getInfinity();

	public void setInfinity(int level, int time);

	public boolean isClientAdjusted();

	public void adjustData();

	public void onUpdate();

	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);
}