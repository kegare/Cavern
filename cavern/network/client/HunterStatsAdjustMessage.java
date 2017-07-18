package cavern.network.client;

import cavern.api.IHunterStats;
import cavern.stats.HunterStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HunterStatsAdjustMessage implements IPlayerMessage<HunterStatsAdjustMessage, IMessage>
{
	private int point;
	private int rank;

	public HunterStatsAdjustMessage() {}

	public HunterStatsAdjustMessage(IHunterStats stats)
	{
		this.point = stats.getPoint();
		this.rank = stats.getRank();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		point = buf.readInt();
		rank = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(point);
		buf.writeInt(rank);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(EntityPlayerSP player)
	{
		IHunterStats stats = HunterStats.get(player, true);

		if (stats != null)
		{
			stats.setPoint(point, false);
			stats.setRank(rank, false);
		}

		return null;
	}
}