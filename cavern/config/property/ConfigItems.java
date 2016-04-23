package cavern.config.property;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import cavern.util.ItemMeta;

public class ConfigItems
{
	private String[] values;

	private final List<ItemMeta> items = Lists.newArrayList();

	public String[] getValues()
	{
		if (values == null)
		{
			values = new String[0];
		}

		return values;
	}

	public void setValues(String[] items)
	{
		values = items;
	}

	public List<ItemMeta> getItems()
	{
		return items;
	}

	public void refreshItems()
	{
		items.clear();

		for (String value : values)
		{
			if (!Strings.isNullOrEmpty(value))
			{
				value = value.trim();

				if (!value.contains(":"))
				{
					value = "minecraft:" + value;
				}

				ItemMeta itemMeta;

				if (value.indexOf(':') != value.lastIndexOf(':'))
				{
					int i = value.lastIndexOf(':');

					itemMeta = new ItemMeta(value.substring(0, i), NumberUtils.toInt(value.substring(i + 1)));
				}
				else
				{
					itemMeta = new ItemMeta(value, 0);
				}

				if (itemMeta.getItem() != null)
				{
					items.add(itemMeta);
				}
			}
		}
	}
}