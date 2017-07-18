package cavern.network.client;

import cavern.api.IMagicianStats;
import cavern.stats.MagicianStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicianStatsAdjustMessage implements IPlayerMessage<MagicianStatsAdjustMessage, IMessage>
{
	private int point;
	private int rank;
	private int mp;

	public MagicianStatsAdjustMessage() {}

	public MagicianStatsAdjustMessage(IMagicianStats stats)
	{
		this.point = stats.getPoint();
		this.rank = stats.getRank();
		this.mp = stats.getMP();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		point = buf.readInt();
		rank = buf.readInt();
		mp = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(point);
		buf.writeInt(rank);
		buf.writeInt(mp);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(EntityPlayerSP player)
	{
		IMagicianStats stats = MagicianStats.get(player, true);

		if (stats != null)
		{
			stats.setPoint(point, false);
			stats.setRank(rank, false);
			stats.setMP(mp, false);
		}

		return null;
	}
}