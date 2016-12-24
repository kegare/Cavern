package cavern.miningassist;

import javax.annotation.Nullable;

import cavern.core.Cavern;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMiningAssistExecutor
{
	public MiningAssist getType();

	public void execute();

	public int calc();

	public default boolean harvestBlock(PlayerInteractionManager im, @Nullable BlockPos pos)
	{
		if (pos == null)
		{
			return false;
		}

		if (Cavern.proxy.isSinglePlayer())
		{
			World world = im.world;
			EntityPlayerMP player = im.player;
			IBlockState state = world.getBlockState(pos);

			if (im.tryHarvestBlock(pos))
			{
				if (!player.capabilities.isCreativeMode)
				{
					world.playEvent(2001, pos, Block.getStateId(state));
				}

				return true;
			}
		}
		else if (im.tryHarvestBlock(pos))
		{
			return true;
		}

		return false;
	}
}