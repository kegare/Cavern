package cavern.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IStats
{
	public IPortalCache getPortalCache(Entity entity);

	public IPlayerData getPlayerData(EntityPlayer player);

	public IMinerStats getMinerStats(EntityPlayer player);

	public IHunterStats getHunterStats(EntityPlayer player);

	public IMagicianStats getMagicianStats(EntityPlayer player);
}