package cavern.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class BlockMeta implements Comparable<BlockMeta>
{
	private Block block;
	private int meta;

	public BlockMeta(Block block, int meta)
	{
		this.block = block;
		this.meta = meta;
	}

	public BlockMeta(IBlockState state)
	{
		this(state.getBlock(), state.getBlock().getMetaFromState(state));
	}

	public BlockMeta(String name, int meta)
	{
		this(Block.blockRegistry.getObject(new ResourceLocation(name)), meta);
	}

	public BlockMeta(String name, String meta)
	{
		this(name, -1);
		this.meta = getMetaFromString(block, meta);
	}

	public Block getBlock()
	{
		return block;
	}

	public int getMeta()
	{
		return meta;
	}

	public IBlockState getBlockState()
	{
		return block.getStateFromMeta(meta);
	}

	public String getBlockName()
	{
		return block.getRegistryName().toString();
	}

	public String getMetaName()
	{
		return getMetaName(block, meta);
	}

	public String getMetaString()
	{
		return getMetaString(block, meta);
	}

	public String getName()
	{
		if (block == null)
		{
			return "null";
		}

		String name = getBlockName();

		if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE)
		{
			return name;
		}

		return name + ":" + meta;
	}

	@Override
	public String toString()
	{
		if (block == null)
		{
			return "null";
		}

		String name = getBlockName();

		if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE)
		{
			return name + ",meta=all";
		}

		return name + ",meta=" + meta;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		else if (obj == null || !(obj instanceof BlockMeta))
		{
			return false;
		}

		BlockMeta blockMeta = (BlockMeta)obj;

		if (block != blockMeta.block)
		{
			return false;
		}
		else if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE || blockMeta.meta < 0 || blockMeta.meta == OreDictionary.WILDCARD_VALUE)
		{
			return true;
		}

		return meta == blockMeta.meta;
	}

	@Override
	public int compareTo(BlockMeta o)
	{
		int i = CaveUtils.compareWithNull(this, o);

		if (i == 0 && o != null)
		{
			i = CaveUtils.blockComparator.compare(getBlock(), o.getBlock());

			if (i == 0 && getBlock() != null && o.getBlock() != null)
			{
				i = Integer.compare(getMeta(), o.getMeta());
			}
		}

		return i;
	}

	public static final Pattern numberPattern = Pattern.compile("^[0-9]+$");

	private static final LoadingCache<Pair<Block, String>, Integer> stringMetaCache = CacheBuilder.newBuilder().build(new CacheLoader<Pair<Block, String>, Integer>()
	{
		@Override
		public Integer load(Pair<Block, String> key) throws Exception
		{
			Block block = key.getLeft();
			String str = key.getRight();

			if (block == null || Strings.isNullOrEmpty(str) || str.equalsIgnoreCase("all") || str.equalsIgnoreCase("null"))
			{
				return -1;
			}

			str = str.trim();

			if (numberPattern.matcher(str).matches())
			{
				try
				{
					return Integer.parseInt(str, 10);
				}
				catch (Exception e) {}
			}

			Class<?> clazz = null;

			for (Field field : block.getClass().getDeclaredFields())
			{
				if ((field.getModifiers() & 0x1) != 0 && (field.getModifiers() & 0x8) != 0)
				{
					if (field.getType() == PropertyEnum.class)
					{
						try
						{
							clazz = ((PropertyEnum<?>)field.get(null)).getValueClass();
						}
						catch (Exception e) {}
					}
				}
			}

			if (clazz == null)
			{
				return -1;
			}

			for (Object obj : clazz.getEnumConstants())
			{
				if (obj instanceof IStringSerializable)
				{
					String name = ((IStringSerializable)obj).getName();

					if (str.equalsIgnoreCase(name))
					{
						for (Method method : obj.getClass().getDeclaredMethods())
						{
							if (method.getReturnType() == Integer.TYPE && method.getParameterTypes().length == 0)
							{
								try
								{
									return ((Integer)method.invoke(obj, new Object[0])).intValue();
								}
								catch (Exception e) {}
							}
						}
					}
				}
			}

			return -1;
		};
	});

	private static final LoadingCache<Pair<Block, Integer>, String> metaStringCache = CacheBuilder.newBuilder().build(new CacheLoader<Pair<Block, Integer>, String>()
	{
		@Override
		public String load(Pair<Block, Integer> key) throws Exception
		{
			Block block = key.getLeft();
			int meta = key.getRight().intValue();

			if (block == null)
			{
				return null;
			}

			if (meta < 0)
			{
				return "all";
			}

			Class<?> clazz = null;

			for (Field field : block.getClass().getDeclaredFields())
			{
				if ((field.getModifiers() & 0x1) != 0 && (field.getModifiers() & 0x8) != 0)
				{
					if (field.getType() == PropertyEnum.class)
					{
						try
						{
							clazz = ((PropertyEnum<?>)field.get(null)).getValueClass();
						}
						catch (Exception e) {}
					}
				}
			}

			if (clazz == null)
			{
				return "null";
			}

			for (Object obj : clazz.getEnumConstants())
			{
				if (obj instanceof IStringSerializable)
				{
					String name = ((IStringSerializable)obj).getName();

					for (Method method : obj.getClass().getDeclaredMethods())
					{
						if (method.getReturnType() == Integer.TYPE && method.getParameterTypes().length == 0)
						{
							try
							{
								if (((Integer)method.invoke(obj, new Object[0])).intValue() == meta)
								{
									return name;
								}
							}
							catch (Exception e) {}
						}
					}
				}
			}

			return "null";
		}
	});

	public static int getMetaFromString(Block block, String str)
	{
		return stringMetaCache.getUnchecked(Pair.of(block, str));
	}

	public static String getMetaName(Block block, int meta)
	{
		if (block.getRegistryName().getResourceDomain().equals("minecraft"))
		{
			return metaStringCache.getUnchecked(Pair.of(block, meta));
		}

		return Integer.toString(meta);
	}

	public static String getMetaString(Block block, int meta)
	{
		String name = getMetaName(block, meta);

		if (Strings.isNullOrEmpty(name) || name.equals("null"))
		{
			return Integer.toString(0);
		}

		return name;
	}
}