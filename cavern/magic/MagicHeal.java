package cavern.magic;

import java.util.Set;

import com.google.common.collect.Sets;

import cavern.magic.IMagic.IPlayerMagic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class MagicHeal implements IPlayerMagic
{
	private final int magicLevel;
	private final long magicSpellTime;
	private final double magicRange;

	public MagicHeal(int level, long time, double range)
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
		return getMagicLevel() > 2 || player.getCachedUniqueIdString().equals(targetPlayer.getCachedUniqueIdString());
	}

	@Override
	public boolean execute(EntityPlayer player, EntityPlayer targetPlayer)
	{
		boolean healBadPotion = getMagicLevel() > 1;
		boolean healed = false;

		if (targetPlayer.shouldHeal())
		{
			targetPlayer.heal(player.getMaxHealth() * 0.5F);

			healed = true;
		}

		if (healBadPotion)
		{
			Set<Potion> potions = Sets.newHashSet();

			for (PotionEffect effect : targetPlayer.getActivePotionEffects())
			{
				Potion potion = effect.getPotion();

				if (potion.isBadEffect())
				{
					potions.add(potion);
				}
			}

			for (Potion potion : potions)
			{
				targetPlayer.removePotionEffect(potion);

				healed = true;
			}
		}

		return healed;
	}
}