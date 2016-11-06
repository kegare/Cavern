package cavern.network.server;

import cavern.api.CavernAPI;
import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CavernConfig;
import cavern.config.IceCavernConfig;
import cavern.config.RuinsCavernConfig;
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
	protected boolean iceCavern;
	protected boolean ruinsCavern;

	public RegenerationMessage() {}

	public RegenerationMessage(boolean backup)
	{
		this.backup = backup;
	}

	public RegenerationMessage(boolean backup, boolean cavern, boolean aquaCavern, boolean caveland, boolean iceCavern, boolean ruinsCavern)
	{
		this(backup);
		this.cavern = cavern;
		this.aquaCavern = aquaCavern;
		this.caveland = caveland;
		this.iceCavern = iceCavern;
	}

	public RegenerationMessage setCavern()
	{
		return setCavern(true);
	}

	public RegenerationMessage setCavern(boolean value)
	{
		cavern = value;

		return this;
	}

	public RegenerationMessage setAquaCavern()
	{
		return setAquaCavern(true);
	}

	public RegenerationMessage setAquaCavern(boolean value)
	{
		aquaCavern = value;

		return this;
	}

	public RegenerationMessage setCaveland()
	{
		return setCaveland(true);
	}

	public RegenerationMessage setCaveland(boolean value)
	{
		caveland = value;

		return this;
	}

	public RegenerationMessage setIceCavern()
	{
		return setIceCavern(true);
	}

	public RegenerationMessage setIceCavern(boolean value)
	{
		iceCavern = value;

		return this;
	}

	public RegenerationMessage setRuinsCavern()
	{
		return setRuinsCavern(true);
	}

	public RegenerationMessage setRuinsCavern(boolean value)
	{
		ruinsCavern = value;

		return this;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		backup = buf.readBoolean();
		cavern = buf.readBoolean();
		aquaCavern = buf.readBoolean();
		caveland = buf.readBoolean();
		iceCavern = buf.readBoolean();
		ruinsCavern = buf.readBoolean();
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

		if (message.iceCavern && !CavernAPI.dimension.isIceCavernDisabled())
		{
			DimensionRegeneration.regenerate(IceCavernConfig.dimensionId, message.backup);
		}

		if (message.ruinsCavern && !CavernAPI.dimension.isRuinsCavernDisabled())
		{
			DimensionRegeneration.regenerate(RuinsCavernConfig.dimensionId, message.backup);
		}

		return null;
	}
}