package cavern.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IAquaTool
{
	public default float getAquaBreakSpeed(ItemStack itemstack, EntityPlayer player, BlockPos pos, IBlockState state, float originalSpeed)
	{
		return originalSpeed * 10.0F;
	}
}