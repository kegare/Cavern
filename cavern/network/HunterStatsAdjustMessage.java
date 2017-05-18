package cavern.network;

import cavern.api.IHunterStats;
import cavern.core.Cavern;
import cavern.stats.HunterStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class HunterStatsAdjustMessage implements IMessage, IMessageHandler<HunterStatsAdjustMessage, IMessage>
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

	@Override
	public IMessage onMessage(HunterStatsAdjustMessage message, MessageContext ctx)
	{
		EntityPlayer player;

		if (ctx.side.isClient())
		{
			player = Cavern.proxy.getClientPlayer();
		}
		else
		{
			player = ctx.getServerHandler().playerEntity;
		}

		if (player != null)
		{
			IHunterStats stats = HunterStats.get(player);

			stats.setPoint(message.point);
			stats.setRank(message.rank);
		}

		return null;
	}
}