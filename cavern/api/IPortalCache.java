package cavern.api;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;

public interface IPortalCache
{
	public DimensionType getLastDim(ResourceLocation key);

	public DimensionType getLastDim(ResourceLocation key, @Nullable DimensionType nullDefault);

	public void setLastDim(ResourceLocation key, DimensionType type);

	@Nullable
	public BlockPos getLastPos(ResourceLocation key, DimensionType type);

	public BlockPos getLastPos(ResourceLocation key, DimensionType type, @Nullable BlockPos pos);

	public boolean hasLastPos(ResourceLocation key, DimensionType type);

	public void setLastPos(ResourceLocation key, DimensionType type, @Nullable BlockPos pos);

	public void clearLastPos(@Nullable ResourceLocation key, DimensionType type);

	public void writeToNBT(NBTTagCompound nbt);

	public void readFromNBT(NBTTagCompound nbt);
}