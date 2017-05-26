package cavern.network.client;

import cavern.api.IMinerStats;
import cavern.stats.MinerStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(MinerStatsAdjustMessage message, MessageContext ctx)
	{
		EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();

		if (player != null)
		{
			IMinerStats stats = MinerStats.get(player, true);

			if (stats != null)
			{
				stats.setPoint(message.point, false);
				stats.setRank(message.rank, false);
				stats.setMiningAssist(message.miningAssist, false);
			}
		}

		return null;
	}
}