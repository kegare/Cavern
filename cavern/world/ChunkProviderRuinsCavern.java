package cavern.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import cavern.config.RuinsCavernConfig;
import cavern.util.WeightedItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;

public class ChunkProviderRuinsCavern implements IChunkGenerator
{
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
	protected static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

	private final World worldObj;
	private final Random rand;

	public ChunkProviderRuinsCavern(World world)
	{
		this.worldObj = world;
		this.rand = new Random();
	}

	public void setBlocksInChunk(ChunkPrimer primer)
	{
		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				for (int y = 255; y >= 0; --y)
				{
					primer.setBlockState(x, y, z, STONE);
				}
			}
		}
	}

	public void replaceBiomeBlocks(int chunkX, int chunkZ, ChunkPrimer primer)
	{
		ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
		List<Pair<BlockPos, IBlockState>> list = RuinsBlockData.BLOCKS_MAP.get(chunkPos);

		if (list != null && !list.isEmpty())
		{
			for (Pair<BlockPos, IBlockState> data : list)
			{
				BlockPos pos = data.getLeft();

				primer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), data.getRight());
			}
		}

		int worldHeight = worldObj.provider.getActualHeight();
		int blockHeight = worldHeight - 1;

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				primer.setBlockState(x, 0, z, BEDROCK);
				primer.setBlockState(x, blockHeight, z, BEDROCK);

				if (blockHeight < 255)
				{
					for (int y = blockHeight + 1; y < 256; ++y)
					{
						primer.setBlockState(x, y, z, AIR);
					}
				}
			}
		}
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		ChunkPrimer primer = new ChunkPrimer();

		setBlocksInChunk(primer);

		replaceBiomeBlocks(chunkX, chunkZ, primer);

		Chunk chunk = new Chunk(worldObj, primer, chunkX, chunkZ);
		byte[] biomeArray = chunk.getBiomeArray();

		for (int i = 0; i < biomeArray.length; ++i)
		{
			biomeArray[i] = (byte)Biome.getIdForBiome(Biomes.PLAINS);
		}

		chunk.resetRelightChecks();

		return chunk;
	}

	@Override
	public void populate(int chunkX, int chunkZ)
	{
		if (chunkX == 0 && chunkZ == 0)
		{
			for (Pair<BlockPos, IBlockState> data : RuinsBlockData.Torch.BLOCKS)
			{
				BlockPos pos = data.getLeft();
				IBlockState state = data.getRight();

				if (RuinsCavernConfig.decorateTorches)
				{
					worldObj.setBlockState(pos, state, 3);
					worldObj.checkLightFor(EnumSkyBlock.BLOCK, pos);
				}
				else
				{
					worldObj.setBlockState(pos, AIR, 2);
				}
			}

			for (Pair<BlockPos, IBlockState> data : RuinsBlockData.TileEntity.BLOCKS)
			{
				BlockPos pos = data.getLeft();
				IBlockState state = data.getRight();

				worldObj.setBlockState(pos, state, 2);

				TileEntity tile = worldObj.getTileEntity(pos);

				if (tile != null && rand.nextDouble() <= RuinsCavernConfig.bonusChest && tile instanceof TileEntityChest)
				{
					TileEntityChest chest = (TileEntityChest)tile;

					for (int i = 0; i < 18; ++i)
					{
						WeightedItem randomItem = WeightedRandom.getRandomItem(rand, WorldProviderRuinsCavern.RUINS_CHEST_ITEMS);

						if (randomItem != null)
						{
							ItemStack item = randomItem.getItem();

							if (item != null)
							{
								if (item.stackSize > 1)
								{
									int min = item.stackSize / 2;

									item.stackSize = Math.max(rand.nextInt(item.stackSize) + 1, min);
								}

								chest.setInventorySlotContents(i, item);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean generateStructures(Chunk chunk, int x, int z)
	{
		return false;
	}

	@Override
	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos)
	{
		return Collections.emptyList();
	}

	@Override
	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos pos)
	{
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunk, int x, int z) {}
}