package cavern.world;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import cavern.config.CavernConfig;
import cavern.config.manager.CaveBiome;
import cavern.config.manager.CaveVein;
import cavern.world.gen.MapGenCavernCaves;
import cavern.world.gen.MapGenCavernRavine;
import cavern.world.gen.MapGenExtremeCaves;
import cavern.world.gen.MapGenExtremeRavine;
import cavern.world.gen.WorldGenCavernDungeons;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.event.terraingen.TerrainGen;

public class ChunkProviderCavern implements IChunkGenerator
{
	private final World worldObj;
	private final Random rand;

	private Biome[] biomesForGeneration;

	private MapGenBase caveGenerator = new MapGenCavernCaves();
	private MapGenBase ravineGenerator = new MapGenCavernRavine();
	private MapGenBase extremeCaveGenerator = new MapGenExtremeCaves();
	private MapGenBase extremeRavineGenerator = new MapGenExtremeRavine();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();

	private WorldGenerator lakeWaterGen = new WorldGenLakes(Blocks.WATER);
	private WorldGenerator lakeLavaGen = new WorldGenLakes(Blocks.LAVA);
	private WorldGenerator dungeonGen = new WorldGenCavernDungeons();
	private WorldGenerator liquidWaterGen = new WorldGenLiquids(Blocks.FLOWING_WATER);
	private WorldGenerator liquidLavaGen = new WorldGenLiquids(Blocks.FLOWING_LAVA);

