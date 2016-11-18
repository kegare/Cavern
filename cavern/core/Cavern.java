package cavern.core;

import org.apache.logging.log4j.Level;

import cavern.api.CavernAPI;
import cavern.block.CaveBlocks;
import cavern.capability.CaveCapabilities;
import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CavernConfig;
import cavern.config.Config;
import cavern.config.GeneralConfig;
import cavern.config.IceCavernConfig;
import cavern.config.MiningAssistConfig;
import cavern.config.RuinsCavernConfig;
import cavern.entity.CaveEntityRegistry;
import cavern.handler.CaveEventHooks;
import cavern.handler.CaveFuelHandler;
import cavern.handler.CavebornEventHooks;
import cavern.handler.ClientEventHooks;
import cavern.handler.MiningAssistEventHooks;
import cavern.handler.api.CavernAPIHandler;
import cavern.handler.api.DimensionHandler;
import cavern.item.CaveItems;
import cavern.network.CaveNetworkRegistry;
import cavern.plugin.HaCPlugin;
import cavern.plugin.MCEPlugin;
import cavern.stats.MinerStats;
import cavern.util.CaveLog;
import cavern.util.Version;
import cavern.world.CaveType;
import cavern.world.RuinsBlockData;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;

@Mod
(
	modid = Cavern.MODID,
	guiFactory = "cavern.client.config.CaveGuiFactory",
	updateJSON = "https://dl.dropboxusercontent.com/u/51943112/versions/cavern.json",
	acceptedMinecraftVersions = "[1.10.2]"
)
@EventBusSubscriber
public class Cavern
{
	public static final String MODID = "cavern";

	@Instance(MODID)
	public static Cavern instance;

	@Metadata(MODID)
	public static ModMetadata metadata;

	@SidedProxy(modId = MODID, clientSide = "cavern.client.ClientProxy", serverSide = "cavern.core.CommonProxy")
	public static CommonProxy proxy;

	public static final CreativeTabCavern TAB_CAVERN = new CreativeTabCavern();

	@EventHandler
	public void construct(FMLConstructionEvent event)
	{
		Version.initVersion();

		CavernAPI.apiHandler = new CavernAPIHandler();
		CavernAPI.dimension = new DimensionHandler();
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		IForgeRegistry<Block> registry = event.getRegistry();

		CaveBlocks.registerBlocks(registry);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> registry = event.getRegistry();

		CaveBlocks.registerItemBlocks(registry);
		CaveItems.registerItems(registry);
	}

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		IForgeRegistry<SoundEvent> registry = event.getRegistry();

		CaveSounds.registerSounds(registry);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Cavern.proxy.initConfigEntries();
		Cavern.proxy.registerRenderers();
		Cavern.proxy.registerKeyBindings();

		if (event.getSide().isClient())
		{
			CaveBlocks.registerModels();
			CaveItems.registerModels();
		}

		CaveBlocks.registerOreDicts();
		CaveItems.registerOreDicts();

		CaveItems.registerEquipments();

		GeneralConfig.syncConfig();

		MiningAssistConfig.syncConfig();

		GameRegistry.registerFuelHandler(new CaveFuelHandler());

		CaveNetworkRegistry.registerMessages();

		CaveCapabilities.registerCapabilities();

		MinerStats.registerMineBonus();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		if (event.getSide().isClient())
		{
			CaveBlocks.registerBlockColors();
			CaveBlocks.registerItemBlockColors();
		}

		CaveBlocks.registerRecipes();
		CaveItems.registerRecipes();

		CaveEntityRegistry.registerEntities();

		CavernConfig.syncConfig();
		CavernConfig.syncBiomesConfig();
		CavernConfig.syncVeinsConfig();

		AquaCavernConfig.syncConfig();
		AquaCavernConfig.syncVeinsConfig();

		CavelandConfig.syncConfig();
		CavelandConfig.syncVeinsConfig();

		IceCavernConfig.syncConfig();
		IceCavernConfig.syncVeinsConfig();

		RuinsCavernConfig.syncConfig();

		CaveType.registerDimensions();

		CaveAchievements.registerAchievements();

		if (event.getSide().isClient())
		{
			MinecraftForge.EVENT_BUS.register(new ClientEventHooks());
		}

		MinecraftForge.EVENT_BUS.register(new CaveEventHooks());
		MinecraftForge.EVENT_BUS.register(new CavebornEventHooks());
		MinecraftForge.EVENT_BUS.register(new MiningAssistEventHooks());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		CaveEntityRegistry.addSpawns();

		MinerStats.registerPointAmounts();

		CavernAPIHandler.registerItems(CavernAPI.apiHandler);
		CavernAPIHandler.registerEvents(CavernAPI.apiHandler);

		loadPlugins();

		RuinsBlockData.init();
	}

	public void loadPlugins()
	{
		if (Loader.isModLoaded(HaCPlugin.LIB_MODID))
		{
			try
			{
				HaCPlugin.load();
			}
			catch (Exception e)
			{
				CaveLog.log(Level.WARN, e, "Failed to load the Heat&Climate mod plugin.");
			}
		}

		if (Loader.isModLoaded(MCEPlugin.MODID))
		{
			try
			{
				MCEPlugin.load();
			}
			catch (Exception e)
			{
				CaveLog.log(Level.WARN, e, "Failed to load the MCEconomy mod plugin.");
			}
		}
	}

	@EventHandler
	public void loaded(FMLLoadCompleteEvent event)
	{
		if (GeneralConfig.miningPoints.hasInit())
		{
			GeneralConfig.miningPoints.init();
		}

		Config.saveConfig(GeneralConfig.config);
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandCavern());

		GeneralConfig.refreshMiningPointItems();
		GeneralConfig.refreshMiningPoints();

		MiningAssistConfig.refreshTargetBlocks();

		CavernConfig.refreshDungeonMobs();
		AquaCavernConfig.refreshDungeonMobs();
		IceCavernConfig.refreshDungeonMobs();
	}
}