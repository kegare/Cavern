package cavern.config;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.client.config.CaveConfigEntries;
import cavern.config.property.ConfigDisplayPos;
import cavern.core.Cavern;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class DisplayConfig
{
	public static Configuration config;

	public static boolean customLoadingScreen;

	public static ConfigDisplayPos miningPointPosition = new ConfigDisplayPos();
	public static boolean showMinerRank;
	public static boolean alwaysShowMinerStatus;

	public static ConfigDisplayPos huntingPointPosition = new ConfigDisplayPos();
	public static boolean showHunterRank;

	public static ConfigDisplayPos magicianPointPosition = new ConfigDisplayPos();
	public static boolean showMagicianRank;

	public static boolean slipperyIceCustomColor;

	public static void syncConfig()
	{
		String category = "display";
		Property prop;
		String comment;
		List<String> propOrder = Lists.newArrayList();

		if (config == null)
		{
			config = Config.loadConfig(category);
		}

		prop = config.get(category, "customLoadingScreen", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		customLoadingScreen = prop.getBoolean(customLoadingScreen);

		prop = config.get(category, "miningPointPosition", ConfigDisplayPos.Type.BOTTOM_RIGHT.ordinal());
		prop.setMinValue(0).setMaxValue(ConfigDisplayPos.Type.values().length - 1).setConfigEntryClass(CaveConfigEntries.cycleInteger);
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

		prop.setComment(comment);
		propOrder.add(prop.getName());
		miningPointPosition.setValue(prop.getInt(miningPointPosition.getValue()));

		prop = config.get(category, "showMinerRank", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		showMinerRank = prop.getBoolean(showMinerRank);

		prop = config.get(category, "alwaysShowMinerStatus", false);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		prop.setComment(comment);
		propOrder.add(prop.getName());
		alwaysShowMinerStatus = prop.getBoolean(alwaysShowMinerStatus);

		prop = config.get(category, "huntingPointPosition", ConfigDisplayPos.Type.BOTTOM_RIGHT.ordinal());
		prop.setMinValue(0).setMaxValue(ConfigDisplayPos.Type.values().length - 1).setConfigEntryClass(CaveConfigEntries.cycleInteger);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";

		min = Integer.parseInt(prop.getMinValue());
		max = Integer.parseInt(prop.getMaxValue());

		for (int i = min; i <= max; ++i)
		{
			comment += Configuration.NEW_LINE + i + ": " + Cavern.proxy.translate(prop.getLanguageKey() + "." + i);

			if (i < max)
			{
				comment += ",";
			}
		}

		prop.setComment(comment);
		propOrder.add(prop.getName());
		huntingPointPosition.setValue(prop.getInt(huntingPointPosition.getValue()));

		prop = config.get(category, "showHunterRank", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		showHunterRank = prop.getBoolean(showHunterRank);

		prop = config.get(category, "magicianPointPosition", ConfigDisplayPos.Type.BOTTOM_RIGHT.ordinal());
		prop.setMinValue(0).setMaxValue(ConfigDisplayPos.Type.values().length - 1).setConfigEntryClass(CaveConfigEntries.cycleInteger);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";

		min = Integer.parseInt(prop.getMinValue());
		max = Integer.parseInt(prop.getMaxValue());

		for (int i = min; i <= max; ++i)
		{
			comment += Configuration.NEW_LINE + i + ": " + Cavern.proxy.translate(prop.getLanguageKey() + "." + i);

			if (i < max)
			{
				comment += ",";
			}
		}

		prop.setComment(comment);
		propOrder.add(prop.getName());
		magicianPointPosition.setValue(prop.getInt(magicianPointPosition.getValue()));

		prop = config.get(category, "showMagicianRank", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		showMagicianRank = prop.getBoolean(showMagicianRank);

		prop = config.get(category, "slipperyIceCustomColor", true);
		prop.setRequiresWorldRestart(true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		slipperyIceCustomColor = prop.getBoolean(slipperyIceCustomColor);

		config.setCategoryPropertyOrder(category, propOrder);

		Config.saveConfig(config);
	}
}