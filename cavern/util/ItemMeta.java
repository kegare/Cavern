package cavern.util;

import com.google.common.base.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class ItemMeta
{
	private Item item;
	private int meta;

	public ItemMeta(Item item, int meta)
	{
		this.item = item;
		this.meta = meta;
	}

	public ItemMeta(ItemStack itemstack)
	{
		this(itemstack.getItem(), itemstack.getItemDamage());
	}

	public ItemMeta(String name, int meta)
	{
		this(Item.itemRegistry.getObject(new ResourceLocation(name)), meta);
	}

	public Item getItem()
	{
		return item;
	}

	public int getMeta()
	{
		return meta;
	}

	public ItemStack getItemStack()
	{
		return new ItemStack(item, 1, meta);
	}

	public String getItemName()
	{
		return item.getRegistryName().toString();
	}

	public String getName()
	{
		if (item == null)
		{
			return "null";
		}

		String name = getItemName();

		if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE)
		{
			return name;
		}

		return name + ":" + meta;
	}

	@Override
	public String toString()
	{
		if (item == null)
		{
			return "null";
		}

		String name = getItemName();

		if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE)
		{
			return name + ",meta=all";
		}

		return name + ",meta=" + meta;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		else if (obj == null || !(obj instanceof ItemMeta))
		{
			return false;
		}

		ItemMeta itemMeta = (ItemMeta)obj;

		if (item != itemMeta.item)
		{
			return false;
		}
		else if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE || itemMeta.meta < 0 || itemMeta.meta == OreDictionary.WILDCARD_VALUE)
		{
			return true;
		}

		return meta == itemMeta.meta;
	}
	
	@Override
	public int hashCode()
	{
		if (meta < 0 || meta == OreDictionary.WILDCARD_VALUE)
		{
			return item.hashCode();
		}
		
		return Objects.hashCode(item, meta);
	}
}