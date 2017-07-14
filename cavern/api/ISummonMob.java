package cavern.api;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;

public interface ISummonMob
{
	public static final Predicate<? super Entity> CAN_SUMMON_MOB_TARGET = entity -> entity instanceof IMob && !(entity instanceof ISummonMob);

	public int getLifeTime();

	@Nullable
	public EntityPlayer getSummoner();

	public default boolean isSummonerEqual(@Nullable EntityPlayer player)
	{
		if (player == null || getSummoner() == null)
		{
			return false;
		}

		return EntityPlayer.getUUID(player.getGameProfile()).equals(EntityPlayer.getUUID(getSummoner().getGameProfile()));
	}
}