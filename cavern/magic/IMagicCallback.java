package cavern.magic;

import cavern.api.IMagicianStats;
import net.minecraft.entity.player.EntityPlayer;

public interface IMagicCallback
{
	public default boolean isHigherMagic(EntityPlayer player, IMagicianStats stats, IMagic magic)
	{
		return !player.capabilities.isCreativeMode && magic.getMagicLevel() > stats.getRank() + 1;
	}

	public default int getMagicCostMP(EntityPlayer player, IMagicianStats stats, IMagic magic)
	{
		return magic.getCostMP(player);
	}

	public default boolean onSuccessMagic(EntityPlayer player, IMagicianStats stats, IMagic magic, boolean success)
	{
		return false;
	}

	public default boolean isSuccessMagic(EntityPlayer player, IMagicianStats stats, IMagic magic, boolean success)
	{
		return success;
	}
}