package cavern.config.property;

import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import cavern.config.GeneralConfig;
import cavern.stats.MinerStats;
import cavern.util.BlockMeta;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

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

	public boolean shouldInit()
	{
		return init;
	}

	public void setInit(boolean flag)
	{
		init = flag;
	}

	public void init()
	{
		Set<String> entries = Sets.newTreeSet();

		for (Block block : Block.REGISTRY)
		{
			if (block == null)
			{
				continue;
			}

			for (int i = 0; i < 16; ++i)
			{
				int point = MinerStats.getPointAmount(block, i);

				if (point > 0)
				{
					String name = block.getRegistryName().toString();
					String meta = BlockMeta.getMetaString(block, i);

					entries.add(name + ":" + meta + "," + point);
				}
			}
		}

		ConfigCategory category = GeneralConfig.config.getCategory(Configuration.CATEGORY_GENERAL);
		Property prop = category.get("miningPoints");

		if (prop != null)
		{
			String[] data = entries.toArray(new String[entries.size()]);

			prop.set(data);

			setValues(data);
		}
	}

	public void refreshPoints()
	{
		MinerStats.MINING_POINTS.clear();

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

					if (blockMeta.isNotAir())
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