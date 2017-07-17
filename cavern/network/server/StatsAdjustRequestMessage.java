package cavern.network.server;

import cavern.handler.CaveEventHooks;
import cavern.network.CaveNetworkRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class StatsAdjustRequestMessage implements IMessage, IMessageHandler<StatsAdjustRequestMessage, IMessage>
{
	private static long requestTime;

	public StatsAdjustRequestMessage() {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	@Override
	public IMessage onMessage(StatsAdjustRequestMessage message, MessageContext ctx)
	{
		CaveEventHooks.adjustPlayerStats(ctx.getServerHandler().player);

		return null;
	}

	public static void request()
	{
		long time = System.currentTimeMillis();

		if (requestTime <= 0 || time - requestTime > 5000L)
		{
			requestTime = time;

			CaveNetworkRegistry.sendToServer(new StatsAdjustRequestMessage());
		}
	}
}