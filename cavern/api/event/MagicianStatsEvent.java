package cavern.api.event;

import cavern.api.IMagicianStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public class MagicianStatsEvent extends PlayerEvent
{
	private final IMagicianStats stats;

	public MagicianStatsEvent(EntityPlayer player, IMagicianStats stats)
	{
		super(player);
		this.stats = stats;
	}

	public IMagicianStats getStats()
	{
		return stats;
	}

	@Cancelable
	public static class AddPoint extends MagicianStatsEvent
	{
		private final int originalPoint;

		private int newPoint;

		public AddPoint(EntityPlayer player, IMagicianStats stats, int point)
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

	public static class PromoteRank extends MagicianStatsEvent
	{
		public PromoteRank(EntityPlayer player, IMagicianStats stats)
		{
			super(player, stats);
		}
	}
}