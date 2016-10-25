package cavern.network.server;

import cavern.api.CavernAPI;
import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CavernConfig;
import cavern.util.DimensionRegeneration;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RegenerationMessage implements IMessage, IMessageHandler<RegenerationMessage, IMessage>
{
	private boolean backup;
	protected boolean cavern;
	protected boolean aquaCavern;
	protected boolean caveland;

	public RegenerationMessage() {}

	public RegenerationMessage(boolean backup, boolean cavern, boolean aquaCavern, boolean caveland)
	{
		this.backup = backup;
		this.cavern = cavern;
		this.aquaCavern = aquaCavern;
		this.caveland = caveland;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		backup = buf.readBoolean();
		cavern = buf.readBoolean();
		aquaCavern = buf.readBoolean();
		caveland = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(backup);
		buf.writeBoolean(cavern);
		buf.writeBoolean(aquaCavern);
		buf.writeBoolean(caveland);
	}

	@Override
	public IMessage onMessage(RegenerationMessage message, MessageContext ctx)
	{
		if (message.cavern)
		{
			DimensionRegeneration.regenerate(CavernConfig.dimensionId, message.backup);
		}

		if (message.aquaCavern && !CavernAPI.dimension.isAquaCavernDisabled())
		{
			DimensionRegeneration.regenerate(AquaCavernConfig.dimensionId, message.backup);
		}

		if (message.caveland && !CavernAPI.dimension.isCavelandDisabled())
		{
			DimensionRegeneration.regenerate(CavelandConfig.dimensionId, message.backup);
		}

		return null;
	}
}