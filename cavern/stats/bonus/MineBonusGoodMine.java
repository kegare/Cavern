package cavern.stats.bonus;

import cavern.api.IMineBonus;
import cavern.core.CaveAchievements;
import cavern.core.Cavern;
import net.minecraft.entity.player.EntityPlayer;

public class MineBonusGoodMine implements IMineBonus
{
	@Override
	public boolean canMineBonus(int combo, EntityPlayer player)
	{
		return combo >= 50 && !Cavern.proxy.hasAchievementUnlocked(player, CaveAchievements.GOOD_MINE);
	}

	@Override
	public void onMineBonus(boolean isClient, int combo, EntityPlayer player)
	{
		if (!isClient)
		{
			player.addStat(CaveAchievements.GOOD_MINE);
		}
	}
}