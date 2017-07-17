package cavern.magic;

import java.util.Random;

import cavern.core.CaveSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicHolyBless implements IEntityMagic
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

		if (getMagicLevel() <= 1)
		{
			return false;
		}

		return player.canEntityBeSeen(entity);
	}

	@Override
	public boolean execute(EntityPlayer player, Entity entity, World world, ItemStack stack, EnumHand hand)
	{
		EntityLivingBase target = (EntityLivingBase)entity;
		int level = getMagicLevel();

		for (int i = 0; i < level; ++i)
		{
			Potion potion = null;
			int timeout = 0;

			while (potion == null || potion.isBadEffect() || target.isPotionActive(potion))
			{
				potion = Potion.REGISTRY.getRandomObject(RANDOM);

				if (potion == MobEffects.GLOWING)
				{
					potion = null;
				}

				if (++timeout > 100)
				{
					return false;
				}
			}

			if (potion != null)
			{
				if (potion.isInstant())
				{
					potion.affectEntity(player, player, target, level - 1, 1.0D);
				}
				else
				{
					target.addPotionEffect(new PotionEffect(potion, (60 + 30 * (level - 1)) * 20, level - 1, false, false));
				}
			}
		}

		target.extinguish();

		return true;
	}
}