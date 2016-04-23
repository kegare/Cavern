package cavern.stats;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import cavern.capability.CaveCapabilities;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

public class PortalCache implements IPortalCache
{
	private final Map<Integer, Integer> lastDim = Maps.newHashMap();
	private final Map<Pair<Integer, Integer>, BlockPos> lastPos = Maps.newHashMap();

	public static IPortalCache get(Entity entity)
	{
		IPortalCache cache = CaveCapabilities.getEntityCapability(entity, CaveCapabilities.PORTAL_CACHE);

		if (cache == null)
		{
			return new PortalCache();
		}

		return cache;
	}

	@Override
	public int getLastDim(int type)
	{
		Integer ret = lastDim.get(type);

		if (ret == null)
		{
			return 0;
		}

		return ret.intValue();
	}

	@Override
	public void setLastDim(int type, int dim)
	{
		lastDim.put(type, dim);
	}

	@Override
	public BlockPos getLastPos(int type, int dim)
	{
		return lastPos.get(Pair.of(type, dim));
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
		return lastPos.containsKey(Pair.of(type, dim));
	}

	@Override
	public void setLastPos(int type, int dim, BlockPos pos)
	{
		if (pos == null)
		{
			lastPos.remove(Pair.of(type, dim));
		}
		else
		{
			lastPos.put(Pair.of(type, dim), pos);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList list = new NBTTagList();

		for (Entry<Integer, Integer> entry : lastDim.entrySet())
		{
			NBTTagCompound data = new NBTTagCompound();

			data.setInteger("Type", entry.getKey());
			data.setInteger("Dim", entry.getValue());

			list.appendTag(data);
		}

		nbt.setTag("LastDim", list);

		list = new NBTTagList();

		for (Entry<Pair<Integer, Integer>, BlockPos> entry : lastPos.entrySet())
		{
			NBTTagCompound data = new NBTTagCompound();
			Pair<Integer, Integer> key = entry.getKey();

			data.setInteger("Type", key.getRight());
			data.setInteger("Dim", key.getLeft());

			BlockPos pos = entry.getValue();

			data.setInteger("X", pos.getX());
			data.setInteger("Y", pos.getY());
			data.setInteger("Z", pos.getZ());

			list.appendTag(data);
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

			lastDim.put(type, dim);
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

			lastPos.put(Pair.of(type, dim), new BlockPos(x, y, z));
		}
	}
}