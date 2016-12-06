package cavern.stats;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import cavern.capability.CaveCapabilities;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

public class PortalCache implements IPortalCache
{
	private final Map<Integer, Integer> lastDim = Maps.newHashMap();
	private final Table<Integer, Integer, BlockPos> lastPos = HashBasedTable.create();

	public static IPortalCache get(Entity entity)
	{
		IPortalCache cache = CaveCapabilities.getCapability(entity, CaveCapabilities.PORTAL_CACHE);

		if (cache == null)
		{
			return new PortalCache();
		}

		return cache;
	}

	@Override
	public int getLastDim(int type)
	{
		Integer ret = lastDim.get(Integer.valueOf(type));

		if (ret == null)
		{
			return 0;
		}

		return ret.intValue();
	}

	@Override
	public void setLastDim(int type, int dim)
	{
		lastDim.put(Integer.valueOf(type), Integer.valueOf(dim));
	}

	@Override
	public BlockPos getLastPos(int type, int dim)
	{
		return lastPos.get(Integer.valueOf(type), Integer.valueOf(dim));
	}

	@Override
	public BlockPos getLastPos(int type, int dim, BlockPos pos)
	{
		BlockPos ret = getLastPos(type, dim);

		return ret == null ? pos == null ? BlockPos.ORIGIN : pos : ret;
	}

	@Override
	public boolean hasLastPos(int type, int dim)
	{
		return lastPos.contains(Integer.valueOf(type), Integer.valueOf(dim));
	}

	@Override
	public void setLastPos(int type, int dim, BlockPos pos)
	{
		if (pos == null)
		{
			lastPos.remove(Integer.valueOf(type), Integer.valueOf(dim));
		}
		else
		{
			lastPos.put(Integer.valueOf(type), Integer.valueOf(dim), pos);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList list = new NBTTagList();

		for (Entry<Integer, Integer> entry : lastDim.entrySet())
		{
			Integer typeObj = entry.getKey();
			Integer dimObj = entry.getValue();

			if (typeObj != null && dimObj != null)
			{
				NBTTagCompound data = new NBTTagCompound();

				data.setInteger("Type", typeObj.intValue());
				data.setInteger("Dim", dimObj.intValue());

				list.appendTag(data);
			}
		}

		nbt.setTag("LastDim", list);

		list = new NBTTagList();

		for (Cell<Integer, Integer, BlockPos> entry : lastPos.cellSet())
		{
			Integer typeObj = entry.getRowKey();
			Integer dimObj = entry.getColumnKey();
			BlockPos pos = entry.getValue();

			if (typeObj != null && dimObj != null && pos != null)
			{
				NBTTagCompound data = new NBTTagCompound();

				data.setInteger("Type", typeObj.intValue());
				data.setInteger("Dim", dimObj.intValue());

				data.setInteger("X", pos.getX());
				data.setInteger("Y", pos.getY());
				data.setInteger("Z", pos.getZ());

				list.appendTag(data);
			}
		}

		nbt.setTag("LastPos", list);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList list = nbt.getTagList("LastDim", NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound data = list.getCompoundTagAt(i);
			int type = data.getInteger("Type");
			int dim = data.getInteger("Dim");

			lastDim.put(Integer.valueOf(type), Integer.valueOf(dim));
		}

		list = nbt.getTagList("LastPos", NBT.TAG_COMPOUND);

		for (int i = 0; i < list.tagCount(); ++i)
		{
			NBTTagCompound data = list.getCompoundTagAt(i);
			int type = data.getInteger("Type");
			int dim = data.getInteger("Dim");
			int x = data.getInteger("X");
			int y = data.getInteger("Y");
			int z = data.getInteger("Z");

			lastPos.put(Integer.valueOf(type), Integer.valueOf(dim), new BlockPos(x, y, z));
		}
	}
}