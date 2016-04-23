package cavern.block;

import java.util.Random;

import cavern.core.Cavern;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockLeavesPerverted extends BlockOldLeaf
{
	public BlockLeavesPerverted()
	{
		super();
		this.setUnlocalizedName("pervertedLeaves");
		this.setHardness(0.05F);
		this.setCreativeTab(Cavern.tabCavern);
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(CaveBlocks.perverted_sapling);
	}

	@Override
	protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {}
}