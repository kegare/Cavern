package cavern.network;

import cavern.core.Cavern;
import cavern.network.client.CaveMusicMessage;
import cavern.network.client.LastMineMessage;
import cavern.network.client.RegenerationGuiMessage;
import cavern.network.server.MineBonusMessage;
import cavern.network.server.MiningAssistMessage;
import cavern.network.server.RegenerationMessage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CaveNetworkRegistry
{
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Cavern.MODID);

	public static int messageId;

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side)
	{
		NETWORK.registerMessage(messageHandler, requestMessageType, messageId++, side);
	}

	public static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType)
	{
		registerMessage(messageHandler, requestMessageType, Side.CLIENT);
		registerMessage(messageHandler, requestMessageType, Side.SERVER);
	}

	public static void sendToAll(IMessage message)
	{
		NETWORK.sendToAll(message);
	}

	public static void sendTo(IMessage message, EntityPlayerMP player)
	{
		NETWORK.sendTo(message, player);
	}

	public static void sendToDimension(IMessage message, int dimensionId)
	{
		NETWORK.sendToDimension(message, dimensionId);
	}

	public static void sendToServer(IMessage message)
	{
		NETWORK.sendToServer(message);
	}

	public static void registerMessages()
	{
		registerMessage(MinerStatsAdjustMessage.class, MinerStatsAdjustMessage.class);
		registerMessage(HunterStatsAdjustMessage.class, HunterStatsAdjustMessage.class);
		registerMessage(LastMineMessage.class, LastMineMessage.class, Side.CLIENT);
		registerMessage(MineBonusMessage.class, MineBonusMessage.class, Side.SERVER);
		registerMessage(CaveMusicMessage.class, CaveMusicMessage.class, Side.CLIENT);
		registerMessage(RegenerationGuiMessage.class, RegenerationGuiMessage.class, Side.CLIENT);
		registerMessage(RegenerationMessage.class, RegenerationMessage.class, Side.SERVER);
		registerMessage(MiningAssistMessage.class, MiningAssistMessage.class, Side.SERVER);
	}
}