package cavern.config.property;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import cavern.util.BlockMeta;

public class ConfigBlocks
{
	private String[] values;

	private final List<BlockMeta> blocks = Lists.newArrayList();

	public String[] getValues()
	{
		if (values == null)
		{
			values = new String[0];
		}

		return values;
	}

	public void setValues(String[] blocks)
	{
		values = blocks;
	}

	public List<BlockMeta> getBlocks()
	{
		return blocks;
	}

	public void refreshBlocks()
	{
		blocks.clear();

		for (String value : values)
		{
			if (!Strings.isNullOrEmpty(value))
			{
				value = value.trim();

				if (!value.contains(":"))
				{
					value = "minecraft:" + value;
				}

				BlockMeta blockMeta;

				if (value.indexOf(':') != value.lastIndexOf(':'))
				{
					int i = value.lastIndexOf(':');

					blockMeta = new BlockMeta(value.substring(0, i), value.substring(i + 1));
				}
				else
				{
					blockMeta = new BlockMeta(value, 0);
				}

				if (blockMeta.getBlock() != null)
				{
					blocks.add(blockMeta);
				}
			}
		}
	}
}