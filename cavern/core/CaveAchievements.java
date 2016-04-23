package cavern.core;

import cavern.block.CaveBlocks;
import cavern.item.CaveItems;
import cavern.item.ItemAcresia;
import cavern.item.ItemCave;
import cavern.util.ArrayListExtended;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;

public class CaveAchievements
{
	private static final ArrayListExtended<Achievement> ACHIEVEMENTS = new ArrayListExtended<>();

	public static final Achievement cavern = CaveAchievement.of("cavern", 0, 0, CaveBlocks.cavern_portal, null).initIndependentStat();
	public static final Achievement aquaCavern = CaveAchievement.of("aquaCavern", -2, 0, CaveBlocks.aqua_cavern_portal, cavern);
	public static final Achievement caveland = CaveAchievement.of("caveland", 2, 0, CaveBlocks.caveland_portal, cavern);
	public static final Achievement aquamarine = CaveAchievement.of("aquamarine", -3, -3, new ItemStack(CaveItems.cave_item, 1, ItemCave.EnumType.AQUAMARINE.getItemDamage()), cavern);
	public static final Achievement magnite = CaveAchievement.of("magnite", 0, -4, new ItemStack(CaveItems.cave_item, 1, ItemCave.EnumType.MAGNITE_INGOT.getItemDamage()), cavern);
	public static final Achievement acresia = CaveAchievement.of("acresia", 3, -3, new ItemStack(CaveBlocks.acresia, 1, ItemAcresia.EnumType.FRUITS.getItemDamage()), caveland);

	public static void registerAchievements()
	{
		AchievementPage page = new AchievementPage("Cavern");
		page.getAchievements().addAll(ACHIEVEMENTS);

		AchievementPage.registerAchievementPage(page);
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
			if (itemstack.getItem() == null)
			{
				itemstack = new ItemStack(Blocks.stone);
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