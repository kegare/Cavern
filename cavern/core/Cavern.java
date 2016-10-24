package cavern.core;

import java.util.Set;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Sets;

import cavern.api.CavernAPI;
import cavern.block.CaveBlocks;
import cavern.capability.CaveCapabilities;
import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CavernConfig;
import cavern.config.Config;
import cavern.config.GeneralConfig;
import cavern.entity.CaveEntityRegistry;
import cavern.handler.CaveEventHooks;
import cavern.handler.CaveFuelHandler;
import cavern.handler.CavebornEventHooks;
import cavern.handler.ClientEventHooks;
import cavern.handler.api.CavernAPIHandler;
import cavern.handler.api.DimensionHandler;
import cavern.item.CaveItems;
import cavern.network.CaveNetworkRegistry;
import cavern.plugin.HaCPlugin;
import cavern.stats.MinerStats;
import cavern.util.BlockMeta;
import cavern.util.CaveLog;
import cavern.util.Version;
import cavern.world.CaveType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
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

@Mod
(
	modid = Cavern.MODID,
	guiFactory = "cavern.client.config.CaveGuiFactory",
	updateJSON = "https://dl.dropboxusercontent.com/u/51943112/versions/cavern.json",
	acceptedMinecraftVersions = "[1.10.2,)",
	dependencies = "required-after:Forge@[12.18.2.2099,)"
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
		CaveBlocks.registerBlocks();
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		CaveItems.registerItems();
	}

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event)
	{
		CaveSounds.registerSounds();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Cavern.proxy.initConfigEntries();
		Cavern.proxy.registerRenderers();

		if (event.getSide().isClient())
		{
			CaveBlocks.registerModels();
			CaveItems.registerModels();
		}

		GeneralConfig.syncConfig();

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

		CaveType.registerDimensions();

		CaveAchievements.registerAchievements();

		if (event.getSide().isClient())
		{
			MinecraftForge.EVENT_BUS.register(new ClientEventHooks());
		}

		MinecraftForge.EVENT_BUS.register(new CaveEventHooks());
		MinecraftForge.EVENT_BUS.register(new CavebornEventHooks());
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		CaveEntityRegistry.addSpawns();

		MinerStats.setPointAmount("oreCoal", 1);
		MinerStats.setPointAmount("oreIron", 1);
		MinerStats.setPointAmount("oreGold", 1);
		MinerStats.setPointAmount("oreRedstone", 2);
		MinerStats.setPointAmount(Blocks.LIT_REDSTONE_ORE, 0, 2);
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

		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Blocks.DIRT, 6), 15);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Blocks.SAND, 6), 12);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.OAK.getMetadata()), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.BIRCH.getMetadata()), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Blocks.TORCH, 2), 50);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.COAL, 5), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.IRON_INGOT), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.GOLD_INGOT), 10);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.EMERALD), 10);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.APPLE, 3), 30);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.BAKED_POTATO, 3), 30);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.BREAD, 2), 30);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.COOKED_BEEF), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.COOKED_CHICKEN), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.COOKED_FISH), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.COOKED_MUTTON), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.COOKED_PORKCHOP), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.COOKED_RABBIT), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.BONE, 5), 30);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.IRON_SWORD), 10);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.IRON_PICKAXE), 10);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.IRON_AXE), 10);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.IRON_SHOVEL), 10);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.IRON_HOE), 8);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.DIAMOND), 3);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.DIAMOND_SWORD), 2);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.DIAMOND_PICKAXE), 2);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.DIAMOND_AXE), 2);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.DIAMOND_SHOVEL), 2);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Items.DIAMOND_HOE), 1);

		loadPlugins();
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
				CaveLog.log(Level.WARN, e, "Failed to load the HaC mod plugin.");
			}
		}
	}

	@EventHandler
	public void loaded(FMLLoadCompleteEvent event)
	{
		if (GeneralConfig.miningPoints.hasInit())
		{
			Set<String> entries = Sets.newTreeSet();

			for (Block block : Block.REGISTRY)
			{
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
		event.registerServerCommand(new CommandCavern());

		GeneralConfig.refreshMiningPointItems();
		GeneralConfig.refreshMiningPoints();

		CavernConfig.refreshDungeonMobs();
		AquaCavernConfig.refreshDungeonMobs();
	}
}