package cavern.core;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.entity.EntityCavenicBear;
import cavern.entity.EntityCavenicCreeper;
import cavern.entity.EntityCavenicSkeleton;
import cavern.entity.EntityCavenicSpider;
import cavern.entity.EntityCavenicWitch;
import cavern.entity.EntityCavenicZombie;
import cavern.entity.EntityCrazyCreeper;
import cavern.entity.EntityCrazySkeleton;
import cavern.entity.EntityCrazySpider;
import cavern.entity.EntityCrazyZombie;
import cavern.item.CaveItems;
import cavern.item.ItemAcresia;
import cavern.item.ItemCave;
import cavern.stats.HunterRank;
import cavern.stats.MagicianRank;
import cavern.stats.MinerRank;
import cavern.util.ArrayListExtended;
import cavern.util.CaveUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class CaveAchievements
{
	private static final ArrayListExtended<Achievement> ACHIEVEMENTS = new ArrayListExtended<>();

	public static final Achievement CAVERN = CaveAchievement.of("cavern", 0, 0, CaveBlocks.CAVERN_PORTAL, null).initIndependentStat();
	public static final Achievement AQUA_CAVERN = CaveAchievement.of("aquaCavern", -2, 0, CaveBlocks.AQUA_CAVERN_PORTAL, null);
	public static final Achievement CAVELAND = CaveAchievement.of("caveland", 2, 0, CaveBlocks.CAVELAND_PORTAL, null);
	public static final Achievement ICE_CAVERN = CaveAchievement.of("iceCavern", -4, 0, CaveBlocks.ICE_CAVERN_PORTAL, null);
	public static final Achievement RUINS_CAVERN = CaveAchievement.of("ruinsCavern", 4, 0, CaveBlocks.RUINS_CAVERN_PORTAL, null);
	public static final Achievement AQUAMARINE = CaveAchievement.of("aquamarine", -2, -2, ItemCave.EnumType.AQUAMARINE.getItemStack(), CAVERN);
	public static final Achievement MAGNITE = CaveAchievement.of("magnite", 0, -4, ItemCave.EnumType.MAGNITE_INGOT.getItemStack(), CAVERN);
	public static final Achievement ACRESIA = CaveAchievement.of("acresia", 2, -2, new ItemStack(CaveBlocks.ACRESIA, 1, ItemAcresia.EnumType.FRUITS.getItemDamage()), CAVELAND);
	public static final Achievement RANDOMITE = CaveAchievement.of("randomite", 3, 3, new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.RANDOMITE_ORE.getMetadata()), CAVERN);
	public static final Achievement FISSURE = CaveAchievement.of("fissure", 5, 2, new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.FISSURED_STONE.getMetadata()), CAVERN);
	public static final Achievement HEXCITE = CaveAchievement.of("hexcite", -3, 4, ItemCave.EnumType.HEXCITE.getItemStack(), CAVERN);
	public static final Achievement GOOD_MINE = CaveAchievement.of("goodMine", 0, 2, Items.IRON_PICKAXE, CAVERN);
	public static final Achievement SLIP_ICE = CaveAchievement.of("slipIce", -4, -2, CaveBlocks.SLIPPERY_ICE, ICE_CAVERN);
	public static final Achievement ICE_CHARGE = CaveAchievement.of("iceCharge", -6, -2, CaveItems.ICE_PICKAXE, ICE_CAVERN);
	public static final Achievement MAGNITE_MINER = CaveAchievement.of("magniteMiner", -2, 2, MinerRank.MAGNITE_MINER.getItemStack(), CAVERN);
	public static final Achievement GOLD_MINER = CaveAchievement.of("goldMiner", -4, 2, MinerRank.GOLD_MINER.getItemStack(), MAGNITE_MINER);
	public static final Achievement AQUA_MINER = CaveAchievement.of("aquaMiner", -6, 2, MinerRank.AQUA_MINER.getItemStack(), GOLD_MINER);
	public static final Achievement DIAMOND_MINER = CaveAchievement.of("diamondMiner", -8, 2, MinerRank.DIAMOND_MINER.getItemStack(), AQUA_MINER).setSpecial();
	public static final Achievement MINER_ORB = CaveAchievement.of("orbMiner", 3, 5, ItemCave.EnumType.MINER_ORB.getItemStack(), RANDOMITE);
	public static final Achievement CAVENIC_SKELETON = CaveAchievement.of("cavenicSkeleton", -1, 7, CaveUtils.getSpawnEgg(EntityCavenicSkeleton.class), CAVERN);
	public static final Achievement CAVENIC_CREEPER = CaveAchievement.of("cavenicCreeper", -3, 7, CaveUtils.getSpawnEgg(EntityCavenicCreeper.class), CAVERN);
	public static final Achievement CAVENIC_ZOMBIE = CaveAchievement.of("cavenicZombie", 1, 7, CaveUtils.getSpawnEgg(EntityCavenicZombie.class), CAVERN);
	public static final Achievement CAVENIC_SPIDER = CaveAchievement.of("cavenicSpider", 3, 7, CaveUtils.getSpawnEgg(EntityCavenicSpider.class), CAVERN);
	public static final Achievement CAVENIC_WITCH = CaveAchievement.of("cavenicWitch", 5, 7, CaveUtils.getSpawnEgg(EntityCavenicWitch.class), CAVERN);
	public static final Achievement CAVENIC_BEAR = CaveAchievement.of("cavenicBear", -8, 0, CaveUtils.getSpawnEgg(EntityCavenicBear.class), ICE_CAVERN);
	public static final Achievement LOST_ORB = CaveAchievement.of("lostOrb", 4, -2, ItemCave.EnumType.MINER_ORB.getItemStack(), RUINS_CAVERN);
	public static final Achievement CAVENIA = CaveAchievement.of("cavenia", 0, 9, ItemCave.EnumType.CAVENIC_ORB.getItemStack(), null);
	public static final Achievement HUNTER = CaveAchievement.of("hunter", -2, 11, HunterRank.HUNTER.getItemStack(), CAVENIA);
	public static final Achievement CAVENIC_HUNTER = CaveAchievement.of("cavenicHunter", -4, 11, HunterRank.CAVENIC_HUNTER.getItemStack(), HUNTER);
	public static final Achievement CRAZY_HUNTER = CaveAchievement.of("crazyHunter", -6, 11, HunterRank.CRAZY_HUNTER.getItemStack(), CAVENIC_HUNTER);
	public static final Achievement CRAZY_RANGER = CaveAchievement.of("crazyRanger", -8, 11, HunterRank.CRAZY_RANGER.getItemStack(), CRAZY_HUNTER).setSpecial();
	public static final Achievement CRAZY_SKELETON = CaveAchievement.of("crazySkeleton", -1, 13, CaveUtils.getSpawnEgg(EntityCrazySkeleton.class), CAVENIA).setSpecial();
	public static final Achievement CRAZY_CREEPER = CaveAchievement.of("crazyCreeper", -3, 13, CaveUtils.getSpawnEgg(EntityCrazyCreeper.class), CAVENIA).setSpecial();
	public static final Achievement CRAZY_ZOMBIE = CaveAchievement.of("crazyZombie", 1, 13, CaveUtils.getSpawnEgg(EntityCrazyZombie.class), CAVENIA).setSpecial();
	public static final Achievement CRAZY_SPIDER = CaveAchievement.of("crazySpider", 3, 13, CaveUtils.getSpawnEgg(EntityCrazySpider.class), CAVENIA).setSpecial();
	public static final Achievement NOVICE_MAGICIAN = CaveAchievement.of("noviceMagician", 3, 9, MagicianRank.NOVICE_MAGICIAN.getItemStack(), null);
	public static final Achievement MAGICIAN = CaveAchievement.of("magician", 5, 9, MagicianRank.MAGICIAN.getItemStack(), NOVICE_MAGICIAN);
	public static final Achievement MAGE = CaveAchievement.of("mage", 7, 9, MagicianRank.MAGE.getItemStack(), MAGICIAN);
	public static final Achievement GRAND_MAGE = CaveAchievement.of("grandMage", 9, 9, MagicianRank.GRAND_MAGE.getItemStack(), MAGE).setSpecial();

	public static final List<Achievement> ESCAPE_ACHIEVEMENTS = Lists.newArrayList();

	public static void registerAchievements()
	{
		AchievementPage page = new AchievementPage("Cavern");
		page.getAchievements().addAll(ACHIEVEMENTS);

		AchievementPage.registerAchievementPage(page);

		ESCAPE_ACHIEVEMENTS.add(CAVERN);
		ESCAPE_ACHIEVEMENTS.add(AQUA_CAVERN);
		ESCAPE_ACHIEVEMENTS.add(CAVELAND);
		ESCAPE_ACHIEVEMENTS.add(ICE_CAVERN);
		ESCAPE_ACHIEVEMENTS.add(RUINS_CAVERN);
		ESCAPE_ACHIEVEMENTS.add(CAVENIA);
		ESCAPE_ACHIEVEMENTS.add(DIAMOND_MINER);
		ESCAPE_ACHIEVEMENTS.add(MINER_ORB);
	}

	public static int getAchievementIndex(Achievement achievement)
	{
		for (int i = 0; i < ACHIEVEMENTS.size(); ++i)
		{
			Achievement entry = ACHIEVEMENTS.get(i);

			if (entry.statId.equals(achievement.statId))
			{
				return i;
			}
		}

		return -1;
	}

	public static Achievement getAchievement(int index)
	{
		return ACHIEVEMENTS.get(index, null);
	}

	public static class CaveAchievement extends Achievement
	{
		private CaveAchievement(String name, int column, int row, ItemStack itemstack, Achievement parent)
		{
			super("achievement.cavern." + name, "cavern." + name, column, row, itemstack, parent);
		}

		public static CaveAchievement of(String name, int column, int row, Block block, Achievement parent)
		{
			return of(name, column, row, new ItemStack(block), parent);
		}

		public static CaveAchievement of(String name, int column, int row, Item item, Achievement parent)
		{
			return of(name, column, row, new ItemStack(item), parent);
		}

		public static CaveAchievement of(String name, int column, int row, ItemStack itemstack, Achievement parent)
		{
			if (itemstack.isEmpty())
			{
				itemstack = new ItemStack(Blocks.STONE);
			}

			CaveAchievement achievement = new CaveAchievement(name, column, row, itemstack, parent);

			achievement.registerStat();

			return achievement;
		}

		@Override
		public Achievement registerStat()
		{
			ACHIEVEMENTS.addIfAbsent(this);

			return super.registerStat();
		}
	}
}