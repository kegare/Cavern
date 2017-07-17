package cavern.network.server;

import cavern.util.DimensionRegeneration;
import cavern.world.CaveType;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RegenerationMessage implements IMessage, IMessageHandler<RegenerationMessage, IMessage>
{
	public boolean backup, cavern, aquaCavern, caveland, iceCavern, ruinsCavern, cavenia, hugeCavern;

	@Override
	public void fromBytes(ByteBuf buf)
	{
		backup = buf.readBoolean();
		cavern = buf.readBoolean();
		aquaCavern = buf.readBoolean();
		caveland = buf.readBoolean();
		iceCavern = buf.readBoolean();
		ruinsCavern = buf.readBoolean();
		cavenia = buf.readBoolean();
		hugeCavern = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(backup);
		buf.writeBoolean(cavern);
		buf.writeBoolean(aquaCavern);
		buf.writeBoolean(caveland);
		buf.writeBoolean(iceCavern);
		buf.writeBoolean(ruinsCavern);
		buf.writeBoolean(cavenia);
		buf.writeBoolean(hugeCavern);
	}

	public void execute()
	{
		if (cavern)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_CAVERN, backup);
		}

		if (aquaCavern)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_AQUA_CAVERN, backup);
		}

		if (caveland)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_CAVELAND, backup);
		}

		if (iceCavern)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_ICE_CAVERN, backup);
		}

		if (ruinsCavern)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_RUINS_CAVERN, backup);
		}

		if (cavenia)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_CAVENIA, backup);
		}

		if (hugeCavern)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_HUGE_CAVERN, backup);
		}
	}

	@Override
	public IMessage onMessage(RegenerationMessage message, MessageContext ctx)
	{
		message.execute();

		return null;
	}
}