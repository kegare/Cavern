package cavern.item;

import cavern.core.Cavern;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemPickaxeIce extends ItemPickaxeCave
{
	public ItemPickaxeIce()
	{
		super(CaveItems.ICE, "pickaxeIce");
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public int getMaxDamage(ItemStack itemstack)
	{
		int max = super.getMaxDamage(itemstack);

		return max + max / 8 * IceEquipment.get(itemstack).getCharge();
	}

	@Override
	public int getHarvestLevel(ItemStack itemstack, String toolClass, EntityPlayer player, IBlockState state)
	{
		int level = super.getHarvestLevel(itemstack, toolClass, player, state);

		if (IceEquipment.get(itemstack).getCharge() >= 150)
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
}