package cavern.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenRuinsCaves extends MapGenCavernCaves
{
	@Override
	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int x, int z, ChunkPrimer primer)
	{
		int worldHeight = world.provider.getActualHeight();
		int chance = rand.nextInt(rand.nextInt(rand.nextInt(25) + 1) + 1);

		if (rand.nextInt(4) != 0)
		{
			chance = 0;
		}

		for (int i = 0; i < chance; ++i)
		{
			double blockX = chunkX * 16 + rand.nextInt(16);
			double blockY = rand.nextInt(rand.nextInt(worldHeight - 8) + 8);
			double blockZ = chunkZ * 16 + rand.nextInt(16);
			int count = 1;

			if (rand.nextInt(3) == 0)
			{
				addRoom(rand.nextLong(), x, z, primer, blockX, blockY, blockZ);

				count += rand.nextInt(4);
			}

			for (int j = 0; j < count; ++j)
			{
				float leftRightRadian = rand.nextFloat() * (float)Math.PI * 2.0F;
				float upDownRadian = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float scale = rand.nextFloat() * 1.75F + rand.nextFloat();

				if (rand.nextInt(10) == 0)
				{
					scale *= rand.nextFloat() * rand.nextFloat() * 2.0F + 1.0F;
				}

				addTunnel(rand.nextLong(), x, z, primer, blockX, blockY, blockZ, scale, leftRightRadian, upDownRadian, 0, 0, 1.0D);
			}
		}
	}

	@Override
	protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop, IBlockState state, IBlockState up)
	{
		if (y < 70 || y > 95)
		{
			data.setBlockState(x, y, z, BLK_AIR);
		}
	}
}