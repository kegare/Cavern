package cavern.handler.api;

import java.util.Set;

import cavern.api.ICavernAPI;
import cavern.api.IMineBonus;
import cavern.api.IMinerStats;
import cavern.block.BlockCave;
import cavern.block.RandomiteItem;
import cavern.stats.MinerStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class CavernAPIHandler implements ICavernAPI
{
	@Override
	public IMinerStats getMinerStats(EntityPlayer player)
	{
		return MinerStats.get(player);
	}

	@Override
	public Set<IMineBonus> getMineBonus()
	{
		return MinerStats.MINE_BONUS;
	}

	@Override
	public void addMineBonus(IMineBonus bonus)
	{
		MinerStats.MINE_BONUS.add(bonus);
	}

	@Override
	public void addRandomiteItem(ItemStack item, int weight)
	{
		BlockCave.RANDOMITE_ITEMS.add(new RandomiteItem(item, weight));
	}
}