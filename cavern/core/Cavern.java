package cavern.core;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import cavern.api.CavernAPI;
import cavern.block.CaveBlocks;
import cavern.capability.CaveCapabilities;
import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CavernConfig;
import cavern.config.Config;
import cavern.config.GeneralConfig;
import cavern.handler.CaveEventHooks;
import cavern.handler.CaveFuelHandler;
import cavern.handler.CavebornEventHooks;
import cavern.handler.ClientEventHooks;
import cavern.handler.api.CavernAPIHandler;
import cavern.handler.api.DimensionHandler;
import cavern.item.CaveItems;
import cavern.network.CaveNetworkRegistry;
import cavern.stats.MinerStats;
import cavern.util.BlockMeta;
import cavern.util.Version;
import cavern.world.CaveType;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod
(
	modid = Cavern.MODID,
	guiFactory = "cavern.client.config.CaveGuiFactory",
	updateJSON = "https://dl.dropboxusercontent.com/u/51943112/versions/cavern.json"
)
public class Cavern
{
	public static final String MODID = "cavern";

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(modId = MODID, clientSide = "cavern.client.ClientProxy", serverSide = "cavern.core.CommonProxy")
	public static CommonProxy proxy;

	public static final CreativeTabCavern tabCavern = new CreativeTabCavern();

	@EventHandler
	public void construct(FMLConstructionEvent event)
	{
		Version.initVersion();

		CavernAPI.apiHandler = new CavernAPIHandler();
		CavernAPI.dimension = new DimensionHandler();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Cavern.proxy.initConfigEntries();
		Cavern.proxy.registerRenderers();

		GeneralConfig.syncConfig();

		CaveBlocks.registerBlocks();
		CaveItems.registerItems();

		if (event.getSide().isClient())
		{
			CaveBlocks.registerModels();
			CaveItems.registerModels();
		}

		CaveBlocks.registerRecipes();
		CaveItems.registerRecipes();

		CaveSounds.registerSounds();

		GameRegistry.registerFuelHandler(new CaveFuelHandler());

		CaveNetworkRegistry.registerMessages();

		CaveCapabilities.registerCapabilities();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.registerBlockColors();
		proxy.registerItemColors();

		CavernConfig.syncConfig();
		CavernConfig.syncBiomesConfig();
		CavernConfig.syncVeinsConfig();

		AquaCavernConfig.syncConfig();
		AquaCavernConfig.syncVeinsConfig();

		CavelandConfig.syncConfig();
		CavelandConfig.syncVeinsConfig();

		CaveType.registerDimensions();
		CaveType.registerCreatureTypes();

		CaveAchievements.registerAchievements();

		if (event.getSide().isClient())
		{
			MinecraftForge.EVENT_BUS.register(ClientEventHooks.instance);
		}

		MinecraftForge.EVENT_BUS.register(CaveEventHooks.instance);
		MinecraftForge.EVENT_BUS.register(CavebornEventHooks.instance);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		MinerStats.setPointAmount("oreCoal", 1);
		MinerStats.setPointAmount("oreIron", 1);
		MinerStats.setPointAmount("oreGold", 1);
		MinerStats.setPointAmount("oreRedstone", 2);
		MinerStats.setPointAmount(Blocks.lit_redstone_ore, 0, 2);
		MinerStats.setPointAmount("oreLapis", 3);
		MinerStats.setPointAmount("oreEmerald", 3);
		MinerStats.setPointAmount("oreDiamond", 5);
		MinerStats.setPointAmount("oreQuartz", 2);
		MinerStats.setPointAmount("oreCopper", 1);
		MinerStats.setPointAmount("oreTin", 1);
		MinerStats.setPointAmount("oreLead", 1);
		MinerStats.setPointAmount("oreSilver", 1);
		MinerStats.setPointAmount("oreAdamantium", 1);
		MinerStats.setPointAmount("oreAluminum", 1);
		MinerStats.setPointAmount("oreApatite", 1);
		MinerStats.setPointAmount("oreMythril", 1);
		MinerStats.setPointAmount("oreOnyx", 1);
		MinerStats.setPointAmount("oreUranium", 2);
		MinerStats.setPointAmount("oreSapphire", 3);
		MinerStats.setPointAmount("oreRuby", 3);
		MinerStats.setPointAmount("oreTopaz", 2);
		MinerStats.setPointAmount("oreChrome", 1);
		MinerStats.setPointAmount("orePlatinum", 1);
		MinerStats.setPointAmount("oreTitanium", 1);
		MinerStats.setPointAmount("oreTofu", 1);
		MinerStats.setPointAmount("oreTofuDiamond", 4);
		MinerStats.setPointAmount("oreSulfur", 1);
		MinerStats.setPointAmount("oreSaltpeter", 1);
		MinerStats.setPointAmount("oreFirestone", 2);
		MinerStats.setPointAmount("oreSalt", 1);
		MinerStats.setPointAmount("oreJade", 1);
		MinerStats.setPointAmount("oreManganese", 1);
		MinerStats.setPointAmount("oreLanite", 1);
		MinerStats.setPointAmount("oreMeurodite", 1);
		MinerStats.setPointAmount("oreSoul", 1);
		MinerStats.setPointAmount("oreSunstone", 1);
		MinerStats.setPointAmount("oreZinc", 1);
		MinerStats.setPointAmount("oreCrocoite", 3);
		MinerStats.setPointAmount("glowstone", 2);
		MinerStats.setPointAmount("oreCavenium", 2);
		MinerStats.setPointAmount("oreAquamarine", 2);
		MinerStats.setPointAmount("oreMagnite", 1);
	}

	@EventHandler
	public void loaded(FMLLoadCompleteEvent event)
	{
		if (GeneralConfig.miningPoints.hasInit())
		{
			Set<String> entries = Sets.newTreeSet();

			for (Iterator<Block> iterator = Block.blockRegistry.iterator(); iterator.hasNext();)
			{
				Block block = iterator.next();

				if (block == null)
				{
					continue;
				}

				for (int i = 0; i < 16; ++i)
				{
					int point = MinerStats.getPointAmount(block, i);

					if (point > 0)
					{
						String name = block.getRegistryName().toString();
						String meta = BlockMeta.getMetaString(block, i);

						entries.add(name + ":" + meta + "," + point);
					}
				}
			}

			ConfigCategory category = GeneralConfig.config.getCategory(Configuration.CATEGORY_GENERAL);
			Property prop = category.get("miningPoints");

			if (prop != null)
			{
				prop.set(entries.toArray(new String[entries.size()]));
			}
		}

		Config.saveConfig(GeneralConfig.config);
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		GeneralConfig.refreshMiningPointItems();
		GeneralConfig.refreshMiningPoints();

		CavernConfig.refreshDungeonMobs();
		AquaCavernConfig.refreshDungeonMobs();
	}
}