	public ChunkProviderCavern(World world)
	{
		this.worldObj = world;
		this.rand = new Random(world.getSeed());
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		rand.setSeed(chunkX * 341873128712L + chunkZ * 132897987541L);

		int worldHeight = worldObj.provider.getActualHeight();
		int blockHeight = worldHeight - 1;

		biomesForGeneration = worldObj.getBiomeProvider().loadBlockGeneratorData(biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);

		ChunkPrimer primer = new ChunkPrimer();

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				for (int y = 255; y >= 0; --y)
				{
					primer.setBlockState(x, y, z, Blocks.STONE.getDefaultState());
				}
			}
		}

		if (CavernConfig.generateCaves)
		{
			caveGenerator.generate(worldObj, chunkX, chunkZ, primer);
		}

		if (CavernConfig.generateRavine)
		{
			ravineGenerator.generate(worldObj, chunkX, chunkZ, primer);
		}

		if (CavernConfig.generateExtremeCaves)
		{
			extremeCaveGenerator.generate(worldObj, chunkX, chunkZ, primer);
		}

		if (CavernConfig.generateExtremeRavine)
		{
			extremeRavineGenerator.generate(worldObj, chunkX, chunkZ, primer);
		}

		if (CavernConfig.generateMineshaft)
		{
			mineshaftGenerator.generate(worldObj, chunkX, chunkZ, primer);
		}

		for (int x = 0; x < 16; ++x)
		{
			for (int z = 0; z < 16; ++z)
			{
				Biome biome = biomesForGeneration[x * 16 + z];
				CaveBiome caveBiome = CavernConfig.biomeManager.getCaveBiome(biome);
				IBlockState top = caveBiome == null ? Blocks.STONE.getDefaultState() : caveBiome.getTopBlock().getBlockState();
				IBlockState filter = caveBiome == null ? top : caveBiome.getTerrainBlock().getBlockState();

				primer.setBlockState(x, 0, z, Blocks.BEDROCK.getDefaultState());
				primer.setBlockState(x, blockHeight, z, Blocks.BEDROCK.getDefaultState());

				for (int y = 1; y <= blockHeight - 1; ++y)
				{
					if (primer.getBlockState(x, y, z).getMaterial().isSolid() && primer.getBlockState(x, y + 1, z).getBlock() == Blocks.AIR)
					{
						primer.setBlockState(x, y, z, top);
					}
					else if (primer.getBlockState(x, y, z).getBlock() == Blocks.STONE)
					{
						primer.setBlockState(x, y, z, filter);
					}
				}

				if (blockHeight < 255)
				{
					for (int y = blockHeight + 1; y < 256; ++y)
					{
						primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
					}
				}
			}
		}

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
		ChunkPos coord = new ChunkPos(chunkX, chunkZ);

		if (CavernConfig.generateMineshaft)
		{
			mineshaftGenerator.generateStructure(worldObj, rand, coord);
		}

		if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
		{
			if (CavernConfig.generateLakes && rand.nextInt(4) == 0 && TerrainGen.populate(this, worldObj, rand, chunkX, chunkZ, false, EventType.LAVA))
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 16);
				z = rand.nextInt(16) + 8;

				lakeLavaGen.generate(worldObj, rand, blockPos.add(x, y, z));
			}
		}
		else if (!BiomeDictionary.isBiomeOfType(biome, Type.END))
		{
			if (CavernConfig.generateLakes)
			{
				if (!BiomeDictionary.isBiomeOfType(biome, Type.SANDY) && rand.nextInt(4) == 0 && TerrainGen.populate(this, worldObj, rand, chunkX, chunkZ, false, EventType.LAKE))
				{
					x = rand.nextInt(16) + 8;
					y = rand.nextInt(worldHeight - 16);
					z = rand.nextInt(16) + 8;

					lakeWaterGen.generate(worldObj, rand, blockPos.add(x, y, z));
				}

				if (rand.nextInt(20) == 0 && TerrainGen.populate(this, worldObj, rand, chunkX, chunkZ, false, EventType.LAVA))
				{
					x = rand.nextInt(16) + 8;
					y = rand.nextInt(worldHeight / 2);
					z = rand.nextInt(16) + 8;

					lakeLavaGen.generate(worldObj, rand, blockPos.add(x, y, z));
				}
			}
		}

		if (CavernConfig.generateDungeons && TerrainGen.populate(this, worldObj, rand, chunkX, chunkZ, false, EventType.DUNGEON))
		{
			for (int i = 0; i < 20; ++i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 30) + 5;
				z = rand.nextInt(16) + 8;

				dungeonGen.generate(worldObj, rand, blockPos.add(x, y, z));
			}
		}

		MinecraftForge.EVENT_BUS.post(new DecorateBiomeEvent.Pre(worldObj, rand, blockPos));

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Pre(worldObj, rand, blockPos));

		for (CaveVein vein : CavernConfig.veinManager.getCaveVeins())
		{
			vein.generateVeins(worldObj, rand, blockPos);
		}

		MinecraftForge.ORE_GEN_BUS.post(new OreGenEvent.Post(worldObj, rand, blockPos));

		if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.SHROOM))
		{
			int i = 0;

			if (BiomeDictionary.isBiomeOfType(biome, Type.MUSHROOM))
			{
				i += 2;
			}
			else if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
			{
				i += 1;
			}

			if (rand.nextInt(2) <= i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 16) + 10;
				z = rand.nextInt(16) + 8;

				decorator.mushroomBrownGen.generate(worldObj, rand, blockPos.add(x, y, z));
			}

			if (rand.nextInt(7) <= i)
			{
				x = rand.nextInt(16) + 8;
				y = rand.nextInt(worldHeight - 16) + 10;
				z = rand.nextInt(16) + 8;

				decorator.mushroomRedGen.generate(worldObj, rand, blockPos.add(x, y, z));
			}
		}

		if (decorator.generateLakes)
		{
			if (BiomeDictionary.isBiomeOfType(biome, Type.NETHER))
			{
				if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.LAKE_LAVA))
				{
					for (int i = 0; i < 40; ++i)
					{
						x = rand.nextInt(16) + 8;
						y = rand.nextInt(worldHeight - 12) + 10;
						z = rand.nextInt(16) + 8;

						liquidLavaGen.generate(worldObj, rand, blockPos.add(x, y, z));
					}
				}
			}
			else if (BiomeDictionary.isBiomeOfType(biome, Type.WATER))
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
			else if (!BiomeDictionary.isBiomeOfType(biome, Type.END))
			{
				if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.LAKE_WATER))
				{
					for (int i = 0; i < 50; ++i)
					{
						x = rand.nextInt(16) + 8;
						y = rand.nextInt(rand.nextInt(worldHeight - 16) + 10);
						z = rand.nextInt(16) + 8;

						liquidWaterGen.generate(worldObj, rand, blockPos.add(x, y, z));
					}
				}

				if (TerrainGen.decorate(worldObj, rand, blockPos, Decorate.EventType.LAKE_LAVA))
				{
					for (int i = 0; i < 20; ++i)
					{
						x = rand.nextInt(16) + 8;
						y = rand.nextInt(worldHeight / 2);
						z = rand.nextInt(16) + 8;

						liquidLavaGen.generate(worldObj, rand, blockPos.add(x, y, z));
					}
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
		boolean enabled = CavernConfig.monsterSpawn > 0;

		if (enabled && creatureType == EnumCreatureType.MONSTER)
		{
			return Collections.emptyList();
		}

		Biome biome = worldObj.getBiomeGenForCoords(pos);

		if (enabled && creatureType == CaveType.CAVERN_MONSTER)
		{
			return biome.getSpawnableList(EnumCreatureType.MONSTER);
		}

		return biome.getSpawnableList(creatureType);
	}

	@Override
	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos pos)
	{
		return null;
	}

	@Override
	public void recreateStructures(Chunk chunk, int x, int z)
	{
		if (CavernConfig.generateMineshaft)
		{
			mineshaftGenerator.generate(worldObj, x, z, null);
		}
	}
}