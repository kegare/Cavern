package cavern.magic;

import cavern.api.ISummonMob;
import cavern.stats.MagicianRank;
import cavern.stats.MagicianStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicThunderbolt implements IEntityMagic
{
	private final int magicLevel;
	private final long magicSpellTime;
	private final double magicRange;

	public MagicThunderbolt(int level, long time, double range)
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
		return 25 * getMagicLevel();
	}

	@Override
	public int getMagicPoint(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return getMagicLevel();
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
		EntityLightningBolt lightningBolt = new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, false);

		world.addWeatherEffect(lightningBolt);

		return true;
	}
}