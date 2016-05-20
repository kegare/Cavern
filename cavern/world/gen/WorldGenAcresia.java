package cavern.world.gen;

import java.util.Random;

import cavern.block.BlockAcresia;
import cavern.block.CaveBlocks;
import cavern.util.SimpleBlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenAcresia extends WorldGenerator
{
	@Override
	public boolean generate(World world, Random rand, BlockPos pos)
	{
		SimpleBlockPos blockpos = new SimpleBlockPos();

		for (int i = 0; i < 64; ++i)
		{
			blockpos.set(pos).add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));

			if (world.isAirBlock(blockpos) && blockpos.getY() < world.getActualHeight() - 1)
			{
				int age;

				if (blockpos.getY() >= world.getActualHeight() / 2)
				{
					age = 3 + rand.nextInt(2);
				}
				else
				{
					age = 2 + rand.nextInt(3);
				}

				IBlockState state = CaveBlocks.acresia.getDefaultState().withProperty(BlockAcresia.AGE, Integer.valueOf(age));

				if (CaveBlocks.acresia.canBlockStay(world, blockpos, state))
				{
					world.setBlockState(blockpos, state, 2);
				}
			}
		}

		return true;
	}
}