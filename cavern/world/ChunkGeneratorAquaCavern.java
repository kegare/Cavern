package cavern.world;

import java.util.List;
import java.util.Random;

import cavern.config.AquaCavernConfig;
import cavern.config.manager.CaveVein;
import cavern.world.gen.MapGenAquaCaves;
import cavern.world.gen.MapGenAquaRavine;
import cavern.world.gen.WorldGenAquaDungeons;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkGeneratorAquaCavern implements IChunkGenerator
{
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState STONE = Blocks.STONE.getDefaultState();
	protected static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

	private final World world;
	private final Random rand;

	private Biome[] biomesForGeneration;

	private final MapGenBase caveGenerator = new MapGenAquaCaves();
	private final MapGenBase ravineGenerator = new MapGenAquaRavine();

	private WorldGenerator dungeonGen = new WorldGenAquaDungeons();

	public ChunkGeneratorAquaCavern(World world)
	{
		this.world = world;
		this.rand = new Random(world.getSeed());
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
		if (!ForgeEventFactory.onReplaceBiomeBlocks(this, chunkX, chunkZ, primer, world))
		{
			return;
		}

		int worldHeight = world.provider.getActualHeight();
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
	public Chunk generateChunk(int chunkX, int chunkZ)
	{
		rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);

		biomesForGeneration = world.getBiomeProvider().getBiomes(biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);

		ChunkPrimer primer = new ChunkPrimer();

		setBlocksInChunk(primer);

		if (AquaCavernConfig.generateCaves)
		{
			caveGenerator.generate(world, chunkX, chunkZ, primer);
		}

		if (AquaCavernConfig.generateRavine)
		{
			ravineGenerator.generate(world, chunkX, chunkZ, primer);
		}

		replaceBiomeBlocks(chunkX, chunkZ, primer);

		Chunk chunk = new Chunk(world, primer, chunkX, chunkZ);
		byte[] biomeArray = chunk.getBiomeArray();

		for (int i = 0; i < biomeArray.length; ++i)
		{
			biomeArray[i] = (byte)Biome.getIdForBiome(biomesForGeneration[i]);
		}

		chunk.resetRelightChecks();

		return chunk;
	}

	@Override
	public void populate(int chunkX, int chunkZ)
	{
		BlockFalling.fallInstantly = true;

		int worldX = chunkX * 16;
		int worldZ = chunkZ * 16;
		BlockPos blockPos = new BlockPos(worldX, 0, worldZ);
		int worldHeight = world.provider.getActualHeight();

		ForgeEventFactory.onChunkPopulate(true, this, world, rand, chunkX, chunkZ, false);

		int x, y, z;

		if (AquaCavernConfig.generateDungeons && TerrainGen.populate(this, world, rand, chunkX, chunkZ, false, EventType.DUNGEON))
		{
			for (int i = 0; i < 20; ++i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 30) + 5;
				z = rand.nextInt(16) + 8;

				dungeonGen.generate(world, rand, blockPos.add(x, y, z));
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(world, rand, blockPos));

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(world, rand, blockPos));

		for (CaveVein vein : AquaCavernConfig.veinManager.getCaveVeins())
		{
			vein.generateVeins(world, rand, blockPos);
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(world, rand, blockPos));

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(world, rand, blockPos));

		ForgeEventFactory.onChunkPopulate(false, this, world, rand, chunkX, chunkZ, false);

		BlockFalling.fallInstantly = false;
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