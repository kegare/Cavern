package cavern.block;

import cavern.item.CaveItems;
import cavern.item.ItemAcresia;
import cavern.item.ItemBlockCave;
import cavern.item.ItemBlockPerverted;
import cavern.item.ItemCave;
import cavern.item.ItemPortalCave;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class CaveBlocks
{
	public static final BlockPortalCavern cavern_portal = new BlockPortalCavern();
	public static final BlockPortalAquaCavern aqua_cavern_portal = new BlockPortalAquaCavern();
	public static final BlockPortalCaveland caveland_portal = new BlockPortalCaveland();
	public static final BlockCave cave_block = new BlockCave();
	public static final BlockAcresia acresia = new BlockAcresia();
	public static final BlockLogPerverted perverted_log = new BlockLogPerverted();
	public static final BlockLeavesPerverted perverted_leaves = new BlockLeavesPerverted();
	public static final BlockSaplingPerverted perverted_sapling = new BlockSaplingPerverted();

	public static void registerBlocks()
	{
		cavern_portal.setRegistryName("cavern_portal");
		aqua_cavern_portal.setRegistryName("aqua_cavern_portal");
		caveland_portal.setRegistryName("caveland_portal");
		cave_block.setRegistryName("cave_block");
		acresia.setRegistryName("acresia");
		perverted_log.setRegistryName("perverted_log");
		perverted_leaves.setRegistryName("perverted_leaves");
		perverted_sapling.setRegistryName("perverted_sapling");

		GameRegistry.register(cavern_portal);
		GameRegistry.register(new ItemPortalCave(cavern_portal));

		GameRegistry.register(aqua_cavern_portal);
		GameRegistry.register(new ItemPortalCave(aqua_cavern_portal));

		GameRegistry.register(caveland_portal);
		GameRegistry.register(new ItemPortalCave(caveland_portal));

		GameRegistry.register(cave_block);
		GameRegistry.register(new ItemBlockCave(cave_block));

		GameRegistry.register(acresia);
		GameRegistry.register(new ItemAcresia(acresia));

		GameRegistry.register(perverted_log);
		GameRegistry.register(new ItemBlockPerverted(perverted_log, Blocks.log));

		GameRegistry.register(perverted_leaves);
		GameRegistry.register(new ItemBlockPerverted(perverted_leaves, Blocks.leaves));

		GameRegistry.register(perverted_sapling);
		GameRegistry.register(new ItemBlockPerverted(perverted_sapling, Blocks.sapling));

		OreDictionary.registerOre("oreAquamarine", new ItemStack(cave_block, 1, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()));
		OreDictionary.registerOre("oreMagnite", new ItemStack(cave_block, 1, BlockCave.EnumType.MAGNITE_ORE.getMetadata()));
		OreDictionary.registerOre("treeLeaves", new ItemStack(perverted_leaves, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("treeSapling", new ItemStack(perverted_sapling, 1, OreDictionary.WILDCARD_VALUE));
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels()
	{
		ModelLoader.setCustomStateMapper(cave_block, new StateMap.Builder().withName(BlockCave.VARIANT).build());
		ModelLoader.setCustomStateMapper(perverted_log, new StateMap.Builder().withName(BlockOldLog.VARIANT).withSuffix("_log").build());
		ModelLoader.setCustomStateMapper(perverted_leaves, new StateMap.Builder().withName(BlockOldLeaf.VARIANT).withSuffix("_leaves").ignore(new IProperty[] {BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE}).build());
		ModelLoader.setCustomStateMapper(perverted_sapling, new StateMap.Builder().withName(BlockSapling.TYPE).withSuffix("_sapling").build());

		registerModel(cavern_portal, "cavern_portal");
		registerModel(aqua_cavern_portal, "aqua_cavern_portal");
		registerModel(caveland_portal, "caveland_portal");
		registerModelWithMeta(cave_block, "aquamarine_ore", "aquamarine_block", "magnite_ore", "magnite_block");
		registerModelWithMeta(acresia, "acresia_seeds", "acresia_fruits");
		registerVanillaModelWithMeta(perverted_log, "oak_log", "spruce_log", "birch_log", "jungle_log");
		registerVanillaModelWithMeta(perverted_leaves, "oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves");
		registerVanillaModelWithMeta(perverted_sapling, "oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling");
	}

	@SideOnly(Side.CLIENT)
	public static void registerModel(Block block, String modelName)
	{
		CaveItems.registerModel(Item.getItemFromBlock(block), modelName);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModelWithMeta(Block block, String... modelName)
	{
		CaveItems.registerModelWithMeta(Item.getItemFromBlock(block), modelName);
	}

	@SideOnly(Side.CLIENT)
	public static void registerVanillaModel(Block block, String modelName)
	{
		CaveItems.registerVanillaModel(Item.getItemFromBlock(block), modelName);
	}

	@SideOnly(Side.CLIENT)
	public static void registerVanillaModelWithMeta(Block block, String... modelName)
	{
		CaveItems.registerVanillaModelWithMeta(Item.getItemFromBlock(block), modelName);
	}

	public static void registerRecipes()
	{
		GameRegistry.addShapedRecipe(new ItemStack(cave_block, 1, BlockCave.EnumType.AQUAMARINE_BLOCK.getMetadata()),
			"AAA", "AAA", "AAA",
			'A', new ItemStack(CaveItems.cave_item, 1, ItemCave.EnumType.AQUAMARINE.getItemDamage())
		);

		GameRegistry.addShapedRecipe(new ItemStack(cave_block, 1, BlockCave.EnumType.MAGNITE_BLOCK.getMetadata()),
			"MMM", "MMM", "MMM",
			'M', new ItemStack(CaveItems.cave_item, 1, ItemCave.EnumType.MAGNITE_INGOT.getItemDamage())
		);

		GameRegistry.addShapelessRecipe(new ItemStack(Items.stick, 8), new ItemStack(perverted_log, 1, OreDictionary.WILDCARD_VALUE));

		for (BlockPlanks.EnumType type : BlockOldLog.VARIANT.getAllowedValues())
		{
			int meta = type.getMetadata();

			GameRegistry.addShapedRecipe(new ItemStack(Blocks.planks, 4, meta),
				"LL", "LL",
				'L', new ItemStack(perverted_log, 1, meta)
			);

			GameRegistry.addShapelessRecipe(new ItemStack(perverted_sapling, 1, meta), new ItemStack(Blocks.sapling, 1, meta), Items.fermented_spider_eye);
		}

		GameRegistry.addSmelting(new ItemStack(cave_block, 1, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()),
			new ItemStack(CaveItems.cave_item, 1, ItemCave.EnumType.AQUAMARINE.getItemDamage()), 1.0F);

		GameRegistry.addSmelting(new ItemStack(cave_block, 1, BlockCave.EnumType.MAGNITE_ORE.getMetadata()),
			new ItemStack(CaveItems.cave_item, 1, ItemCave.EnumType.MAGNITE_INGOT.getItemDamage()), 0.7F);

		GameRegistry.addSmelting(new ItemStack(perverted_log, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Items.coal, 1, 1), 0.0F);
	}
}