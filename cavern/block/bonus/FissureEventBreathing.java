package cavern.block.bonus;

import java.util.Random;

import cavern.api.IFissureBreakEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FissureEventBreathing implements IFissureBreakEvent
{
	@Override
	public void onBreakBlock(World world, BlockPos pos, IBlockState state, float chance, int fortune, EntityPlayer player, Random random)
	{
		if (player != null)
		{
			player.setAir(300);
		}
	}
}