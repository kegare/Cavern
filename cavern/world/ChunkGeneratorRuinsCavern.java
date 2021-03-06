package cavern.world;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import cavern.config.RuinsCavernConfig;
import cavern.util.WeightedItemStack;
import cavern.world.gen.MapGenRuinsCaves;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
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
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;

public class ChunkGeneratorRuinsCavern implements IChunkGenerator
{
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
	protected static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();
	protected static final IBlockState PODZOL = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.PODZOL);

	private final World world;
	private final Random rand;

	private MapGenBase caveGenerator = new MapGenRuinsCaves();

	public ChunkGeneratorRuinsCavern(World world)
	{
		this.world = world;
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
		int worldHeight = world.provider.getActualHeight();
		int blockHeight = worldHeight - 1;

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				primer.setBlockState(x, 0, z, BEDROCK);
				primer.setBlockState(x, blockHeight, z, BEDROCK);

				if (RuinsCavernConfig.generateCaves)
				{
					for (int y = 1; y <= blockHeight - 1; ++y)
					{
						if (primer.getBlockState(x, y, z).getBlock() == Blocks.STONE && primer.getBlockState(x, y + 1, z).getBlock() == Blocks.AIR)
						{
							primer.setBlockState(x, y, z, PODZOL);
						}
					}
				}

				if (blockHeight < 255)
				{
					for (int y = blockHeight + 1; y < 256; ++y)
					{
						primer.setBlockState(x, y, z, AIR);
					}
				}
			}
		}

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
	}

	@Override
	public Chunk generateChunk(int chunkX, int chunkZ)
	{
		ChunkPrimer primer = new ChunkPrimer();

		setBlocksInChunk(primer);

		if (RuinsCavernConfig.generateCaves)
		{
			caveGenerator.generate(world, chunkX, chunkZ, primer);
		}

		replaceBiomeBlocks(chunkX, chunkZ, primer);

		Chunk chunk = new Chunk(world, primer, chunkX, chunkZ);
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
		if (chunkX != 0 || chunkZ != 0)
		{
			return;
		}

		for (Pair<BlockPos, IBlockState> data : RuinsBlockData.Torch.BLOCKS)
		{
			BlockPos pos = data.getLeft();
			IBlockState state = data.getRight();

			if (RuinsCavernConfig.decorateTorches)
			{
				world.setBlockState(pos, state, 3);
				world.checkLightFor(EnumSkyBlock.BLOCK, pos);
			}
			else
			{
				world.setBlockState(pos, AIR, 2);
			}
		}

		for (Pair<BlockPos, IBlockState> data : RuinsBlockData.TileEntity.BLOCKS)
		{
			BlockPos pos = data.getLeft();
			IBlockState state = data.getRight();

			world.setBlockState(pos, state, 2);

			TileEntity tile = world.getTileEntity(pos);

			if (tile != null && rand.nextDouble() <= RuinsCavernConfig.bonusChest && tile instanceof TileEntityChest)
			{
				TileEntityChest chest = (TileEntityChest)tile;

				for (int i = 0; i < 18; ++i)
				{
					WeightedItemStack randomItem = WeightedRandom.getRandomItem(rand, WorldProviderRuinsCavern.RUINS_CHEST_ITEMS);
					ItemStack stack = randomItem.getItemStack();

					if (!stack.isEmpty())
					{
						int count = stack.getCount();

						if (count > 1)
						{
							int min = count / 2;

							stack.setCount(Math.max(rand.nextInt(count) + 1, min));
						}

						chest.setInventorySlotContents(i, stack);
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
		Biome biome = world.getBiome(pos);

		return biome.getSpawnableList(creatureType);
	}

	@Override
	public boolean isInsideStructure(World world, String structureName, BlockPos pos)
	{
		return false;
	}

	@Override
	public BlockPos getNearestStructurePos(World world, String structureName, BlockPos pos, boolean findUnexplored)
	{
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunk, int x, int z) {}
}