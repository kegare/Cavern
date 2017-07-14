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

	@Override
	public IMessage onMessage(RegenerationMessage message, MessageContext ctx)
	{
		if (message.cavern)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_CAVERN, message.backup);
		}

		if (message.aquaCavern)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_AQUA_CAVERN, message.backup);
		}

		if (message.caveland)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_CAVELAND, message.backup);
		}

		if (message.iceCavern)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_ICE_CAVERN, message.backup);
		}

		if (message.ruinsCavern)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_RUINS_CAVERN, message.backup);
		}

		if (message.cavenia)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_CAVENIA, message.backup);
		}

		if (message.hugeCavern)
		{
			DimensionRegeneration.regenerate(CaveType.DIM_HUGE_CAVERN, message.backup);
		}

		return null;
	}
}