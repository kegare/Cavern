package cavern.handler.api;

import cavern.api.ICavernAPI;
import cavern.api.IMinerStats;
import cavern.stats.MinerStats;
import net.minecraft.entity.player.EntityPlayer;

public class CavernAPIHandler implements ICavernAPI
{
	@Override
	public IMinerStats getMinerStats(EntityPlayer player)
	{
		return MinerStats.get(player);
	}
}