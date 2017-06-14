package cavern.core;

import cavern.api.CavernAPI;
import cavern.block.CaveBlocks;
import cavern.capability.CaveCapabilities;
import cavern.client.CaveKeyBindings;
import cavern.client.CaveRenderingRegistry;
import cavern.client.config.CaveConfigEntries;
import cavern.client.handler.ClientEventHooks;
import cavern.client.handler.HunterStatsHUDEventHooks;
import cavern.client.handler.MagicSpellEventHooks;
import cavern.client.handler.MagicianStatsHUDEventHooks;
import cavern.client.handler.MinerStatsHUDEventHooks;
import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CaveniaConfig;
import cavern.config.CavernConfig;
import cavern.config.Config;
import cavern.config.GeneralConfig;
import cavern.config.IceCavernConfig;
import cavern.config.MiningAssistConfig;
import cavern.config.RuinsCavernConfig;
import cavern.entity.CaveEntityRegistry;
import cavern.handler.CaveEventHooks;
import cavern.handler.CaveFuelHandler;
import cavern.handler.CaveGuiHandler;
import cavern.handler.CavebornEventHooks;
import cavern.handler.CaveniaEventHooks;
import cavern.handler.MiningAssistEventHooks;
import cavern.handler.api.CavernAPIHandler;
import cavern.handler.api.DimensionHandler;
import cavern.item.CaveItems;
import cavern.network.CaveNetworkRegistry;
import cavern.recipe.CompositingManager;
import cavern.stats.MinerStats;
import cavern.util.Version;
import cavern.world.CaveType;
import cavern.world.RuinsBlockData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
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
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod
(
	modid = Cavern.MODID,
	guiFactory = "cavern.client.config.CaveGuiFactory",
	updateJSON = "https://raw.githubusercontent.com/kegare/Cavern/master/cavern.json",
	dependencies = "required-after:Forge@[13.20.0.2262,)"
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
		CavernAPI.compositing = new CompositingManager();

		if (event.getSide().isClient())
		{
			clientConstruct();
		}
	}

	@SideOnly(Side.CLIENT)
	public void clientConstruct()
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.isJava64bit() && Runtime.getRuntime().maxMemory() >= 2000000000L)
		{
			Config.highDefault = true;
		}

		CaveConfigEntries.initEntries();
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
		if (event.getSide().isClient())
		{
			CaveBlocks.registerModels();
			CaveItems.registerModels();

			CaveRenderingRegistry.registerRenderers();
			CaveRenderingRegistry.registerRenderBlocks();

			CaveKeyBindings.registerKeyBindings();
		}

		CaveBlocks.registerOreDicts();
		CaveItems.registerOreDicts();

		CaveItems.registerEquipments();

		GeneralConfig.syncConfig();

		MiningAssistConfig.syncConfig();

		GameRegistry.registerFuelHandler(new CaveFuelHandler());

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new CaveGuiHandler());

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

		CompositingManager.registerRecipes(CavernAPI.compositing);

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

		CaveniaConfig.syncConfig();
		CaveniaConfig.syncBiomesConfig();
		CaveniaConfig.syncVeinsConfig();

		CaveType.registerDimensions();

		CaveAchievements.registerAchievements();

		if (event.getSide().isClient())
		{
			MinecraftForge.EVENT_BUS.register(new ClientEventHooks());
			MinecraftForge.EVENT_BUS.register(new MinerStatsHUDEventHooks());
			MinecraftForge.EVENT_BUS.register(new HunterStatsHUDEventHooks());
			MinecraftForge.EVENT_BUS.register(new MagicianStatsHUDEventHooks());
			MinecraftForge.EVENT_BUS.register(new MagicSpellEventHooks());
		}

		MinecraftForge.EVENT_BUS.register(new CaveEventHooks());
		MinecraftForge.EVENT_BUS.register(new CavebornEventHooks());
		MinecraftForge.EVENT_BUS.register(new MiningAssistEventHooks());
		MinecraftForge.EVENT_BUS.register(new CaveniaEventHooks());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		CaveEntityRegistry.addSpawns();

		MinerStats.registerPointAmounts();

		CavernAPIHandler.registerItems(CavernAPI.apiHandler);
		CavernAPIHandler.registerEvents(CavernAPI.apiHandler);

		RuinsBlockData.init();
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

		MiningAssistConfig.refreshEffectiveItems();
		MiningAssistConfig.refreshTargetBlocks();

		CavernConfig.refreshDungeonMobs();
		AquaCavernConfig.refreshDungeonMobs();
		IceCavernConfig.refreshDungeonMobs();
	}
}