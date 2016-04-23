package cavern.handler;

import cavern.block.BlockLeavesPerverted;
import cavern.block.BlockLogPerverted;
import cavern.block.BlockSaplingPerverted;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.IFuelHandler;

public class CaveFuelHandler implements IFuelHandler
{
	@Override
	public int getBurnTime(ItemStack fuel)
	{
		Block block = Block.getBlockFromItem(fuel.getItem());

		if (block instanceof BlockLogPerverted)
		{
			return 100;
		}
		else if (block instanceof BlockLeavesPerverted || block instanceof BlockSaplingPerverted)
		{
			return 35;
		}

		return 0;
	}
}