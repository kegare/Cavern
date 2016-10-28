package cavern.world;

import java.util.List;
import java.util.Random;

import cavern.config.CavernConfig;
import cavern.config.IceCavernConfig;
import cavern.config.manager.CaveVein;
import cavern.world.gen.MapGenIceCaves;
import cavern.world.gen.MapGenIceRavine;
import cavern.world.gen.WorldGenIceDungeons;
import cavern.world.gen.WorldGenIceLiquids;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkProviderIceCavern implements IChunkGenerator
{
	protected static final IBlockState AIR = Blocks.AIR.getDefaultState();
	protected static final IBlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
	protected static final IBlockState ICE = Blocks.ICE.getDefaultState();
	protected static final IBlockState WATER = Blocks.WATER.getDefaultState();
	protected static final IBlockState BEDROCK = Blocks.BEDROCK.getDefaultState();

	private final World worldObj;
	private final Random rand;

	private Biome[] biomesForGeneration;

	private MapGenBase caveGenerator = new MapGenIceCaves();
	private MapGenBase ravineGenerator = new MapGenIceRavine();

	private WorldGenerator lakeWaterGen = new WorldGenLakes(Blocks.WATER);
	private WorldGenerator liquidWaterGen = new WorldGenIceLiquids(Blocks.FLOWING_WATER);
	private WorldGenerator dungeonGen = new WorldGenIceDungeons();

	public ChunkProviderIceCavern(World world)
	{
		this.worldObj = world;
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
					primer.setBlockState(x, y, z, PACKED_ICE);
				}
			}
		}
	}

	public void replaceBiomeBlocks(int chunkX, int chunkZ, ChunkPrimer primer)
	{
		if (!ForgeEventFactory.onReplaceBiomeBlocks(this, chunkX, chunkZ, primer, worldObj))
		{
			return;
		}

		int worldHeight = worldObj.provider.getActualHeight();
		int blockHeight = worldHeight - 1;

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				primer.setBlockState(x, 0, z, BEDROCK);
				primer.setBlockState(x, blockHeight, z, BEDROCK);
				primer.setBlockState(x, 1, z, PACKED_ICE);

				for (int y = 10; y <= 20; ++y)
				{
					if (primer.getBlockState(x, y, z).getMaterial().isSolid() && primer.getBlockState(x, y + 1, z).getBlock() == Blocks.AIR)
					{
						if (rand.nextDouble() < 0.3D)
						{
							primer.setBlockState(x, y, z, WATER);
						}
						else
						{
							primer.setBlockState(x, y, z, ICE);
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
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);

		biomesForGeneration = worldObj.getBiomeProvider().loadBlockGeneratorData(biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);

		ChunkPrimer primer = new ChunkPrimer();

		setBlocksInChunk(primer);

		if (CavernConfig.generateCaves)
		{
			caveGenerator.generate(worldObj, chunkX, chunkZ, primer);
		}

		if (CavernConfig.generateRavine)
		{
			ravineGenerator.generate(worldObj, chunkX, chunkZ, primer);
		}

		replaceBiomeBlocks(chunkX, chunkZ, primer);

		Chunk chunk = new Chunk(worldObj, primer, chunkX, chunkZ);
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
		Biome biome = worldObj.getBiomeGenForCoords(blockPos.add(16, 0, 16));
		BiomeDecorator decorator = biome.theBiomeDecorator;
		int worldHeight = worldObj.provider.getActualHeight();

		ForgeEventFactory.onChunkPopulate(true, this, worldObj, rand, chunkX, chunkZ, false);

		int x, y, z;

		if (IceCavernConfig.generateLakes)
		{
			if (!BiomeDictionary.isBiomeOfType(biome, Type.SANDY) && rand.nextInt(4) == 0 && TerrainGen.populate(this, worldObj, rand, chunkX, chunkZ, false, EventType.LAKE))
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 16);
				z = rand.nextInt(16) + 8;

				lakeWaterGen.generate(worldObj, rand, blockPos.add(x, y, z));
			}
		}

		if (IceCavernConfig.generateDungeons && TerrainGen.populate(this, worldObj, rand, chunkX, chunkZ, false, EventType.DUNGEON))
		{
			for (int i = 0; i < 20; ++i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 30) + 15;
				z = rand.nextInt(16) + 8;

				dungeonGen.generate(worldObj, rand, blockPos.add(x, y, z));
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(worldObj, rand, blockPos));

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, rand, blockPos));

		for (CaveVein vein : IceCavernConfig.veinManager.getCaveVeins())
		{
			vein.generateVeins(worldObj, rand, blockPos);
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(worldObj, rand, blockPos));

		if (decorator.generateLakes)
		{
			if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.LAKE_WATER))
			{
				for (int i = 0; i < 65; ++i)
				{
					x = rand.nextInt(16) + 8;
					y = rand.nextInt(rand.nextInt(worldHeight - 16) + 10);
					z = rand.nextInt(16) + 8;

					liquidWaterGen.generate(worldObj, rand, blockPos.add(x, y, z));
				}
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Post(worldObj, rand, blockPos));

		ForgeEventFactory.onChunkPopulate(false, this, worldObj, rand, chunkX, chunkZ, false);

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
		Biome biome = worldObj.getBiomeGenForCoords(pos);

		return biome.getSpawnableList(creatureType);
	}

	@Override
	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos pos)
	{
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunk, int x, int z) {}
}