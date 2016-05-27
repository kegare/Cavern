package cavern.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ItemAxeAquamarine extends ItemAxeCave implements IAquaTool
{
	public ItemAxeAquamarine()
	{
		super(CaveItems.AQUAMARINE, 8.0F, -3.0F, "axeAquamarine");
	}

	@Override
	public float getAquaBreakSpeed(ItemStack itemstack, EntityPlayer player, BlockPos pos, IBlockState state, float originalSpeed)
	{
		return originalSpeed * 10.0F;
	}
}