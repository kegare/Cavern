package cavern.network.client;

import cavern.client.gui.toasts.DelayedToast;
import cavern.client.gui.toasts.MiningAssistToast;
import cavern.client.gui.toasts.RuinsMissionToast;
import cavern.client.handler.ClientEventHooks;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ToastMessage implements IMessage, IMessageHandler<ToastMessage, IMessage>
{
	private String key;

	public ToastMessage() {}

	public ToastMessage(String key)
	{
		this.key = key;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		key = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, key);
	}

	@SideOnly(Side.CLIENT)
	public void execute()
	{
		switch (key)
		{
			case "mining_assist":
				ClientEventHooks.DELAYED_TOAST.add(new DelayedToast(new MiningAssistToast(), 10000L));
				break;
			case "ruins_mission":
				ClientEventHooks.DELAYED_TOAST.add(new DelayedToast(new RuinsMissionToast(), 10000L));
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(ToastMessage message, MessageContext ctx)
	{
		message.execute();

		return null;
	}
}