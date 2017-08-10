package cavern.network.server;

import cavern.core.CaveSounds;
import cavern.stats.MagicianStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MagicTeleportMessage implements IPlayerMessage<MagicTeleportMessage, IMessage>
{
	private int distance;
	private int movementFactor;

	public MagicTeleportMessage() {}

	public MagicTeleportMessage(int dist, int factor)
	{
		this.distance = dist;
		this.movementFactor = factor;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		distance = buf.readInt();
		movementFactor = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(distance);
		buf.writeInt(movementFactor);
	}

	@Override
	public IMessage process(EntityPlayerMP player)
	{
		WorldServer world = player.getServerWorld();
		EnumFacing front = player.getHorizontalFacing();
		BlockPos origin = player.getPosition();
		BlockPos pos = null;

		while (distance > 0)
		{
			pos = origin.offset(front, distance);

			BlockPos prev = pos;
			int count = 0;

			while (!world.isAirBlock(pos) && ++count <= 3)
			{
				pos = pos.up();
			}

			if (count > 4)
			{
				pos = prev;
			}

			prev = pos;
			count = 0;

			while (world.isAirBlock(pos.down()) && ++count <= 3)
			{
				pos = pos.down();
			}

			if (count > 4)
			{
				pos = prev;
			}

			if (!world.isAirBlock(pos))
			{
				--distance;
			}
			else break;
		}

		if (distance <= 0 || pos == null)
		{
			return null;
		}

		world.playSound(player, player.posX, player.posY, player.posZ, CaveSounds.MAGIC_SUCCESS_MISC, SoundCategory.PLAYERS, 0.5F, 0.85F);

		player.setPositionAndUpdate(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

		world.playSound(null, pos, CaveSounds.MAGIC_SUCCESS_MISC, SoundCategory.PLAYERS, 1.0F, 1.25F);

		if (!player.capabilities.isCreativeMode)
		{
			MagicianStats.get(player).addMP(-(distance / movementFactor * 5));
		}

		return null;
	}
}