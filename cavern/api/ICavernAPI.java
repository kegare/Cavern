package cavern.api;

import net.minecraft.entity.player.EntityPlayer;

public interface ICavernAPI
{
	public IMinerStats getMinerStats(EntityPlayer player);
}