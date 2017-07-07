package cavern.api;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;

public interface IMineBonus
{
	static final Random RANDOM = new Random();

	public boolean canMineBonus(int combo, EntityPlayer player);

	public void onMineBonus(boolean isClient, int combo, EntityPlayer player);
}