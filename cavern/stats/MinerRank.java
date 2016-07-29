package cavern.stats;

import cavern.item.CaveItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum MinerRank
{
	BEGINNER(0, 0, "beginner", Items.WOODEN_PICKAXE),
	STONE_MINER(1, 50, "stoneMiner", Items.STONE_PICKAXE),
	IRON_MINER(2, 100, "ironMiner", Items.IRON_PICKAXE),
	GOLD_MINER(3, 1000, "goldMiner", Items.GOLDEN_PICKAXE),
	AQUA_MINER(4, 3000, "aquaMiner", CaveItems.AQUAMARINE_PICKAXE),
	DIAMOND_MINER(5, 10000, "diamondMiner", Items.DIAMOND_PICKAXE);

	private int rank;
	private int phase;
	private String name;
	private Item pickaxe;

	@SideOnly(Side.CLIENT)
	private ItemStack renderItemStack;

	private MinerRank(int rank, int phase, String name, Item pickaxe)
	{
		this.rank = rank;
		this.phase = phase;
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

	public static MinerRank getRank(int rank)
	{
		if (rank < 0)
		{
			rank = 0;
		}

		int max = values().length - 1;

		if (rank > max)
		{
			rank = max;
		}

		return values()[rank];
	}
}