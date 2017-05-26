package cavern.magic;

import cavern.magic.IMagic.IPlayerMagic;
import cavern.util.CaveUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MagicReturn implements IPlayerMagic
{
	private final int magicLevel;
	private final long magicSpellTime;
	private final double magicRange;

	public MagicReturn(int level, long time, double range)
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
		return 100 * getMagicLevel();
	}

	@Override
	public int getMagicPoint()
	{
		return 10 * getMagicLevel();
	}

	@Override
	public boolean isTarget(EntityPlayer player, EntityPlayer targetPlayer)
	{
		return getMagicLevel() > 1 || player.getCachedUniqueIdString().equals(targetPlayer.getCachedUniqueIdString());
	}

	@Override
	public boolean execute(EntityPlayer player, EntityPlayer targetPlayer)
	{
		BlockPos spawnPos = player.getBedLocation();

		if (spawnPos == null)
		{
			return false;
		}

		World world = player.world;

		if (world.getBlockState(spawnPos.down()).isFullBlock() && world.isAirBlock(spawnPos.up(2)))
		{
			CaveUtils.setLocationAndAngles(targetPlayer, spawnPos);

			return true;
		}

		return false;
	}
}