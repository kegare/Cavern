package cavern.config;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.client.config.CaveConfigEntries;
import cavern.config.manager.CaveVein;
import cavern.config.manager.CaveVeinManager;
import cavern.core.Cavern;
import cavern.util.BlockMeta;
import cavern.world.CaveType;
import cavern.world.gen.WorldGenAquaDungeons;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockStone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class AquaCavernConfig
{
	public static Configuration config;

	public static boolean dimensionDisabled;
	public static int dimensionId;
	public static int worldHeight;
	public static boolean randomSeed;

	public static boolean generateCaves;
	public static boolean generateRavine;
	public static boolean generateDungeons;

	public static String[] dungeonMobs;

	public static double caveBrightness;

	public static CaveVeinManager veinManager = new CaveVeinManager(CaveType.AQUA_CAVERN);

	public static void syncConfig()
	{
		String category = "dimension";
		Property prop;
		String comment;
		List<String> propOrder = Lists.newArrayList();

		if (config == null)
		{
			config = Config.loadConfig("aquacavern", category);
		}

		prop = config.get(category, "dimensionDisabled", false);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		dimensionDisabled = prop.getBoolean(dimensionDisabled);

		prop = config.get(category, "dimension", -52);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		dimensionId = prop.getInt(dimensionId);

		prop = config.get(category, "worldHeight", Config.highDefault ? 256 : 128);
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

		prop = config.get(category, "generateCaves", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateCaves = prop.getBoolean(generateCaves);

		prop = config.get(category, "generateRavine", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateRavine = prop.getBoolean(generateRavine);

		prop = config.get(category, "generateDungeons", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateDungeons = prop.getBoolean(generateDungeons);

		Set<Class<? extends Entity>> classes = Sets.newHashSet();

		classes.add(EntityZombie.class);
		classes.add(EntitySkeleton.class);
		classes.add(EntitySpider.class);

		Set<String> mobs = Sets.newTreeSet();

		for (Class<? extends Entity> clazz : classes)
		{
			ResourceLocation name = EntityList.getKey(clazz);

			if (name != null)
			{
				mobs.add(name.toString());
			}
		}

		prop = config.get(category, "dungeonMobs", mobs.toArray(new String[mobs.size()]));
		prop.setConfigEntryClass(CaveConfigEntries.selectMobs);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		dungeonMobs = prop.getStringList();

		prop = config.get(category, "caveBrightness", 0.075D);
		prop.setMinValue(0.0D).setMaxValue(1.0D);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		caveBrightness = prop.getDouble(caveBrightness);

		config.setCategoryPropertyOrder(category, propOrder);
		config.setCategoryLanguageKey(category, Config.LANG_KEY + category + ".cavern");

		Config.saveConfig(config);
	}

	public static void syncVeinsConfig()
	{
		if (veinManager.config == null)
		{
			veinManager.config = Config.loadConfig("aquacavern", "veins");
		}
		else
		{
			veinManager.getCaveVeins().clear();
		}

		if (veinManager.config.getCategoryNames().isEmpty())
		{
			List<CaveVein> veins = Lists.newArrayList();

			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.GRANITE.getMetadata()), 30, 25, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.DIORITE.getMetadata()), 32, 25, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.STONE, BlockStone.EnumType.ANDESITE.getMetadata()), 32, 25, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.COAL_ORE, 0), 40, 17, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.IRON_ORE, 0), 40, 10, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.GOLD_ORE, 0), 8, 7, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.REDSTONE_ORE, 0), 12, 7, 1, 40));
			veins.add(new CaveVein(new BlockMeta(Blocks.LAPIS_ORE, 0), 8, 5, 1, 50));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIAMOND_ORE, 0), 3, 6, 1, 20));
			veins.add(new CaveVein(new BlockMeta(Blocks.EMERALD_ORE, 0), 8, 5, 50, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()), 30, 8, 20, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.MAGNITE_ORE.getMetadata()), 35, 10, 1, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), 28, 6, 1, 127));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.HEXCITE_ORE.getMetadata()), 6, 5, 1, 30));
			veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.FISSURED_STONE.getMetadata()), 70, 3, 1, 255));
			veins.add(new CaveVein(new BlockMeta(Blocks.DIRT, 0), 20, 25, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.GRAVEL, 0), 10, 20, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.CLAY, 0), 30, 20, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.SAND, BlockSand.EnumType.SAND.getMetadata()), 15, 20, 1, 127));

			if (Config.highDefault)
			{
				veins.add(new CaveVein(new BlockMeta(Blocks.COAL_ORE, 0), 35, 20, 128, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.IRON_ORE, 0), 30, 12, 128, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.GOLD_ORE, 0), 5, 8, 128, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.LAPIS_ORE, 0), 4, 7, 128, 255));
				veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()), 12, 12, 128, 255, Type.COLD, Type.WATER, Type.WET));
				veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.MAGNITE_ORE.getMetadata()), 30, 10, 128, 255));
				veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), 28, 4, 128, 255));
				veins.add(new CaveVein(new BlockMeta(CaveBlocks.CAVE_BLOCK, BlockCave.EnumType.HEXCITE_ORE.getMetadata()), 4, 5, 200, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.DIRT, 0), 20, 25, 128, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.GRAVEL, 0), 10, 20, 128, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.CLAY, 0), 30, 20, 128, 255));
				veins.add(new CaveVein(new BlockMeta(Blocks.SAND, BlockSand.EnumType.SAND.getMetadata()), 10, 20, 128, 255, Type.SANDY));
			}

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

	public static void refreshDungeonMobs()
	{
		WorldGenAquaDungeons.clearDungeonMobs();
		WorldGenAquaDungeons.addDungeonMobs(Sets.newHashSet(dungeonMobs));
	}
}