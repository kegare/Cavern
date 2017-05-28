package cavern.magic;

import java.util.Random;

import cavern.magic.IMagic.IPlainMagic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagicFlameBreath implements IPlainMagic
{
	private static final Random RANDOM = new Random();

	private final int magicLevel;
	private final long magicSpellTime;
	private final double magicRange;

	public MagicFlameBreath(int level, long time, double range)
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
		return 20 * getMagicLevel();
	}

	@Override
	public int getMagicPoint()
	{
		return getMagicLevel();
	}

	@Override
	public boolean execute(EntityPlayer player)
	{
		int level = getMagicLevel();
		double range = getMagicRange();
		World world = player.world;
		BlockPos blockPos = player.getPosition();
		int count = 0;

		for (BlockPos pos : BlockPos.getAllInBox(blockPos.add(range, range, range), blockPos.add(-range, -range, -range)))
		{
			double dist = Math.sqrt(blockPos.distanceSq(pos));

			if (dist > range)
			{
				continue;
			}

			BlockPos down = pos.down();

			if (world.isAirBlock(pos))
			{
				if (world.isAirBlock(down) || !world.getBlockState(down).isFullBlock())
				{
					continue;
				}

				BlockPos up = pos.up();

				if (world.isAirBlock(up) && RANDOM.nextInt(Math.max(5 - level, 2)) == 0)
				{
					world.setBlockState(pos, Blocks.FIRE.getDefaultState());

					++count;
				}
			}
		}

		if (count > 0 && !player.isPotionActive(MobEffects.FIRE_RESISTANCE))
		{
			player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 10 * level * 20, 0, false, false));
		}

		return count > 0;
	}
}