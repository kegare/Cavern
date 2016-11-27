package cavern.world;

import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class CaveDataManager
{
	private long worldSeed = -1L;
	private int worldHeight;

	public CaveDataManager(@Nullable NBTTagCompound compound)
	{
		if (compound != null)
		{
			if (compound.hasKey("Seed", NBT.TAG_ANY_NUMERIC))
			{
				worldSeed = compound.getLong("Seed");
			}

			if (compound.hasKey("Height", NBT.TAG_ANY_NUMERIC))
			{
				worldHeight = compound.getInteger("Height");
			}
		}
	}

	public NBTTagCompound getCompound()
	{
		NBTTagCompound compound = new NBTTagCompound();

		compound.setLong("Seed", worldSeed);
		compound.setInteger("Height", worldHeight);

		return compound;
	}

	public long getWorldSeed()
	{
		return worldSeed;
	}

	public long getWorldSeed(long defaultSeed)
	{
		if (worldSeed == -1L)
		{
			setWorldSeed(defaultSeed);
		}

		return worldSeed;
	}

	public void setWorldSeed(long seed)
	{
		worldSeed = seed;
	}

	public int getWorldHeight()
	{
		return worldHeight;
	}

	public int getWorldHeight(int defaultHeight)
	{
		if (worldHeight <= 0)
		{
			setWorldHeight(defaultHeight);
		}

		return worldHeight;
	}

	public void setWorldHeight(int height)
	{
		worldHeight = height;
	}
}