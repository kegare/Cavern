package cavern.network.client;

import cavern.client.gui.GuiRegeneration;
import cavern.client.handler.ClientEventHooks;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RegenerationGuiMessage implements IMessage, IMessageHandler<RegenerationGuiMessage, IMessage>
{
	private int type;

	public RegenerationGuiMessage() {}

	public RegenerationGuiMessage(EnumType type)
	{
		this.type = type.ordinal();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		type = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(type);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(RegenerationGuiMessage message, MessageContext ctx)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		EnumType type = EnumType.values()[message.type];
		boolean isOpen = mc.currentScreen != null && mc.currentScreen instanceof GuiRegeneration;

		if (type == EnumType.OPEN)
		{
			if (!isOpen)
			{
				ClientEventHooks.displayGui = new GuiRegeneration();
			}
		}
		else if (isOpen)
		{
			((GuiRegeneration)mc.currentScreen).updateProgress(type);
		}

		return null;
	}

	public enum EnumType
	{
		OPEN,
		START,
		BACKUP,
		REGENERATED,
		FAILED
	}
}