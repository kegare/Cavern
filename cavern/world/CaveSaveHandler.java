package cavern.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.Random;

import org.apache.logging.log4j.Level;

import cavern.util.CaveLog;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;

public class CaveSaveHandler
{
	private static Random rand = new Random();

	protected String name;
	protected int dimension;

	protected NBTTagCompound data;
	protected long worldSeed;
	protected int worldHeight;

	public CaveSaveHandler(String name)
	{
		this.name = name;
	}

	public CaveSaveHandler setDimension(int dim)
	{
		dimension = dim;

		return this;
	}

	public NBTTagCompound getData()
	{
		if (data == null)
		{
			readFromFile();
			loadFromNBT();
		}

		return data;
	}

	protected NBTTagCompound getRawData()
	{
		return data;
	}

	public long getWorldSeed()
	{
		return worldSeed;
	}

	public CaveSaveHandler setWorldSeed(long seed)
	{
		worldSeed = seed;

		return this;
	}

	public int getWorldHeight()
	{
		return worldHeight;
	}

	public CaveSaveHandler setWorldHeight(int height)
	{
		worldHeight = height;

		return this;
	}

	public File getSaveDir()
	{
		File root = DimensionManager.getCurrentSaveRootDirectory();

		if (root == null || !root.exists() || root.isFile())
		{
			return null;
		}

		File dir = new File(root, "DIM" + dimension);

		if (!dir.exists())
		{
			dir.mkdirs();
		}

		return dir.isDirectory() ? dir : null;
	}

	public File getSaveFile()
	{
		File dir = getSaveDir();

		if (dir != null)
		{
			File file = new File(dir, name.replace(" ", "_").toLowerCase(Locale.ENGLISH) + ".dat");

			return file;
		}

		return null;
	}

	public void readFromFile()
	{
		File file = getSaveFile();

		if (file != null && file.exists() && file.isFile() && file.canRead())
		{
			try (FileInputStream input = new FileInputStream(file))
			{
				data = CompressedStreamTools.readCompressed(input);
			}
			catch (Exception e)
			{
				CaveLog.log(Level.ERROR, e, "An error occurred trying to reading " + name + " dimension data");
			}
		}

		if (data == null)
		{
			data = new NBTTagCompound();
		}
	}

	public void writeToFile()
	{
		File file = getSaveFile();

		if (file != null && data != null)
		{
			try (FileOutputStream output = new FileOutputStream(file))
			{
				CompressedStreamTools.writeCompressed(data, output);
			}
			catch (Exception e)
			{
				CaveLog.log(Level.ERROR, e, "An error occurred trying to reading " + name + " dimension data");
			}
		}

		data = null;
	}

	public void readFromBuffer(ByteBuf buffer)
	{
		worldSeed = buffer.readLong();
		worldHeight = buffer.readInt();
	}

	public void writeToBuffer(ByteBuf buffer)
	{
		buffer.writeLong(worldSeed);
		buffer.writeInt(worldHeight);
	}

	public void loadFromNBT()
	{
		loadFromNBT(data);
	}

	public void loadFromNBT(NBTTagCompound nbt)
	{
		if (nbt == null)
		{
			return;
		}

		if (!nbt.hasKey("Seed", NBT.TAG_ANY_NUMERIC))
		{
			nbt.setLong("Seed", rand.nextLong());
		}

		if (!nbt.hasKey("Height", NBT.TAG_ANY_NUMERIC) || nbt.getInteger("Height") <= 0)
		{
			if (worldHeight <= 0)
			{
				worldHeight = 128;
			}

			nbt.setInteger("Height", worldHeight);
		}

		worldSeed = nbt.getLong("Seed");
		worldHeight = nbt.getInteger("Height");
	}
}