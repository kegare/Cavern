package cavern.network.server;

import cavern.item.ItemMagicalBook;
import cavern.magic.IMagic;
import cavern.stats.MagicianStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MagicExecuteMessage implements IMessage, IMessageHandler<MagicExecuteMessage, IMessage>
{
	private boolean heldMain;

	public MagicExecuteMessage() {}

	public MagicExecuteMessage(EnumHand hand)
	{
		this.heldMain = hand == EnumHand.MAIN_HAND;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		heldMain = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(heldMain);
	}

	@Override
	public IMessage onMessage(MagicExecuteMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().player;
		EnumHand hand =  message.heldMain ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		ItemStack held = player.getHeldItem(hand);

		if (!held.isEmpty() && held.getItem() instanceof ItemMagicalBook)
		{
			IMagic magic = ((ItemMagicalBook)held.getItem()).getMagic(held);

			if (MagicianStats.executeMagic(player, magic))
			{
				player.swingArm(hand);
			}
		}

		return null;
	}
}