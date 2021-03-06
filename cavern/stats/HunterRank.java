package cavern.stats;

import javax.annotation.Nullable;

import cavern.item.CaveItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public enum HunterRank
{
	CHICKEN(0, 0, 1.0F, 0, "chicken", new ItemStack(Items.CHICKEN)),
	NOOB(1, 2, 1.0F, 0, "noob", new ItemStack(Items.PORKCHOP)),
	NOVICE_HUNTER(2, 10, 1.1F, 0, "noviceHunter", new ItemStack(Items.WOODEN_SWORD)),
	HUNTER(3, 50, 1.2F, 1, "hunter", new ItemStack(Items.IRON_SWORD)),
	CAVENIC_HUNTER(4, 300, 1.3F, 3, "cavenicHunter", new ItemStack(CaveItems.HEXCITE_SWORD)),
	CRAZY_HUNTER(5, 500, 1.5F, 5, "crazyHunter", new ItemStack(CaveItems.MAGNITE_SWORD)),
	RANGER(6, 2000, 2.0F, 10, "ranger", new ItemStack(Items.GOLDEN_SWORD)),
	CRAZY_RANGER(7, 5000, 3.0F, 15, "crazyRanger", new ItemStack(Items.DIAMOND_SWORD));

	public static final HunterRank[] VALUES = new HunterRank[values().length];

	private final int rank;
	private final int phase;
	private final float boost;
	private final int superCritical;
	private final String name;
	private final ItemStack theItemStack;

	private HunterRank(int rank, int phase, float boost, int superCiritical, String name, @Nullable ItemStack stack)
	{
		this.rank = rank;
		this.phase = phase;
		this.boost = boost;
		this.superCritical = superCiritical;
		this.name = name;
		this.theItemStack = stack;
	}

	public int getRank()
	{
		return rank;
	}

	public int getPhase()
	{
		return phase;
	}

	public float getBoost()
	{
		return boost;
	}

	public int getSuperCritical()
	{
		return superCritical;
	}

	public String getName()
	{
		return name;
	}

	public String getUnlocalizedName()
	{
		return "cavern.hunterrank." + name;
	}

	public ItemStack getItemStack()
	{
		return theItemStack == null ? ItemStack.EMPTY : theItemStack;
	}

	public static HunterRank get(int rank)
	{
		if (rank < 0)
		{
			rank = 0;
		}

		int max = VALUES.length - 1;

		if (rank > max)
		{
			rank = max;
		}

		return VALUES[rank];
	}

	static
	{
		for (HunterRank rank : values())
		{
			VALUES[rank.getRank()] = rank;
		}
	}
}