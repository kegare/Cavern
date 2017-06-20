package cavern.item;

import java.util.List;

import cavern.api.IMagicianStats;
import cavern.core.Cavern;
import cavern.stats.MagicianRank;
import cavern.stats.MagicianStats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemElixir extends Item
{
	public ItemElixir()
	{
		super();
		this.setUnlocalizedName("elixir");
		this.setMaxStackSize(16);
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
			subItems.add(type.getItemStack());
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return EnumAction.DRINK;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		player.setActiveHand(hand);

		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
	{
		EntityPlayer player = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;

		if (player == null || !player.capabilities.isCreativeMode)
		{
			stack.shrink(1);
		}

		if (player != null)
		{
			EnumType type = EnumType.byItemStack(stack);
			IMagicianStats stats = MagicianStats.get(player);
			int amount = type.getHealMPAmount();

			if (type.isHealPercentages())
			{
				MagicianRank rank = MagicianRank.get(stats.getRank());
				int max = rank.getMaxMP(player);

				stats.addMP(MathHelper.ceil(max * (amount / 100.0D)));
			}
			else
			{
				stats.addMP(amount);
			}

			player.addStat(StatList.getObjectUseStats(this));
		}

		if (player == null || !player.capabilities.isCreativeMode)
		{
			if (stack.isEmpty())
			{
				return new ItemStack(Items.GLASS_BOTTLE);
			}

			if (player != null)
			{
				player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
			}
		}

		return stack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
	{
		EnumType type = EnumType.byItemStack(stack);
		String healAmount = String.valueOf(type.getHealMPAmount());

		if (type.isHealPercentages())
		{
			healAmount += "%";
		}

		tooltip.add(Cavern.proxy.translate("item.elixir.tooltip.mp") + ": " + healAmount);
	}

	public enum EnumType
	{
		ELIXIR_NORMAL(0, "elixir", 50, false),
		ELIXIR_MEDIUM(1, "elixirMedium", 50, true),
		ELIXIR_HIGH(2, "elixirHigh", 100, true);

		private static final EnumType[] DAMAGE_LOOKUP = new EnumType[values().length];

		private final int itemDamage;
		private final String unlocalizedName;
		private final int healMPAmount;
		private final boolean healPercentages;

		private EnumType(int damage, String name, int amount, boolean percentages)
		{
			this.itemDamage = damage;
			this.unlocalizedName = name;
			this.healMPAmount = amount;
			this.healPercentages = percentages;
		}

		public int getItemDamage()
		{
			return itemDamage;
		}

		public String getUnlocalizedName()
		{
			return unlocalizedName;
		}

		public int getHealMPAmount()
		{
			return healMPAmount;
		}

		public boolean isHealPercentages()
		{
			return healPercentages;
		}

		public ItemStack getItemStack()
		{
			return getItemStack(1);
		}

		public ItemStack getItemStack(int amount)
		{
			return new ItemStack(CaveItems.ELIXIR, amount, getItemDamage());
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