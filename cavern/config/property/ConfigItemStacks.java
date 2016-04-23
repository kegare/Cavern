package cavern.config.property;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ConfigItemStacks
{
	private String[] values;

	private final List<ItemStack> items = Lists.newArrayList();

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

	public List<ItemStack> getItems()
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

				if (!value.contains(","))
				{
					value += ",1";
				}

				int i = value.indexOf(',');
				String str = value.substring(0, i);
				int size = NumberUtils.toInt(value.substring(i + 1));

				if (!str.contains(":"))
				{
					str = "minecraft:" + value;
				}

				Item item;
				int meta;

				if (str.indexOf(':') != str.lastIndexOf(':'))
				{
					i = str.lastIndexOf(':');
					item = Item.itemRegistry.getObject(new ResourceLocation(str.substring(0, i)));
					meta = NumberUtils.toInt(str.substring(i + 1));
				}
				else
				{
					item = Item.itemRegistry.getObject(new ResourceLocation(str));
					meta = 0;
				}

				if (item != null)
				{
					items.add(new ItemStack(item, size, meta));
				}
			}
		}
	}
}