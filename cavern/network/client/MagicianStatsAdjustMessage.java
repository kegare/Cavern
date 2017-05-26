package cavern.network.client;

import cavern.api.IMagicianStats;
import cavern.stats.MagicianStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicianStatsAdjustMessage implements IMessage, IMessageHandler<MagicianStatsAdjustMessage, IMessage>
{
	private int point;
	private int rank;
	private int mp;

	public MagicianStatsAdjustMessage() {}

	public MagicianStatsAdjustMessage(IMagicianStats stats)
	{
		this.point = stats.getPoint();
		this.rank = stats.getRank();
		this.mp = stats.getMP();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		point = buf.readInt();
		rank = buf.readInt();
		mp = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(point);
		buf.writeInt(rank);
		buf.writeInt(mp);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(MagicianStatsAdjustMessage message, MessageContext ctx)
	{
		EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();

		if (player != null)
		{
			IMagicianStats stats = MagicianStats.get(player, true);

			if (stats != null)
			{
				stats.setPoint(message.point, false);
				stats.setRank(message.rank, false);
				stats.setMP(message.mp, false);
			}
		}

		return null;
	}
}