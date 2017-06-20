package cavern.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemAxeIce extends ItemAxeCave
{
	public ItemAxeIce()
	{
		super(CaveItems.ICE, 2.0F, -3.15F, "axeIce");
	}

	@Override
	public int getMaxDamage(ItemStack stack)
	{
		int max = super.getMaxDamage(stack);

		return max + max / 8 * IceEquipment.get(stack).getCharge();
	}

	@Override
	public int getHarvestLevel(ItemStack stack, String toolClass, EntityPlayer player, IBlockState state)
	{
		int level = super.getHarvestLevel(stack, toolClass, player, state);

		if (IceEquipment.get(stack).getCharge() >= 150)
		{
			++level;
		}

		return level;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		return super.canApplyAtEnchantingTable(stack, enchantment) && IceEquipment.canApplyEnchantments(stack, enchantment);
	}
}