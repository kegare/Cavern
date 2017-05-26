package cavern.magic;

import cavern.magic.IMagic.IEntityMagic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class MagicVenomBlast implements IEntityMagic
{
	private final int magicLevel;
	private final long magicSpellTime;
	private final double magicRange;

	public MagicVenomBlast(int level, long time, double range)
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
		return 30 * getMagicLevel();
	}

	@Override
	public int getMagicPoint()
	{
		return getMagicLevel();
	}

	@Override
	public boolean isTarget(EntityPlayer player, Entity entity)
	{
		return entity instanceof IMob;
	}

	@Override
	public boolean execute(EntityPlayer player, Entity entity)
	{
		if (entity instanceof EntityLivingBase)
		{
			EntityLivingBase target = (EntityLivingBase)entity;
			int level = getMagicLevel();
			int duration = (10 + 5 * (level - 1)) * 20;

			target.addPotionEffect(new PotionEffect(MobEffects.POISON, duration, 2, false, true));
			target.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, duration * 2, 3, false, true));

			if (target.isNonBoss())
			{
				target.attackEntityFrom(DamageSource.MAGIC, target.getHealth() * (0.2F * level));
			}

			return true;
		}

		return false;
	}
}