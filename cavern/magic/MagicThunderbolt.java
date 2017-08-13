package cavern.magic;

import cavern.api.ISummonMob;
import cavern.core.Cavern;
import cavern.stats.MagicianRank;
import cavern.stats.MagicianStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
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
		return 30 * getMagicLevel();
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

	public boolean spawnLightningBolt(World world, double x, double y, double z)
	{
		EntityLightningBolt lightningBolt = new EntityLightningBolt(world, x, y, z, false);

		return world.addWeatherEffect(lightningBolt);
	}

	@Override
	public boolean execute(EntityPlayer player, Entity entity, World world, ItemStack stack, EnumHand hand)
	{
		return spawnLightningBolt(world, entity.posX, entity.posY, entity.posZ);
	}

	@Override
	public boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		if (IEntityMagic.super.executeMagic(player, world, stack, hand))
		{
			return true;
		}

		Vec3d hitVec = ForgeHooks.rayTraceEyeHitVec(player, Cavern.proxy.getBlockReachDistance(player));

		if (hitVec != null)
		{
			return spawnLightningBolt(world, hitVec.x, hitVec.y - 0.5D, hitVec.z);
		}

		EnumFacing front = player.getHorizontalFacing();
		BlockPos pos = player.getPosition().up();
		int i = 0;

		do
		{
			pos = pos.offset(front);

			++i;
		}
		while (i < 7 && world.isAirBlock(pos));

		pos = pos.offset(front.getOpposite());

		while (world.isAirBlock(pos))
		{
			pos = pos.down();
		}

		return i > 3 && world.isAirBlock(pos.up()) && spawnLightningBolt(world, pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D);
	}
}