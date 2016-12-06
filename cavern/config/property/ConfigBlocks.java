package cavern.config.property;

import java.util.Arrays;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import cavern.util.BlockMeta;

public class ConfigBlocks
{
	private String[] values;

	private final Set<BlockMeta> blocks = Sets.newConcurrentHashSet();

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

	public Set<BlockMeta> getBlocks()
	{
		return blocks;
	}

	public void refreshBlocks()
	{
		blocks.clear();

		Arrays.stream(getValues()).filter(value -> !Strings.isNullOrEmpty(value)).forEach(value ->
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
		});
	}
}