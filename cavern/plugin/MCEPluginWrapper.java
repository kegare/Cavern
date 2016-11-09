package cavern.plugin;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.entity.EntityCavenicCreeper;
import cavern.entity.EntityCavenicSkeleton;
import cavern.entity.EntityCavenicSpider;
import cavern.entity.EntityCavenicZombie;
import cavern.item.CaveItems;
import cavern.item.ItemAcresia;
import cavern.item.ItemCave;
import cavern.stats.MinerRank;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import shift.mceconomy3.api.MCEconomyAPI;

public class MCEPluginWrapper
{
	public static final MCEPortalShop PORTAL = new MCEPortalShop();

	public static void registerShops()
	{
		PORTAL.addProduct(new ItemStack(Blocks.TORCH, 16), 20);
		PORTAL.addProduct(new ItemStack(Items.BREAD, 3), 30, MinerRank.STONE_MINER);
		PORTAL.addProduct(new ItemStack(Items.BONE, 2), 10);

		for (int i = 0; i < 3; ++i)
		{
			PORTAL.addProduct(new ItemStack(Blocks.LOG, 1, i), 30);
		}

		MCEPlugin.PORTAL_SHOP = MCEconomyAPI.ShopManager.registerShop(PORTAL);
	}

	public static void registerPurchaseItems()
	{
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.CAVERN_PORTAL, 1, OreDictionary.WILDCARD_VALUE), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.AQUA_CAVERN_PORTAL, 1, OreDictionary.WILDCARD_VALUE), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.CAVELAND_PORTAL, 1, OreDictionary.WILDCARD_VALUE), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.ICE_CAVERN_PORTAL, 1, OreDictionary.WILDCARD_VALUE), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.RUINS_CAVERN_PORTAL, 1, OreDictionary.WILDCARD_VALUE), -1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.AQUAMARINE_ORE.getMetadata()), 60);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.AQUAMARINE_BLOCK.getMetadata()), 315);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.MAGNITE_ORE.getMetadata()), 50);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.MAGNITE_BLOCK.getMetadata()), 900);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), 75);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.HEXCITE_ORE.getMetadata()), 2200);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.HEXCITE_BLOCK.getMetadata()), 9900);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.FISSURED_STONE.getMetadata()), 75);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.FISSURED_PACKED_ICE.getMetadata()), 75);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.ACRESIA, 1, ItemAcresia.EnumType.SEEDS.getItemDamage()), 0);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.ACRESIA, 1, ItemAcresia.EnumType.FRUITS.getItemDamage()), 1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.PERVERTED_LOG, 1, OreDictionary.WILDCARD_VALUE), 1);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.PERVERTED_LEAVES, 1, OreDictionary.WILDCARD_VALUE), 0);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.PERVERTED_SAPLING, 1, OreDictionary.WILDCARD_VALUE), 0);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveBlocks.SLIPPERY_ICE), 110);

		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.AQUAMARINE.getItemDamage()), 35);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.MAGNITE_INGOT.getItemDamage()), 100);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.HEXCITE.getItemDamage()), 1100);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.ICE_STICK.getItemDamage()), 2);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.MINER_ORB.getItemDamage()), 5000);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.AQUAMARINE_PICKAXE), 110);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.AQUAMARINE_AXE), 110);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.AQUAMARINE_SHOVEL), 40);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.MAGNITE_SWORD), 205);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.MAGNITE_PICKAXE), 305);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.MAGNITE_AXE), 305);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.MAGNITE_SHOVEL), 105);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_SWORD), 2205);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_PICKAXE), 3305);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_AXE), 3305);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_SHOVEL), 1105);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_HOE), 2205);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_HELMET), 5505);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_CHESTPLATE), 8805);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_LEGGINGS), 7705);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.HEXCITE_BOOTS), 4405);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ICE_SWORD), 135);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ICE_PICKAXE), 200);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ICE_AXE), 200);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ICE_SHOVEL), 70);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ICE_HOE), 135);
		MCEconomyAPI.addPurchaseItem(new ItemStack(CaveItems.ICE_BOW), 30);

		MCEconomyAPI.ShopManager.addPurchaseEntity(EntityCavenicSkeleton.class, 50);
		MCEconomyAPI.ShopManager.addPurchaseEntity(EntityCavenicCreeper.class, 50);
		MCEconomyAPI.ShopManager.addPurchaseEntity(EntityCavenicZombie.class, 50);
		MCEconomyAPI.ShopManager.addPurchaseEntity(EntityCavenicSpider.class, 50);
	}
}