package cavern.magic;

import cavern.magic.IMagic.IPlainMagic;
import net.minecraft.entity.player.EntityPlayer;
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

		if (!world.isRemote)
		{
			world.newExplosion(player, player.posX, player.posY + 2.0D, player.posZ, 3.0F + 2.5F * getMagicLevel(), false, true);
		}

		return true;
	}
}