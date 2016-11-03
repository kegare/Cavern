package cavern.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemHoeIce extends ItemHoeCave
{
	public ItemHoeIce()
	{
		super(CaveItems.ICE, "hoeIce");
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
}