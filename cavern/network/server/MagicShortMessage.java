package cavern.network.server;

import cavern.core.CaveDamageSources;
import cavern.util.CaveUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MagicShortMessage implements IMessage, IMessageHandler<MagicShortMessage, IMessage>
{
	private float damage;

	public MagicShortMessage() {}

	public MagicShortMessage(float damage)
	{
		this.damage = damage;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		damage = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeFloat(damage);
	}

	public void execute(EntityPlayerMP player)
	{
		player.attackEntityFrom(CaveDamageSources.EXHAUST_MP, damage);

		CaveUtils.grantAdvancement(player, "short_mp");
	}

	@Override
	public IMessage onMessage(MagicShortMessage message, MessageContext ctx)
	{
		message.execute(ctx.getServerHandler().player);

		return null;
	}
}