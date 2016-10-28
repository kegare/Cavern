package cavern.block.bonus;

import java.util.Random;

import cavern.api.IFissureBreakEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FissureEventExplosion implements IFissureBreakEvent
{
	@Override
	public void onBreakBlock(World world, BlockPos pos, IBlockState state, float chance, int fortune, EntityPlayer player, Random random)
	{
		world.newExplosion(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, random.nextDouble() < 0.15D ? 3.0F : 1.45F, false, true);
	}
}