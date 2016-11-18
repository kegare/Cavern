package cavern.network;

import cavern.api.IMinerStats;
import cavern.core.Cavern;
import cavern.stats.MinerStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MinerStatsAdjustMessage implements IMessage, IMessageHandler<MinerStatsAdjustMessage, IMessage>
{
	private int point;
	private int rank;
	private int miningAssist;

	public MinerStatsAdjustMessage() {}

	public MinerStatsAdjustMessage(IMinerStats stats)
	{
		this.point = stats.getPoint();
		this.rank = stats.getRank();
		this.miningAssist = stats.getMiningAssist();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		point = buf.readInt();
		rank = buf.readInt();
		miningAssist = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(point);
		buf.writeInt(rank);
		buf.writeInt(miningAssist);
	}

	@Override
	public IMessage onMessage(MinerStatsAdjustMessage message, MessageContext ctx)
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
			IMinerStats stats = MinerStats.get(player);

			stats.setPoint(message.point);
			stats.setRank(message.rank);
			stats.setMiningAssist(message.miningAssist);
		}

		return null;
	}
}