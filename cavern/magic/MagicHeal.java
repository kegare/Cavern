package cavern.magic;

import java.util.Set;

import com.google.common.collect.Sets;

import cavern.core.CaveSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicHeal implements IEntityMagic
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
		return 50 * getMagicLevel();
	}

	@Override
	public int getMagicPoint(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return getMagicLevel() + 1;
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return CaveSounds.MAGIC_HOLY;
	}

	@Override
	public boolean isTargetEntity(EntityPlayer player, Entity entity)
	{
		if (player.isEntityEqual(entity))
		{
			return true;
		}

		if (getMagicLevel() <= 1 || !player.canEntityBeSeen(entity))
		{
			return false;
		}

		if (entity instanceof EntityPlayer)
		{
			return true;
		}

		if (entity instanceof IEntityOwnable)
		{
			IEntityOwnable ownable = (IEntityOwnable)entity;

			if (player.isEntityEqual(ownable.getOwner()) || player.getCachedUniqueIdString().equals(ownable.getOwnerId().toString()))
			{
				return true;
			}
		}

		return false;
	}

	public boolean shouldHeal(EntityLivingBase entity)
	{
		return entity.getHealth() > 0.0F && entity.getHealth() < entity.getMaxHealth();
	}

	@Override
	public boolean execute(EntityPlayer player, Entity entity, World world, ItemStack stack, EnumHand hand)
	{
		EntityLivingBase target = (EntityLivingBase)entity;
		boolean healBadPotion = getMagicLevel() > 2;
		boolean healed = false;

		if (shouldHeal(target))
		{
			target.heal(player.getMaxHealth() * MathHelper.clamp(0.25F * getMagicLevel(), 0.5F, 1.0F));

			healed = true;
		}

		if (healBadPotion)
		{
			Set<Potion> potions = Sets.newHashSet();

			for (PotionEffect effect : target.getActivePotionEffects())
			{
				Potion potion = effect.getPotion();

				if (potion.isBadEffect())
				{
					potions.add(potion);
				}
			}

			for (Potion potion : potions)
			{
				target.removePotionEffect(potion);

				healed = true;
			}
		}

		return healed;
	}
}