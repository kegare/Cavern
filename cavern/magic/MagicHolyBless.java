package cavern.magic;

import java.util.Random;

import cavern.magic.IMagic.IPlayerMagic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class MagicHolyBless implements IPlayerMagic
{
	private static final Random RANDOM = new Random();

	private final int magicLevel;
	private final long magicSpellTime;
	private final double magicRange;

	public MagicHolyBless(int level, long time, double range)
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
		return 50 * getMagicLevel();
	}

	@Override
	public int getMagicPoint()
	{
		return getMagicLevel() + 1;
	}

	@Override
	public boolean isTarget(EntityPlayer player, EntityPlayer targetPlayer)
	{
		return getMagicLevel() > 1 || player.getCachedUniqueIdString().equals(targetPlayer.getCachedUniqueIdString());
	}

	@Override
	public boolean execute(EntityPlayer player, EntityPlayer targetPlayer)
	{
		int level = getMagicLevel();

		for (int i = 0; i < level; ++i)
		{
			Potion potion = null;
			int timeout = 0;

			while (potion == null || potion.isBadEffect() || targetPlayer.isPotionActive(potion))
			{
				potion = Potion.REGISTRY.getRandomObject(RANDOM);

				if (++timeout > 100)
				{
					return false;
				}
			}

			targetPlayer.addPotionEffect(new PotionEffect(potion, (60 + 30 * (level - 1)) * 20, level - 1, false, false));
		}

		targetPlayer.extinguish();

		return true;
	}
}