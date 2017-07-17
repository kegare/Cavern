package cavern.network.server;

import cavern.api.IMagicianStats;
import cavern.core.CaveSounds;
import cavern.stats.MagicianStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MagicFlyingMessage implements IMessage, IMessageHandler<MagicFlyingMessage, IMessage>
{
	private boolean isFlying;
	private boolean allowFlying;
	private int point;

	public MagicFlyingMessage() {}

	public MagicFlyingMessage(boolean isFlying, boolean allowFlying, int point)
	{
		this.isFlying = isFlying;
		this.allowFlying = allowFlying;
		this.point = point;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		isFlying = buf.readBoolean();
		allowFlying = buf.readBoolean();
		point = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(isFlying);
		buf.writeBoolean(allowFlying);
		buf.writeInt(point);
	}

	public void execute(EntityPlayerMP player)
	{
		WorldServer world = player.getServerWorld();
		IMagicianStats stats = MagicianStats.get(player);

		player.capabilities.isFlying = isFlying;
		player.capabilities.allowFlying = allowFlying;
		player.fallDistance = 0.0F;

		SoundEvent sound;

		if (isFlying)
		{
			sound = CaveSounds.MAGIC_SUCCESS_MISC;

			if (!player.capabilities.isCreativeMode)
			{
				stats.addMP(-10);
			}
		}
		else
		{
			sound = SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE;
		}

		if (point != 0)
		{
			stats.addPoint(point);
		}

		world.playSound(null, player.posX, player.posY + 0.15D, player.posZ, sound, SoundCategory.PLAYERS, 0.35F, 1.5F);
	}

	@Override
	public IMessage onMessage(MagicFlyingMessage message, MessageContext ctx)
	{
		message.execute(ctx.getServerHandler().player);

		return null;
	}
}