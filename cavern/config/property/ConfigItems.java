package cavern.config.property;

import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import cavern.util.ItemMeta;
import net.minecraft.item.ItemStack;

public class ConfigItems
{
	private String[] values;

	private final Set<ItemMeta> items = Sets.newConcurrentHashSet();

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

	public Set<ItemMeta> getItems()
	{
		return items;
	}

	public boolean isEmpty()
	{
		return items.isEmpty();
	}

	public boolean hasItemStack(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return false;
		}

		for (ItemMeta itemMeta : items)
		{
			if (itemMeta.getItem() == stack.getItem())
			{
				if (stack.isItemStackDamageable())
				{
					return true;
				}

				if (stack.getHasSubtypes())
				{
					if (itemMeta.getMeta() == stack.getMetadata())
					{
						return true;
					}
				}
				else return true;
			}
		}

		return false;
	}

	public void refreshItems()
	{
		items.clear();

		Arrays.stream(getValues()).filter(value -> !Strings.isNullOrEmpty(value)).forEach(value ->
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

			if (!itemMeta.isEmpty())
			{
				items.add(itemMeta);
			}
		});
	}
}