package cavern.network.client;

import cavern.api.IHunterStats;
import cavern.stats.HunterStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	@SideOnly(Side.CLIENT)
	public void execute(EntityPlayer player)
	{
		IHunterStats stats = HunterStats.get(player, true);

		if (stats != null)
		{
			stats.setPoint(point, false);
			stats.setRank(rank, false);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(HunterStatsAdjustMessage message, MessageContext ctx)
	{
		EntityPlayer player = FMLClientHandler.instance().getClientPlayerEntity();

		if (player != null)
		{
			message.execute(player);
		}

		return null;
	}
}