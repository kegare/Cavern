package cavern.api;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;

public interface ISummonMob
{
	public int getLifeTime();

	@Nullable
	public EntityPlayer getSummoner();

	public default boolean isSummonerEqual(EntityPlayer player)
	{
		if (player == null || getSummoner() == null)
		{
			return false;
		}

		return EntityPlayer.getUUID(player.getGameProfile()).equals(EntityPlayer.getUUID(getSummoner().getGameProfile()));
	}
}