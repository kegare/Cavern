package cavern.config;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.client.config.CaveConfigEntries;
import cavern.config.manager.CaveBiome;
import cavern.config.manager.CaveBiomeManager;
import cavern.config.manager.CaveVein;
import cavern.config.manager.CaveVeinManager;
import cavern.config.property.ConfigBiomeType;
import cavern.core.Cavern;
import cavern.util.BlockMeta;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class HugeCavernConfig
{
	public static Configuration config;

	public static boolean dimensionDisabled;
	public static int dimensionId;
	public static int worldHeight;
	public static boolean randomSeed;
	public static ConfigBiomeType biomeType = new ConfigBiomeType();

	public static boolean generateCaves;
	public static boolean generateLakes;

	public static int monsterSpawn;
	public static double caveBrightness;

	public static CaveBiomeManager biomeManager = new CaveBiomeManager();
	public static CaveVeinManager veinManager = new CaveVeinManager();

	public static void syncConfig()
	{
		String category = "dimension";
		Property prop;
		String comment;
		List<String> propOrder = Lists.newArrayList();

		if (config == null)
		{
			config = Config.loadConfig("hugecavern", category);
		}

		prop = config.get(category, "dimensionDisabled", false);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		dimensionDisabled = prop.getBoolean(dimensionDisabled);

		prop = config.get(category, "dimension", -56);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		dimensionId = prop.getInt(dimensionId);

		prop = config.get(category, "worldHeight", Config.highProfiles ? 256 : 128);
		prop.setMinValue(64).setMaxValue(256);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		worldHeight = prop.getInt(worldHeight);

		prop = config.get(category, "randomSeed", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		randomSeed = prop.getBoolean(randomSeed);

		prop = config.get(category, "biomeType", ConfigBiomeType.Type.NATURAL.ordinal());
		prop.setMinValue(0).setMaxValue(ConfigBiomeType.Type.values().length - 1).setConfigEntryClass(CaveConfigEntries.cycleInteger);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";

		int min = Integer.parseInt(prop.getMinValue());
		int max = Integer.parseInt(prop.getMaxValue());

		for (int i = min; i <= max; ++i)
		{
			comment += Configuration.NEW_LINE + i + ": " + Cavern.proxy.translate(prop.getLanguageKey() + "." + i);

			if (i < max)
			{
				comment += ",";
			}
		}

		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		biomeType.setValue(prop.getInt(biomeType.getValue()));

		prop = config.get(category, "generateCaves", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateCaves = prop.getBoolean(generateCaves);

		prop = config.get(category, "generateLakes", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateLakes = prop.getBoolean(generateLakes);

		prop = config.get(category, "monsterSpawn", Config.highProfiles ? 100 : 0);
		prop.setMinValue(0).setMaxValue(5000);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		monsterSpawn = prop.getInt(monsterSpawn);

		prop = config.get(category, "caveBrightness", 0.095D);
		prop.setMinValue(0.0D).setMaxValue(1.0D);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		caveBrightness = prop.getDouble(caveBrightness);

		config.setCategoryPropertyOrder(category, propOrder);

		Config.saveConfig(config);
	}

	public static void syncBiomesConfig()
	{
		if (biomeManager.config == null)
		{
			biomeManager.config = Config.loadConfig("hugecavern", "biomes");
		}
		else
		{
			biomeManager.getCaveBiomes().clear();
		}

		if (biomeManager.config.getCategoryNames().isEmpty())
		{
			List<CaveBiome> biomes = Lists.newArrayList();

			biomes.add(new CaveBiome(Biomes.OCEAN, 15));
			biomes.add(new CaveBiome(Biomes.PLAINS, 100));
			biomes.add(new CaveBiome(Biomes.DESERT, 70));
			biomes.add(new CaveBiome(Biomes.DESERT_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.FOREST, 80));
			biomes.add(new CaveBiome(Biomes.FOREST_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.TAIGA, 80));
			biomes.add(new CaveBiome(Biomes.TAIGA_HILLS, 10));
			biomes.add(new CaveBiome(Biomes.JUNGLE, 80, null, new BlockMeta(Blocks.GRAVEL.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.JUNGLE_HILLS, 10, null, new BlockMeta(Blocks.GRAVEL.getDefaultState())));
			biomes.add(new CaveBiome(Biomes.SWAMPLAND, 60));
			biomes.add(new CaveBiome(Biomes.EXTREME_HILLS, 50));
			biomes.add(new CaveBiome(Biomes.SAVANNA, 50));
			biomes.add(new CaveBiome(Biomes.MESA, 50, null, new BlockMeta(Blocks.RED_SANDSTONE.getDefaultState())));

			CavernConfig.generateBiomesConfig(biomeManager, biomes);
		}
		else
		{
			CavernConfig.addBiomesFromConfig(biomeManager);
		}

		Config.saveConfig(biomeManager.config);
	}

	public static void syncVeinsConfig()
	{
		if (veinManager.config == null)
		{
			veinManager.config = Config.loadConfig("hugecavern", "veins");
		}
		else
		{
			veinManager.getCaveVeins().clear();
		}

		if (veinManager.config.getCategoryNames().isEmpty())
		{
			List<CaveVein> veins = Lists.newArrayList();

			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.GRANITE.getMetadata()), 15, 25, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.GRANITE.getMetadata()), 28, 25, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.DIORITE.getMetadata()), 15, 25, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.DIORITE.getMetadata()), 28, 25, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.ANDESITE.getMetadata()), 18, 25, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.ANDESITE.getMetadata()), 30, 25, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.COAL_ORE, 0), 30, 17, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.COAL_ORE, 0), 55, 17, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.IRON_ORE, 0), 35, 10, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.IRON_ORE, 0), 60, 10, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.GOLD_ORE, 0), 5, 5, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.GOLD_ORE, 0), 10, 5, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.REDSTONE_ORE, 0), 8, 6, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.REDSTONE_ORE, 0), 20, 6, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.LAPIS_ORE, 0), 10, 5, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.LAPIS_ORE, 0), 22, 5, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIAMOND_ORE, 0), 2, 5, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIAMOND_ORE, 0), 4, 5, 50, 255));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()), 10, 8, 1, 50, Type.COLD, Type.WATER, Type.WET));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()), 12, 8, 50, 255, Type.COLD, Type.WATER, Type.WET));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.MAGNITE_ORE.getMetadata()), 30, 10, 1, 50));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.MAGNITE_ORE.getMetadata()), 50, 10, 50, 255));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), 15, 4, 1, 50));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), 24, 4, 50, 255));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.HEXCITE_ORE.getMetadata()), 2, 5, 1, 50));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.HEXCITE_ORE.getMetadata()), 4, 5, 50, 255));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.FISSURED_STONE.getMetadata()), 40, 2, 1, 50));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.FISSURED_STONE.getMetadata()), 80, 2, 50, 255));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.MANALITE_ORE.getMetadata()), 3, 4, 1, 50));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.MANALITE_ORE.getMetadata()), 5, 4, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIRT, 0), 16, 25, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIRT, 0), 20, 25, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.GRAVEL, 0), 8, 20, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.GRAVEL, 0), 10, 20, 50, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.SAND, BlockSand.EnumType.SAND.getMetadata()), 8, 20, 1, 50, Type.SANDY));
			veins.add(new CaveVein(new BlockMeta(Blocks.SAND, BlockSand.EnumType.SAND.getMetadata()), 10, 20, 50, 255, Type.SANDY));

			CavernConfig.generateVeinsConfig(veinManager, veins);
		}
		else
		{
			if (CavernConfig.addVeinsFromConfig(veinManager))
			{
				try
				{
					FileUtils.forceDelete(new File(veinManager.config.toString()));

					veinManager.getCaveVeins().clear();
					veinManager.config = null;

					syncVeinsConfig();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		Config.saveConfig(veinManager.config);
	}
}