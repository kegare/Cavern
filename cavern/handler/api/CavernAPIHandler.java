package cavern.handler.api;

import java.util.List;
import java.util.Set;

import cavern.api.ICavernAPI;
import cavern.api.IFissureBreakEvent;
import cavern.api.IHunterStats;
import cavern.api.IIceEquipment;
import cavern.api.IMagicianStats;
import cavern.api.IMineBonus;
import cavern.api.IMinerStats;
import cavern.block.BlockCave;
import cavern.block.bonus.FissureBreakEvent;
import cavern.block.bonus.FissureEventExplosion;
import cavern.block.bonus.FissureEventPotion;
import cavern.block.bonus.FissureEventRefresh;
import cavern.item.CaveItems;
import cavern.item.IceEquipment;
import cavern.item.ItemCave;
import cavern.item.ItemElixir;
import cavern.item.ItemMagicalBook;
import cavern.stats.HunterStats;
import cavern.stats.MagicianStats;
import cavern.stats.MinerStats;
import cavern.util.WeightedItemStack;
import cavern.world.WorldProviderIceCavern;
import cavern.world.WorldProviderRuinsCavern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CavernAPIHandler implements ICavernAPI
{
	@Override
	public IMinerStats getMinerStats(EntityPlayer player)
	{
		return MinerStats.get(player);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getMineCombo()
	{
		return MinerStats.mineCombo;
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
	public IHunterStats getHunterStats(EntityPlayer player)
	{
		return HunterStats.get(player);
	}

	@Override
	public IMagicianStats getMagicianStats(EntityPlayer player)
	{
		return MagicianStats.get(player);
	}

	@Override
	public void addRandomiteItem(ItemStack item, int weight)
	{
		BlockCave.RANDOMITE_ITEMS.add(new WeightedItemStack(item, weight));
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
		WorldProviderIceCavern.HIBERNATE_ITEMS.add(new WeightedItemStack(item, weight));
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

	@Override
	public void addRuinsChestItem(ItemStack item, int weight)
	{
		WorldProviderRuinsCavern.RUINS_CHEST_ITEMS.add(new WeightedItemStack(item, weight));
	}

	@Override
	public void addRuinsChestItem(Item item, int weight)
	{
		addRuinsChestItem(new ItemStack(item), weight);
	}

	@Override
	public void addRuinsChestItem(Item item, int amount, int weight)
	{
		addRuinsChestItem(new ItemStack(item, amount), weight);
	}

	@Override
	public void addRuinsChestItem(Block block, int weight)
	{
		addRuinsChestItem(new ItemStack(block), weight);
	}

	@Override
	public void addRuinsChestItem(Block block, int amount, int weight)
	{
		addRuinsChestItem(new ItemStack(block, amount), weight);
	}

	@Override
	public void addMagicalItem(Item item)
	{
		getMagicalItems().add(item);
	}

	@Override
	public List<Item> getMagicalItems()
	{
		return CaveItems.getMagicalItems();
	}

	public static void registerItems(ICavernAPI handler)
	{
		handler.addRandomiteItem(Blocks.DIRT, 6, 15);
		handler.addRandomiteItem(Blocks.SAND, 6, 12);
		handler.addRandomiteItem(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.OAK.getMetadata()), 15);
		handler.addRandomiteItem(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()), 15);
		handler.addRandomiteItem(new ItemStack(Blocks.LOG, 1, BlockPlanks.EnumType.BIRCH.getMetadata()), 15);
		handler.addRandomiteItem(new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.OAK.getMetadata()), 8);
		handler.addRandomiteItem(new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()), 8);
		handler.addRandomiteItem(new ItemStack(Blocks.SAPLING, 1, BlockPlanks.EnumType.BIRCH.getMetadata()), 8);
		handler.addRandomiteItem(Blocks.TORCH, 5, 30);
		handler.addRandomiteItem(Items.COAL, 5, 20);
		handler.addRandomiteItem(Items.IRON_INGOT, 20);
		handler.addRandomiteItem(Items.GOLD_INGOT, 10);
		handler.addRandomiteItem(Items.EMERALD, 10);
		handler.addRandomiteItem(Items.APPLE, 3, 25);
		handler.addRandomiteItem(Items.BAKED_POTATO, 3, 20);
		handler.addRandomiteItem(Items.BREAD, 2, 18);
		handler.addRandomiteItem(Items.COOKED_BEEF, 15);
		handler.addRandomiteItem(Items.COOKED_CHICKEN, 15);
		handler.addRandomiteItem(Items.COOKED_FISH, 15);
		handler.addRandomiteItem(Items.COOKED_MUTTON, 15);
		handler.addRandomiteItem(Items.COOKED_PORKCHOP, 15);
		handler.addRandomiteItem(Items.COOKED_RABBIT, 15);
		handler.addRandomiteItem(Items.BONE, 5, 30);
		handler.addRandomiteItem(Items.IRON_SWORD, 8);
		handler.addRandomiteItem(Items.IRON_PICKAXE, 10);
		handler.addRandomiteItem(Items.IRON_AXE, 10);
		handler.addRandomiteItem(Items.IRON_SHOVEL, 10);
		handler.addRandomiteItem(Items.IRON_HOE, 8);
		handler.addRandomiteItem(Items.DIAMOND, 2);
		handler.addRandomiteItem(Items.DIAMOND_SWORD, 1);
		handler.addRandomiteItem(Items.DIAMOND_PICKAXE, 1);
		handler.addRandomiteItem(Items.DIAMOND_AXE, 1);
		handler.addRandomiteItem(Items.DIAMOND_SHOVEL, 1);
		handler.addRandomiteItem(Items.DIAMOND_HOE, 1);
		handler.addRandomiteItem(ItemCave.EnumType.MINER_ORB.getItemStack(), 1);
		handler.addRandomiteItem(ItemMagicalBook.EnumType.UNKNOWN.getItemStack(), 2);

		handler.addHibernateItem(ItemCave.EnumType.ICE_STICK.getItemStack(8), 30);
		handler.addHibernateItem(IceEquipment.getChargedItem(CaveItems.ICE_SWORD, 20), 10);
		handler.addHibernateItem(IceEquipment.getChargedItem(CaveItems.ICE_PICKAXE, 30), 10);
		handler.addHibernateItem(IceEquipment.getChargedItem(CaveItems.ICE_AXE, 30), 10);
		handler.addHibernateItem(IceEquipment.getChargedItem(CaveItems.ICE_SHOVEL, 10), 10);
		handler.addHibernateItem(IceEquipment.getChargedItem(CaveItems.ICE_HOE, 20), 10);
		handler.addHibernateItem(Items.BONE, 6, 30);
		handler.addHibernateItem(Items.FISH, 4, 30);
		handler.addHibernateItem(Items.BEEF, 2, 15);
		handler.addHibernateItem(Items.CHICKEN, 2, 15);
		handler.addHibernateItem(Items.MUTTON, 2, 15);
		handler.addHibernateItem(Items.PORKCHOP, 2, 15);
		handler.addHibernateItem(Items.RABBIT, 2, 15);
		handler.addHibernateItem(Items.APPLE, 15);
		handler.addHibernateItem(Items.GOLDEN_APPLE, 5);
		handler.addHibernateItem(Items.PUMPKIN_PIE, 15);
		handler.addHibernateItem(Blocks.REEDS, 4, 12);
		handler.addHibernateItem(Items.STICK, 4, 15);
		handler.addHibernateItem(Items.COAL, 4, 15);
		handler.addHibernateItem(ItemCave.EnumType.AQUAMARINE.getItemStack(), 10);
		handler.addHibernateItem(ItemCave.EnumType.HEXCITE.getItemStack(), 5);
		handler.addHibernateItem(ItemElixir.EnumType.ELIXIR_NORMAL.getItemStack(), 20);
		handler.addHibernateItem(ItemElixir.EnumType.ELIXIR_MEDIUM.getItemStack(), 15);
		handler.addHibernateItem(ItemElixir.EnumType.ELIXIR_HIGH.getItemStack(), 8);
		handler.addHibernateItem(ItemMagicalBook.EnumType.UNKNOWN.getItemStack(), 15);

		handler.addRuinsChestItem(Blocks.DIRT, 64, 15);
		handler.addRuinsChestItem(Blocks.SAND, 32, 10);
		handler.addRuinsChestItem(Blocks.GRAVEL, 32, 10);
		handler.addRuinsChestItem(Blocks.COBBLESTONE, 64, 20);
		handler.addRuinsChestItem(new ItemStack(Blocks.LOG, 16, BlockPlanks.EnumType.OAK.getMetadata()), 5);
		handler.addRuinsChestItem(new ItemStack(Blocks.LOG, 16, BlockPlanks.EnumType.SPRUCE.getMetadata()), 5);
		handler.addRuinsChestItem(Blocks.TORCH, 32, 25);
		handler.addRuinsChestItem(Items.BREAD, 10, 10);
		handler.addRuinsChestItem(Items.IRON_SWORD, 10);
		handler.addRuinsChestItem(Items.IRON_PICKAXE, 10);
		handler.addRuinsChestItem(Items.IRON_SHOVEL, 10);
		handler.addRuinsChestItem(Items.IRON_AXE, 10);
		handler.addRuinsChestItem(Items.COAL, 16, 15);
		handler.addRuinsChestItem(Items.IRON_INGOT, 3, 13);
		handler.addRuinsChestItem(Items.GOLD_INGOT, 3, 5);
		handler.addRuinsChestItem(ItemCave.EnumType.AQUAMARINE.getItemStack(), 10);
		handler.addRuinsChestItem(ItemCave.EnumType.MAGNITE_INGOT.getItemStack(3), 12);
	}

	public static void registerEvents(ICavernAPI handler)
	{
		handler.addFissureBreakEvent(new FissureEventPotion(), 100);
		handler.addFissureBreakEvent(new FissureEventExplosion(), 10);
		handler.addFissureBreakEvent(new FissureEventRefresh(), 50);
	}
}