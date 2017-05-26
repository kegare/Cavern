package cavern.magic;

import cavern.magic.IMagic.IEntityMagic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

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
		return 25 * getMagicLevel();
	}

	@Override
	public int getMagicPoint()
	{
		return getMagicLevel();
	}

	@Override
	public boolean isTarget(EntityPlayer player, Entity entity)
	{
		return entity instanceof IMob;
	}

	@Override
	public boolean execute(EntityPlayer player, Entity entity)
	{
		World world = entity.world;

		if (!world.isRemote)
		{
			EntityLightningBolt lightningBolt = new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ, false);

			world.addWeatherEffect(lightningBolt);
		}

		return true;
	}
}