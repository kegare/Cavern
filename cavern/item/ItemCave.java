package cavern.item;

import cavern.core.Cavern;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCave extends Item
{
	public ItemCave()
	{
		super();
		this.setUnlocalizedName("itemCave");
		this.setHasSubtypes(true);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "item." + EnumType.byItemStack(stack).getUnlocalizedName();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		for (EnumType type : EnumType.values())
		{
			subItems.add(new ItemStack(item, 1, type.getItemDamage()));
		}
	}

	public enum EnumType
	{
		AQUAMARINE(0, "aquamarine"),
		MAGNITE_INGOT(1, "ingotMagnite"),
		HEXCITE(2, "hexcite"),
		ICE_STICK(3, "stickIce"),
		MINER_ORB(4, "orbMiner");

		private static final EnumType[] DAMAGE_LOOKUP = new EnumType[values().length];

		private final int itemDamage;
		private final String unlocalizedName;

		private EnumType(int damage, String name)
		{
			this.itemDamage = damage;
			this.unlocalizedName = name;
		}

		public int getItemDamage()
		{
			return itemDamage;
		}

		public String getUnlocalizedName()
		{
			return unlocalizedName;
		}

		public ItemStack getItemStack()
		{
			return getItemStack(1);
		}

		public ItemStack getItemStack(int amount)
		{
			return new ItemStack(CaveItems.CAVE_ITEM, amount, getItemDamage());
		}

		public static EnumType byDamage(int damage)
		{
			if (damage < 0 || damage >= DAMAGE_LOOKUP.length)
			{
				damage = 0;
			}

			return DAMAGE_LOOKUP[damage];
		}

		public static EnumType byItemStack(ItemStack itemstack)
		{
			return byDamage(itemstack == null ? 0 : itemstack.getItemDamage());
		}

		static
		{
			for (EnumType type : values())
			{
				DAMAGE_LOOKUP[type.getItemDamage()] = type;
			}
		}
	}
}