package cavern.client.config;

import cavern.client.config.common.MiningPointsEntry;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CaveConfigEntries
{
	public static Class<? extends IConfigEntry> cycleInteger;

	public static Class<? extends IConfigEntry> selectBlocks;
	public static Class<? extends IConfigEntry> selectItems;
	public static Class<? extends IConfigEntry> selectMobs;

	public static Class<? extends IConfigEntry> miningPoints;

	@SideOnly(Side.CLIENT)
	public static void initEntries()
	{
		cycleInteger = CycleIntegerEntry.class;

		selectBlocks = SelectBlocksEntry.class;
		selectItems = SelectItemsEntry.class;
		selectMobs = SelectMobsEntry.class;

		miningPoints = MiningPointsEntry.class;
	}
}