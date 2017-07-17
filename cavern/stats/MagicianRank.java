package cavern.stats;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public enum MagicianRank
{
	BEGINNER(0, 0, 50, 1.75F, 20, "beginner", new ItemStack(Items.FEATHER)),
	NOVICE_MAGICIAN(1, 5, 100, 1.25F, 10, "noviceMagician", new ItemStack(Items.STICK)),
	MAGICIAN(2, 50, 200, 1.0F, 8, "magician", new ItemStack(Items.BOOK)),
	MAGE(3, 300, 300, 0.75F, 7, "mage", new ItemStack(Items.WRITABLE_BOOK)),
	GRAND_MAGE(4, 1000, 500, 0.55F, 6, "grandMage", new ItemStack(Items.WRITTEN_BOOK));

	public static final MagicianRank[] VALUES = new MagicianRank[values().length];

	private final int rank;
	private final int phase;
	private final int maxMP;
	private final float boost;
	private final int spellingSpeed;
	private final String name;
	private final ItemStack theItemStack;

	private MagicianRank(int rank, int phase, int max, float boost, int speed, String name, ItemStack stack)
	{
		this.rank = rank;
		this.phase = phase;
		this.maxMP = max;
		this.boost = boost;
		this.spellingSpeed = speed;
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

	public int getMaxMP()
	{
		return maxMP;
	}

	public int getMaxMP(@Nullable EntityPlayer player)
	{
		if (player == null)
		{
			return getMaxMP();
		}

		int minerRank = MinerStats.get(player).getRank();
		int hunterRank = HunterStats.get(player).getRank();
		double bonus = 1.0D;

		if (minerRank > 0)
		{
			bonus += 0.1D * minerRank;
		}

		if (hunterRank > 0)
		{
			bonus += 0.15D * hunterRank;
		}

		return MathHelper.floor(getMaxMP() * MathHelper.clamp(bonus, 1.0D, 3.0D));
	}

	public float getBoost()
	{
		return boost;
	}

	public int getSpellingSpeed()
	{
		return spellingSpeed;
	}

	public String getName()
	{
		return name;
	}

	public String getUnlocalizedName()
	{
		return "cavern.magicianrank." + name;
	}

	public ItemStack getItemStack()
	{
		return theItemStack == null ? ItemStack.EMPTY : theItemStack;
	}

	public static MagicianRank get(int rank)
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
		for (MagicianRank rank : values())
		{
			VALUES[rank.getRank()] = rank;
		}
	}
}