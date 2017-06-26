package cavern.item;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemPickaxeIce extends ItemPickaxeCave
{
	public ItemPickaxeIce()
	{
		super(CaveItems.ICE, "pickaxeIce");
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
	public boolean canHarvestBlock(IBlockState state)
	{
		Material material = state.getMaterial();

		if (material == Material.ICE || material == Material.PACKED_ICE)
		{
			return true;
		}

		return super.canHarvestBlock(state);
	}

	@Override
	public float getStrVsBlock(ItemStack stack, IBlockState state)
	{
		Material material = state.getMaterial();

		if (material == Material.ICE || material == Material.PACKED_ICE)
		{
			return efficiencyOnProperMaterial;
		}

		return super.getStrVsBlock(stack, state);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		return super.canApplyAtEnchantingTable(stack, enchantment) && IceEquipment.canApplyEnchantments(stack, enchantment);
	}
}