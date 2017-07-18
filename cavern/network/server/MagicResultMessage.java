package cavern.network.server;

import cavern.api.IMagicianStats;
import cavern.stats.MagicianStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MagicResultMessage implements IPlayerMessage<MagicResultMessage, IMessage>
{
	private int cost;
	private int point;

	public MagicResultMessage() {}

	public MagicResultMessage(int cost, int point)
	{
		this.cost = cost;
		this.point = point;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		cost = buf.readInt();
		point = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(cost);
		buf.writeInt(point);
	}

	@Override
	public IMessage process(EntityPlayerMP player)
	{
		IMagicianStats stats = MagicianStats.get(player);

		if (!player.capabilities.isCreativeMode)
		{
			stats.addMP(-cost);
		}

		stats.addPoint(point);

		return null;
	}
}