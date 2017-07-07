package cavern.miningassist;

import javax.annotation.Nullable;

import cavern.api.IMinerStats;
import cavern.stats.MinerStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IStringSerializable;

public enum MiningAssist implements IStringSerializable
{
	DISABLED(0, "disabled"),
	QUICK(1, "quick"),
	RANGED(2, "ranged"),
	ADIT(3, "adit");

	public static final MiningAssist[] VALUES = new MiningAssist[values().length];

	private final int type;
	private final String name;

	private MiningAssist(int type, String name)
	{
		this.type = type;
		this.name = name;
	}

	public int getType()
	{
		return type;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public String getUnlocalizedName()
	{
		return "cavern.miningassist." + name;
	}

	public static MiningAssist byPlayer(EntityPlayer player)
	{
		return byMiner(MinerStats.get(player, true));
	}

	public static MiningAssist byMiner(@Nullable IMinerStats stats)
	{
		return get(stats == null ? 0 : stats.getMiningAssist());
	}

	public static MiningAssist get(int type)
	{
		if (type < 0 || type >= VALUES.length)
		{
			type = 0;
		}

		return VALUES[type];
	}

	static
	{
		for (MiningAssist assist : values())
		{
			VALUES[assist.getType()] = assist;
		}
	}
}