package cavern.magic;

import cavern.api.ISummonMob;
import cavern.stats.MagicianRank;
import cavern.stats.MagicianStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	@SideOnly(Side.CLIENT)
	@Override
	public long getMagicSpellTime(ItemStack stack, EnumHand hand)
	{
		return magicSpellTime;
	}

	@Override
	public double getMagicRange(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return magicRange;
	}

	@Override
	public int getMagicCost(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return 30 * getMagicLevel();
	}

	@Override
	public boolean isTargetEntity(EntityPlayer player, Entity entity)
	{
		if (player.isEntityEqual(entity))
		{
			return false;
		}

		if (!(entity instanceof IMob) || entity instanceof ISummonMob || entity instanceof IEntityOwnable && ((IEntityOwnable)entity).getOwner() != null)
		{
			return false;
		}

		if (getMagicLevel() < 3 || MagicianStats.get(player).getRank() < MagicianRank.MAGE.getRank())
		{
			if (!player.canEntityBeSeen(entity))
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean execute(EntityPlayer player, Entity entity, World world, ItemStack stack, EnumHand hand)
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
}