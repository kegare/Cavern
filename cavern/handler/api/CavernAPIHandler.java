package cavern.handler.api;

import java.util.Set;

import cavern.api.CavernAPI;
import cavern.api.ICavernAPI;
import cavern.api.IFissureBreakEvent;
import cavern.api.IIceEquipment;
import cavern.api.IMineBonus;
import cavern.api.IMinerStats;
import cavern.block.BlockCave;
import cavern.block.bonus.FissureBreakEvent;
import cavern.block.bonus.FissureEventBreathing;
import cavern.block.bonus.FissureEventExplosion;
import cavern.block.bonus.FissureEventPotion;
import cavern.block.bonus.WeightedItem;
import cavern.item.CaveItems;
import cavern.item.IceEquipment;
import cavern.item.ItemCave;
import cavern.stats.MinerStats;
import cavern.world.WorldProviderIceCavern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CavernAPIHandler implements ICavernAPI
{
	@Override
	public IMinerStats getMinerStats(EntityPlayer player)
	{
		return MinerStats.get(player);
	}

	@Override
	public Set<IMineBonus> getMineBonus()
	{
		return MinerStats.MINE_BONUS;
	}

	@Override
	public void addMineBonus(IMineBonus bonus)
	{
		MinerStats.MINE_BONUS.add(bonus);
	}

	@Override
	public void addRandomiteItem(ItemStack item, int weight)
	{
		BlockCave.RANDOMITE_ITEMS.add(new WeightedItem(item, weight));
	}

	@Override
	public void addRandomiteItem(Item item, int weight)
	{
		addRandomiteItem(new ItemStack(item), weight);
	}

	@Override
	public void addRandomiteItem(Item item, int amount, int weight)
	{
		addRandomiteItem(new ItemStack(item, amount), weight);
	}

	@Override
	public void addRandomiteItem(Block block, int weight)
	{
		addRandomiteItem(new ItemStack(block), weight);
	}

	@Override
	public void addRandomiteItem(Block block, int amount, int weight)
	{
		addRandomiteItem(new ItemStack(block, amount), weight);
	}

	@Override
	public void addHibernateItem(ItemStack item, int weight)
	{
		WorldProviderIceCavern.HIBERNATE_ITEMS.add(new WeightedItem(item, weight));
	}

	@Override
	public void addHibernateItem(Item item, int weight)
	{
		addHibernateItem(new ItemStack(item), weight);
	}

	@Override
	public void addHibernateItem(Item item, int amount, int weight)
	{
		addHibernateItem(new ItemStack(item, amount), weight);
	}

	@Override
	public void addHibernateItem(Block block, int weight)
	{
		addHibernateItem(new ItemStack(block), weight);
	}

	@Override
	public void addHibernateItem(Block block, int amount, int weight)
	{
		addHibernateItem(new ItemStack(block, amount), weight);
	}

	@Override
	public void addFissureBreakEvent(IFissureBreakEvent event, int weight)
	{
		BlockCave.FISSURE_EVENTS.add(new FissureBreakEvent(event, weight));
	}

	@Override
	public void registerIceEquipment(Item item)
	{
		IceEquipment.register(item);
	}

	@Override
	public boolean isIceEquipment(Item item)
	{
		return IceEquipment.isIceEquipment(item);
	}

	@Override
	public boolean isIceEquipment(ItemStack item)
	{
		return IceEquipment.isIceEquipment(item);
	}

	@Override
	public IIceEquipment getIceEquipment(ItemStack item)
	{
		return IceEquipment.get(item);
	}

	@Override
	public ItemStack getChargedIceItem(Item item, int charge)
	{
		return IceEquipment.getChargedItem(item, charge);
	}

	public static void registerItems()
	{
		CavernAPI.apiHandler.addRandomiteItem(Blocks.DIRT, 6, 15);
		CavernAPI.apiHandler.addRandomiteItem(Blocks.SAND, 6, 12);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.OAK.getMetadata()), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()), 20);
		CavernAPI.apiHandler.addRandomiteItem(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.BIRCH.getMetadata()), 20);
		CavernAPI.apiHandler.addRandomiteItem(Blocks.TORCH, 5, 35);
		CavernAPI.apiHandler.addRandomiteItem(Items.COAL, 5, 20);
		CavernAPI.apiHandler.addRandomiteItem(Items.IRON_INGOT, 20);
		CavernAPI.apiHandler.addRandomiteItem(Items.GOLD_INGOT, 10);
		CavernAPI.apiHandler.addRandomiteItem(Items.EMERALD, 10);
		CavernAPI.apiHandler.addRandomiteItem(Items.APPLE, 3, 30);
		CavernAPI.apiHandler.addRandomiteItem(Items.BAKED_POTATO, 3, 30);
		CavernAPI.apiHandler.addRandomiteItem(Items.BREAD, 2, 30);
		CavernAPI.apiHandler.addRandomiteItem(Items.COOKED_BEEF, 20);
		CavernAPI.apiHandler.addRandomiteItem(Items.COOKED_CHICKEN, 20);
		CavernAPI.apiHandler.addRandomiteItem(Items.COOKED_FISH, 20);
		CavernAPI.apiHandler.addRandomiteItem(Items.COOKED_MUTTON, 20);
		CavernAPI.apiHandler.addRandomiteItem(Items.COOKED_PORKCHOP, 20);
		CavernAPI.apiHandler.addRandomiteItem(Items.COOKED_RABBIT, 20);
		CavernAPI.apiHandler.addRandomiteItem(Items.BONE, 5, 30);
		CavernAPI.apiHandler.addRandomiteItem(Items.IRON_SWORD, 10);
		CavernAPI.apiHandler.addRandomiteItem(Items.IRON_PICKAXE, 10);
		CavernAPI.apiHandler.addRandomiteItem(Items.IRON_AXE, 10);
		CavernAPI.apiHandler.addRandomiteItem(Items.IRON_SHOVEL, 10);
		CavernAPI.apiHandler.addRandomiteItem(Items.IRON_HOE, 8);
		CavernAPI.apiHandler.addRandomiteItem(Items.DIAMOND, 3);
		CavernAPI.apiHandler.addRandomiteItem(Items.DIAMOND_SWORD, 2);
		CavernAPI.apiHandler.addRandomiteItem(Items.DIAMOND_PICKAXE, 2);
		CavernAPI.apiHandler.addRandomiteItem(Items.DIAMOND_AXE, 2);
		CavernAPI.apiHandler.addRandomiteItem(Items.DIAMOND_SHOVEL, 2);
		CavernAPI.apiHandler.addRandomiteItem(Items.DIAMOND_HOE, 1);

		CavernAPI.apiHandler.addHibernateItem(new ItemStack(CaveItems.CAVE_ITEM, 8, ItemCave.EnumType.ICE_STICK.getItemDamage()), 30);
		CavernAPI.apiHandler.addHibernateItem(IceEquipment.getChargedItem(CaveItems.ICE_SWORD, 20), 10);
		CavernAPI.apiHandler.addHibernateItem(IceEquipment.getChargedItem(CaveItems.ICE_PICKAXE, 30), 10);
		CavernAPI.apiHandler.addHibernateItem(IceEquipment.getChargedItem(CaveItems.ICE_AXE, 30), 10);
		CavernAPI.apiHandler.addHibernateItem(IceEquipment.getChargedItem(CaveItems.ICE_SHOVEL, 10), 10);
		CavernAPI.apiHandler.addHibernateItem(IceEquipment.getChargedItem(CaveItems.ICE_HOE, 20), 10);
		CavernAPI.apiHandler.addHibernateItem(Items.BONE, 6, 30);
		CavernAPI.apiHandler.addHibernateItem(Items.FISH, 4, 30);
		CavernAPI.apiHandler.addHibernateItem(Items.BEEF, 2, 15);
		CavernAPI.apiHandler.addHibernateItem(Items.CHICKEN, 2, 15);
		CavernAPI.apiHandler.addHibernateItem(Items.MUTTON, 2, 15);
		CavernAPI.apiHandler.addHibernateItem(Items.PORKCHOP, 2, 15);
		CavernAPI.apiHandler.addHibernateItem(Items.RABBIT, 2, 15);
		CavernAPI.apiHandler.addHibernateItem(Items.APPLE, 15);
		CavernAPI.apiHandler.addHibernateItem(Items.GOLDEN_APPLE, 5);
		CavernAPI.apiHandler.addHibernateItem(Items.PUMPKIN_PIE, 15);
		CavernAPI.apiHandler.addHibernateItem(Blocks.REEDS, 4, 12);
		CavernAPI.apiHandler.addHibernateItem(Items.STICK, 4, 15);
		CavernAPI.apiHandler.addHibernateItem(Items.COAL, 4, 15);
		CavernAPI.apiHandler.addHibernateItem(new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.AQUAMARINE.getItemDamage()), 10);
		CavernAPI.apiHandler.addHibernateItem(new ItemStack(CaveItems.CAVE_ITEM, 1, ItemCave.EnumType.HEXCITE.getItemDamage()), 3);
	}

	public static void registerEvents()
	{
		CavernAPI.apiHandler.addFissureBreakEvent(new FissureEventPotion(), 100);
		CavernAPI.apiHandler.addFissureBreakEvent(new FissureEventExplosion(), 10);
		CavernAPI.apiHandler.addFissureBreakEvent(new FissureEventBreathing(), 50);
	}
}