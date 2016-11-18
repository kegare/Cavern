package cavern.api;

import net.minecraft.nbt.NBTTagCompound;

public interface IMinerStats
{
	public int getPoint();

	public void setPoint(int value);

	public void setPoint(int value, boolean adjust);

	public void addPoint(int value);

	public void addPoint(int value, boolean adjust);

	public int getRank();

	public void setRank(int value);

	public void setRank(int value, boolean adjust);

	public int getMiningAssist();

	public void setMiningAssist(int type);

	public void setMiningAssist(int type, boolean adjust);

	public void toggleMiningAssist();

	public void adjustData();

	public void adjustClientData();

	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);
}