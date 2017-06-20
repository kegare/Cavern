package cavern.miningassist;

public enum MiningAssist
{
	DISABLED(0, "disabled"),
	QUICK(1, "quick"),
	RANGED(2, "ranged"),
	ADIT(3, "adit");

	private static final MiningAssist[] TYPE_LOOKUP = new MiningAssist[values().length];

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

	public String getName()
	{
		return name;
	}

	public String getUnlocalizedName()
	{
		return "cavern.miningassist." + name;
	}

	public static MiningAssist byType(int type)
	{
		if (type < 0 || type >= TYPE_LOOKUP.length)
		{
			type = 0;
		}

		return TYPE_LOOKUP[type];
	}

	static
	{
		for (MiningAssist assist : values())
		{
			TYPE_LOOKUP[assist.getType()] = assist;
		}
	}
}