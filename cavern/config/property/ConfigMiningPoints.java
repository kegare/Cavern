package cavern.config.property;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Strings;

import cavern.stats.MinerStats;
import cavern.util.BlockMeta;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class ConfigMiningPoints
{
	private String[] values;

	private boolean init;

	public String[] getValues()
	{
		if (values == null)
		{
			values = new String[0];
		}

		return values;
	}

	public void setValues(String[] entries)
	{
		values = entries;
	}

	public boolean hasInit()
	{
		return init;
	}

	public void setInit(boolean flag)
	{
		init = flag;
	}

	public void refreshPoints()
	{
		MinerStats.pointAmounts.clear();

		for (String value : values)
		{
			if (!Strings.isNullOrEmpty(value) && value.contains(","))
			{
				value = value.trim();

				int i = value.indexOf(',');
				String str = value.substring(0, i);
				int point = NumberUtils.toInt(value.substring(i + 1));

				if (str.contains(":"))
				{
					i = str.lastIndexOf(':');
					BlockMeta blockMeta = new BlockMeta(str.substring(0, i), str.substring(i + 1));
					Block block = blockMeta.getBlock();

					if (block != null && block != Blocks.air)
					{
						MinerStats.setPointAmount(blockMeta, point);
					}
				}
				else
				{
					MinerStats.setPointAmount(str, point);
				}
			}
		}
	}
}