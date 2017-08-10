package cavern.network.server;

import cavern.api.IMagicianStats;
import cavern.core.CaveSounds;
import cavern.stats.MagicianStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MagicInvisibleMessage implements IPlayerMessage<MagicInvisibleMessage, IMessage>
{
	private boolean invisible;
	private int cost;

	public MagicInvisibleMessage() {}

	public MagicInvisibleMessage(boolean invisible, int cost)
	{
		this.invisible = invisible;
		this.cost = cost;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		invisible = buf.readBoolean();
		cost = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(invisible);
		buf.writeInt(cost);
	}

	@Override
	public IMessage process(EntityPlayerMP player)
	{
		IMagicianStats stats = MagicianStats.get(player);
		WorldServer world = player.getServerWorld();

		player.setInvisible(invisible);
		stats.setInvisible(invisible);

		if (!player.capabilities.isCreativeMode)
		{
			stats.addMP(-cost);
		}

		world.playSound(null, player.posX, player.posY + 0.65D, player.posZ, CaveSounds.MAGIC_SUCCESS_MISC, SoundCategory.PLAYERS, 0.25F, 0.75F);

		return null;
	}
}