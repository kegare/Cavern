package cavern.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IMineBonus
{
	public boolean canMineBonus(int combo, EntityPlayer player);

	public void onMineBonus(boolean isClient, int combo, EntityPlayer player);
}