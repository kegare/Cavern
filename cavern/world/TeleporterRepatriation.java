package cavern.world;

import cavern.block.BlockPortalCavern;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.WorldServer;

public class TeleporterRepatriation extends TeleporterCavern
{
	public TeleporterRepatriation(WorldServer world)
	{
		super(world);
	}

	@Override
	protected boolean isPortalBlock(IBlockState state)
	{
		return state != null && state.getBlock() instanceof BlockPortalCavern;
	}
}