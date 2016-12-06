package cavern.config;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.client.config.CaveConfigEntries;
import cavern.config.property.ConfigCaveborn;
import cavern.config.property.ConfigDisplayPos;
import cavern.config.property.ConfigItems;
import cavern.config.property.ConfigMiningPoints;
import cavern.core.Cavern;
import cavern.util.CaveUtils;
import cavern.util.ItemMeta;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;

public class GeneralConfig
{
	public static Configuration config;

	public static boolean versionNotify;

	public static double caveMusicVolume;

	public static ConfigDisplayPos miningPointPosition = new ConfigDisplayPos();
	public static boolean showMinerRank;
	public static ConfigItems miningPointItems = new ConfigItems();
	public static ConfigMiningPoints miningPoints = new ConfigMiningPoints();
	public static boolean miningCombo;
	public static boolean alwaysShowMinerStatus;

	public static ConfigCaveborn caveborn = new ConfigCaveborn();
	public static boolean cavernEscapeMission;

	public static boolean portalCache;
	public static boolean slipperyIceCustomColor;

	protected static final Side side = FMLLaunchHandler.side();

	public static void syncConfig()
	{
		String category = Configuration.CATEGORY_GENERAL;
		Property prop;
		String comment;
		List<String> propOrder = Lists.newArrayList();

		if (config == null)
		{
			config = Config.loadConfig(category);
		}

		prop = config.get(category, "versionNotify", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, does not have to match client-side and server-side.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		versionNotify = prop.getBoolean(versionNotify);

		if (side.isClient())
		{
			prop = config.get(category, "caveMusicVolume", 0.35D);
			prop.setMinValue(0.0D).setMaxValue(1.0D);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			comment += " [range: " + prop.getMinValue() + " ~ " + prop.getMaxValue() + ", default: " + prop.getDefault() + "]";
			prop.setComment(comment);
			propOrder.add(prop.getName());
			caveMusicVolume = prop.getDouble(caveMusicVolume);

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
		}

		prop = config.get(category, "showMinerRank", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, does not have to match client-side and server-side.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		showMinerRank = prop.getBoolean(showMinerRank);

		prop = config.get(category, "miningPointItems", new String[0]);
		prop.setConfigEntryClass(CaveConfigEntries.selectItems);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		miningPointItems.setValues(prop.getStringList());

		miningPoints.setInit(!config.getCategory(category).containsKey("miningPoints"));
		prop = config.get(category, "miningPoints", new String[0]);
		prop.setConfigEntryClass(CaveConfigEntries.miningPoints);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		miningPoints.setValues(prop.getStringList());

		prop = config.get(category, "miningCombo", true);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, does not have to match client-side and server-side.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		miningCombo = prop.getBoolean(miningCombo);

		if (side.isClient())
		{
			prop = config.get(category, "alwaysShowMinerStatus", false);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			comment += Configuration.NEW_LINE;
			comment += "Note: If multiplayer, client-side only.";
			prop.setComment(comment);
			propOrder.add(prop.getName());
			alwaysShowMinerStatus = prop.getBoolean(alwaysShowMinerStatus);
		}

		prop = config.get(category, "caveborn", ConfigCaveborn.Type.DISABLED.ordinal());
		prop.setMinValue(0).setMaxValue(ConfigCaveborn.Type.values().length - 1).setConfigEntryClass(CaveConfigEntries.cycleInteger);
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
		caveborn.setValue(prop.getInt(caveborn.getValue()));

		prop = config.get(category, "cavernEscapeMission", false);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		cavernEscapeMission = prop.getBoolean(cavernEscapeMission);

		prop = config.get(category, "portalCache", false);
		prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
		comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
		comment += " [default: " + prop.getDefault() + "]";
		comment += Configuration.NEW_LINE;
		comment += "Note: If multiplayer, server-side only.";
		prop.setComment(comment);
		propOrder.add(prop.getName());
		portalCache = prop.getBoolean(portalCache);

		if (side.isClient())
		{
			prop = config.get(category, "slipperyIceCustomColor", true);
			prop.setRequiresWorldRestart(true);
			prop.setLanguageKey(Config.LANG_KEY + category + "." + prop.getName());
			comment = Cavern.proxy.translate(prop.getLanguageKey() + ".tooltip");
			comment += " [default: " + prop.getDefault() + "]";
			comment += Configuration.NEW_LINE;
			comment += "Note: If multiplayer, client-side only.";
			prop.setComment(comment);
			propOrder.add(prop.getName());
			slipperyIceCustomColor = prop.getBoolean(slipperyIceCustomColor);
		}

		config.setCategoryPropertyOrder(category, propOrder);

		Config.saveConfig(config);
	}

	public static boolean refreshMiningPointItems()
	{
		if (miningPointItems == null)
		{
			return false;
		}

		miningPointItems.refreshItems();

		return true;
	}

	public static boolean isMiningPointItem(ItemStack itemstack)
	{
		if (miningPointItems == null || itemstack.isEmpty())
		{
			return false;
		}

		if (miningPointItems.getItems().isEmpty())
		{
			return CaveUtils.isItemPickaxe(itemstack);
		}

		ItemMeta itemMeta = new ItemMeta(itemstack.getItem(), itemstack.isItemStackDamageable() ? -1 : itemstack.getItemDamage());

		return miningPointItems.getItems().contains(itemMeta);
	}

	public static boolean refreshMiningPoints()
	{
		if (miningPoints == null)
		{
			return false;
		}

		miningPoints.refreshPoints();

		return true;
	}
}