package cavern.api;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.DimensionType;

public interface IPlayerData
{
	public long getLastTeleportTime(DimensionType type);

	public void setLastTeleportTime(DimensionType type, long time);

	public long getLastSleepTime();

	public void setLastSleepTime(long time);

	@Nullable
	public NBTTagList getInventoryCache();

	public void setInventoryCache(@Nullable NBTTagList list);

	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);
}