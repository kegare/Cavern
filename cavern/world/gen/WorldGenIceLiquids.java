package cavern.world.gen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenIceLiquids extends WorldGenerator
{
	private final Block block;

	public WorldGenIceLiquids(Block liquid)
	{
		this.block = liquid;
	}

	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		if (world.getBlockState(pos.up()).getBlock() != Blocks.PACKED_ICE)
		{
			return false;
		}
		else if (world.getBlockState(pos.down()).getBlock() != Blocks.PACKED_ICE)
		{
			return false;
		}
		else
		{
			IBlockState state = world.getBlockState(pos);

			if (!state.getBlock().isAir(state, world, pos) && state.getBlock() != Blocks.PACKED_ICE)
			{
				return false;
			}
			else
			{
				int i = 0;

				if (world.getBlockState(pos.west()).getBlock() == Blocks.PACKED_ICE)
				{
					++i;
				}

				if (world.getBlockState(pos.east()).getBlock() == Blocks.PACKED_ICE)
				{
					++i;
				}

				if (world.getBlockState(pos.north()).getBlock() == Blocks.PACKED_ICE)
				{
					++i;
				}

				if (world.getBlockState(pos.south()).getBlock() == Blocks.PACKED_ICE)
				{
					++i;
				}

				int j = 0;

				if (world.isAirBlock(pos.west()))
				{
					++j;
				}

				if (world.isAirBlock(pos.east()))
				{
					++j;
				}

				if (world.isAirBlock(pos.north()))
				{
					++j;
				}

				if (world.isAirBlock(pos.south()))
				{
					++j;
				}

				if (i == 3 && j == 1)
				{
					IBlockState blockstate = block.getDefaultState();

					world.setBlockState(pos, blockstate, 2);
					world.immediateBlockTick(pos, blockstate, rand);
				}

				return true;
			}
		}
	}
}