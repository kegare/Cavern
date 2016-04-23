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

	public MinerStatsAdjustMessage() {}

	public MinerStatsAdjustMessage(IMinerStats stats)
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
	public IMessage onMessage(MinerStatsAdjustMessage message, MessageContext ctx)
	{
		EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();
		IMinerStats stats = MinerStats.get(player);

		stats.setPoint(message.point);
		stats.setRank(message.rank);

		return null;
	}
}