package cavern.magic;

import cavern.core.CaveSounds;
import cavern.magic.IMagic.IPlayerMagic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class MagicReturn implements IPlayerMagic
{
	private final int magicLevel;
	private final long magicSpellTime;
	private final double magicRange;

	private int errorCode;

	public MagicReturn(int level, long time, double range)
	{
		this.magicLevel = level;
		this.magicSpellTime = time;
		this.magicRange = range;
	}

	@Override
	public int getMagicLevel()
	{
		return magicLevel;
	}

	@Override
	public long getMagicSpellTime()
	{
		return magicSpellTime;
	}

	@Override
	public double getMagicRange()
	{
		return magicRange;
	}

	@Override
	public int getCostMP()
	{
		return 100 * getMagicLevel();
	}

	@Override
	public int getMagicPoint()
	{
		return 5 * getMagicLevel();
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return CaveSounds.MAGIC_SUCCESS_MISC;
	}

	@Override
	public boolean isTarget(EntityPlayer player, EntityPlayer targetPlayer)
	{
		return getMagicLevel() > 1 || player.getCachedUniqueIdString().equals(targetPlayer.getCachedUniqueIdString());
	}

	@Override
	public ITextComponent getFailedMessage()
	{
		switch (errorCode)
		{
			case 1:
				return new TextComponentTranslation("item.magicalBook.return.no");
		}

		return IPlayerMagic.super.getFailedMessage();
	}

	@Override
	public boolean execute(EntityPlayer player, EntityPlayer targetPlayer)
	{
		BlockPos spawnPos = player.getBedLocation();

		if (spawnPos == null)
		{
			errorCode = 1;

			return false;
		}

		if (targetPlayer.attemptTeleport(spawnPos.getX() + 0.5D, spawnPos.getY() + 0.5D, spawnPos.getZ() + 0.5D))
		{
			return true;
		}

		errorCode = 0;

		return false;
	}
}