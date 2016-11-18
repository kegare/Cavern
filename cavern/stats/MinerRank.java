package cavern.stats;

import cavern.item.CaveItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum MinerRank
{
	BEGINNER(0, 0, 1.0F, "beginner", Items.WOODEN_PICKAXE),
	STONE_MINER(1, 50, 1.0F, "stoneMiner", Items.STONE_PICKAXE),
	IRON_MINER(2, 100, 1.0F, "ironMiner", Items.IRON_PICKAXE),
	MAGNITE_MINER(3, 300, 1.1F, "magniteMiner", CaveItems.MAGNITE_PICKAXE),
	GOLD_MINER(4, 1000, 1.2F, "goldMiner", Items.GOLDEN_PICKAXE),
	AQUA_MINER(5, 1500, 1.25F, "aquaMiner", CaveItems.AQUAMARINE_PICKAXE),
	HEXCITE_MINER(6, 3000, 1.5F, "hexciteMiner", CaveItems.HEXCITE_PICKAXE),
	DIAMOND_MINER(7, 5000, 1.75F, "diamondMiner", Items.DIAMOND_PICKAXE);

	private static final MinerRank[] RANK_LOOKUP = new MinerRank[values().length];

	private final int rank;
	private final int phase;
	private final float boost;
	private final String name;
	private final Item pickaxe;

	@SideOnly(Side.CLIENT)
	private ItemStack renderItemStack;

	private MinerRank(int rank, int phase, float boost, String name, Item pickaxe)
	{
		this.rank = rank;
		this.phase = phase;
		this.boost = boost;
		this.name = name;
		this.pickaxe = pickaxe;
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

	public String getName()
	{
		return name;
	}

	public String getUnlocalizedName()
	{
		return "cavern.minerrank." + name;
	}

	public Item getPickaxe()
	{
		return pickaxe;
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getRenderItemStack()
	{
		if (renderItemStack == null)
		{
			renderItemStack = new ItemStack(pickaxe);
		}

		return renderItemStack;
	}

	public static MinerRank get(int rank)
	{
		if (rank < 0)
		{
			rank = 0;
		}

		int max = RANK_LOOKUP.length - 1;

		if (rank > max)
		{
			rank = max;
		}

		return RANK_LOOKUP[rank];
	}

	static
	{
		for (MinerRank rank : values())
		{
			RANK_LOOKUP[rank.getRank()] = rank;
		}
	}
}