package cavern.magic;

import cavern.magic.IMagic.IPlainMagic;
import cavern.util.CaveUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagicExplosion implements IPlainMagic
{
	private final int magicLevel;
	private final long magicSpellTime;
	private final double magicRange;

	public MagicExplosion(int level, long time, double range)
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
	public boolean execute(EntityPlayer player)
	{
		World world = player.world;
		EnumFacing front = player.getHorizontalFacing();
		BlockPos pos = player.getPosition().up();
		int i = 0;

		do
		{
			pos = pos.offset(front);

			++i;
		}
		while (i < 2 + getMagicLevel() * 2 && world.isAirBlock(pos));

		boolean grief = world.getGameRules().getBoolean("mobGriefing");

		MagicalExplosion.createExplosion(world, player, pos.getX() + 0.5D, pos.getY() + 0.25D, pos.getZ() + 0.5D, 3.0F + 2.5F * getMagicLevel(), grief);

		CaveUtils.grantAdvancement(player, "magic_explosion");

		return true;
	}
}