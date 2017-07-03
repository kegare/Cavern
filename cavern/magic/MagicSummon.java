package cavern.magic;

import javax.annotation.Nullable;

import cavern.core.CaveSounds;
import cavern.entity.EntitySummonCavenicSkeleton;
import cavern.entity.EntitySummonCavenicZombie;
import cavern.entity.EntitySummonSkeleton;
import cavern.entity.EntitySummonZombie;
import cavern.magic.IMagic.IPlainMagic;
import cavern.util.CaveUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MagicSummon implements IPlainMagic
{
	private final int magicLevel;
	private final long magicSpellTime;

	public MagicSummon(int level, long time)
	{
		this.magicLevel = level;
		this.magicSpellTime = time;
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
		return 0.0D;
	}

	@Override
	public int getCostMP()
	{
		return 50 * Math.max(getMagicLevel(), 1);
	}

	@Override
	public int getMagicPoint()
	{
		return 2 * getMagicLevel();
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return CaveSounds.MAGIC_SUCCESS_MISC;
	}

	@Override
	public boolean execute(EntityPlayer player)
	{
		EnumFacing frontFace = player.getHorizontalFacing();
		BlockPos origin = player.getPosition();
		BlockPos summonPos;

		for (int i = 0; i < 3; ++i)
		{
			summonPos = getSummonPos(player, origin.offset(frontFace, i));

			if (summonPos != null)
			{
				summon(player, summonPos);

				return true;
			}
		}

		for (BlockPos pos : BlockPos.getAllInBoxMutable(origin.add(2, 0, 2), origin.add(-2, 0, -2)))
		{
			summonPos = getSummonPos(player, pos);

			if (summonPos != null)
			{
				summon(player, summonPos);

				return true;
			}
		}

		return false;
	}

	@Nullable
	protected BlockPos getSummonPos(EntityPlayer player, BlockPos checkPos)
	{
		World world = player.world;
		BlockPos pos = checkPos;
		int diff = 0;

		if (world.isAirBlock(pos))
		{
			while (diff < 5 && world.isAirBlock(pos))
			{
				pos = pos.down();

				++diff;
			}

			pos = pos.up();
		}
		else while (diff < 5 && !world.isAirBlock(pos))
		{
			pos = pos.up();

			++diff;
		}

		if (!world.isAirBlock(pos) || !world.isAirBlock(pos.up()) || world.isAirBlock(pos.down()))
		{
			return null;
		}

		if (!world.checkNoEntityCollision(new AxisAlignedBB(pos)))
		{
			return null;
		}

		if (world.rayTraceBlocks(new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ), new Vec3d(pos), false, true, false) != null)
		{
			return null;
		}

		BlockPos blockpos = player.getPosition();
		double prev = pos.distanceSq(blockpos);

		if (prev - pos.distanceSq(blockpos.offset(player.getHorizontalFacing())) < 0.0D)
		{
			return null;
		}

		return pos;
	}

	public void summon(EntityPlayer player, BlockPos pos)
	{
		World world = player.world;
		EntityLivingBase entity;

		switch (getMagicLevel())
		{
			case 1:
				entity = new EntitySummonZombie(world, player);
				break;
			case 2:
				entity = new EntitySummonSkeleton(world, player);
				break;
			case 3:
				entity = new EntitySummonCavenicZombie(world, player);
				break;
			case 4:
				entity = new EntitySummonCavenicSkeleton(world, player);
				break;
			default:
				entity = null;
		}

		if (entity == null)
		{
			return;
		}

		entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);

		if (entity instanceof EntityLiving)
		{
			((EntityLiving)entity).onInitialSpawn(world.getDifficultyForLocation(pos), null);
		}

		if (world.spawnEntity(entity))
		{
			CaveUtils.grantAdvancement(player, "magic_summon");
		}
	}
}