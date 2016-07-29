package cavern.api;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

public interface ICavernAPI
{
	public IMinerStats getMinerStats(EntityPlayer player);

	public Set<IMineBonus> getMineBonus();

	public void addMineBonus(IMineBonus bonus);
}