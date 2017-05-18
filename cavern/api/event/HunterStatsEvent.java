package cavern.api.event;

import cavern.api.IHunterStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class HunterStatsEvent extends PlayerEvent
{
	private final IHunterStats stats;

	public HunterStatsEvent(EntityPlayer player, IHunterStats stats)
	{
		super(player);
		this.stats = stats;
	}

	public IHunterStats getStats()
	{
		return stats;
	}

	@Cancelable
	public static class AddPoint extends HunterStatsEvent
	{
		private final int originalPoint;

		private int newPoint;

		public AddPoint(EntityPlayer player, IHunterStats stats, int point)
		{
			super(player, stats);
			this.originalPoint = point;
			this.newPoint = point;
		}

		public int getPoint()
		{
			return originalPoint;
		}

		public int getNewPoint()
		{
			return newPoint;
		}

		public void setNewPoint(int point)
		{
			newPoint = point;
		}
	}

	public static class PromoteRank extends HunterStatsEvent
	{
		public PromoteRank(EntityPlayer player, IHunterStats stats)
		{
			super(player, stats);
		}
	}
}