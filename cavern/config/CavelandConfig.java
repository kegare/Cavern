package cavern.config;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

import cavern.config.manager.CaveVein;
import cavern.config.manager.CaveVeinManager;
import cavern.util.BlockMeta;
import cavern.world.CaveType;
import net.minecraft.block.BlockSand;
import net.minecraft.init.Blocks;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class CavelandConfig
{
	public static Configuration config;

	public static int dimensionId;
	public static int worldHeight;
	public static boolean randomSeed;

	public static boolean generateRiver;
	public static boolean generateLakes;

	public static int monsterSpawn;
	public static double caveBrightness;

	public static CaveVeinManager veinManager = new CaveVeinManager(CaveType.CAVELAND);

	public static void syncConfig()
	{
		String category = "dimension";
		Property prop;
		String comment;
		List<String> propOrder = Lists.newArrayList();

		if (config == null)
		{
			config = Config.loadConfig("caveland", category);
		}

		prop = config.get(category, "dimension", -53);
		prop.setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = I18n.translateToLocal(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		dimensionId = prop.getInt(dimensionId);

		if (dimensionId == 0 || DimensionManager.isDimensionRegistered(dimensionId))
		{
			dimensionId = DimensionManager.getNextFreeDimId();

			prop.set(dimensionId);
		}

		prop = config.get(category, "worldHeight", 128);
		prop.setMinValue(64).setMaxValue(256);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = I18n.translateToLocal(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		worldHeight = prop.getInt(worldHeight);

		prop = config.get(category, "randomSeed", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = I18n.translateToLocal(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		randomSeed = prop.getBoolean(randomSeed);

		prop = config.get(category, "generateRiver", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = I18n.translateToLocal(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateRiver = prop.getBoolean(generateRiver);

		prop = config.get(category, "generateLakes", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = I18n.translateToLocal(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		generateLakes = prop.getBoolean(generateLakes);

		prop = config.get(category, "monsterSpawn", 70);
		prop.setMinValue(0).setMaxValue(5000).setRequiresMcRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = I18n.translateToLocal(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		monsterSpawn = prop.getInt(monsterSpawn);

		prop = config.get(category, "caveBrightness", 0.075D);
		prop.setMinValue(0.0D).setMaxValue(1.0D);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = I18n.translateToLocal(prop.getLanguageKey() + ".tooltip");
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
			veinManager.config = Config.loadConfig("caveland", "veins");
		}
		else
		{
			veinManager.getCaveVeins().clear();
		}

		if (veinManager.config.getCategoryNames().isEmpty())
		{
			List<CaveVein> veins = Lists.newArrayList();

			veins.add(new CaveVein(new BlockMeta(Blocks.COAL_ORE, 0), 30, 17, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.COAL_ORE, 0), 20, 15, 1, 10));
			veins.add(new CaveVein(new BlockMeta(Blocks.IRON_ORE, 0), 30, 10, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.IRON_ORE, 0), 20, 8, 1, 10));
			veins.add(new CaveVein(new BlockMeta(Blocks.GRAVEL, 0), 30, 20, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.GRAVEL, 0), 10, 10, 1, 10));
			veins.add(new CaveVein(new BlockMeta(Blocks.SAND, BlockSand.EnumType.SAND.getMetadata()), 20, 20, 1, 127));
			veins.add(new CaveVein(new BlockMeta(Blocks.SAND, BlockSand.EnumType.SAND.getMetadata()), 7, 10, 1, 10));

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