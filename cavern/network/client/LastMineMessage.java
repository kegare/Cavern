package cavern.network.client;

import cavern.api.IMineBonus;
import cavern.config.GeneralConfig;
import cavern.network.server.MineBonusMessage;
import cavern.stats.MinerStats;
import cavern.util.BlockMeta;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LastMineMessage implements IPlayerMessage<LastMineMessage, IMessage>
{
	private String name;
	private int meta;
	private int point;

	public LastMineMessage() {}

	public LastMineMessage(BlockMeta blockMeta, int point)
	{
		this.name = blockMeta.getBlockName();
		this.meta = blockMeta.getMeta();
		this.point = point;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		name = ByteBufUtils.readUTF8String(buf);
		meta = buf.readByte();
		point = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeByte(meta);
		buf.writeInt(point);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(EntityPlayerSP player)
	{
		MinerStats.setLastMine(new BlockMeta(name, meta), point);

		long time = Minecraft.getSystemTime();

		if (GeneralConfig.miningCombo)
		{
			if (time - MinerStats.lastMineTime <= 15000L)
			{
				++MinerStats.mineCombo;

				int combo = MinerStats.mineCombo;
				boolean flag = false;

				for (IMineBonus bonus : MinerStats.MINE_BONUS)
				{
					if (bonus.canMineBonus(combo, player))
					{
						bonus.onMineBonus(true, combo, player);

						flag = true;
					}
				}

				if (flag)
				{
					return new MineBonusMessage(combo);
				}
			}
			else
			{
				MinerStats.mineCombo = 0;
			}
		}

		MinerStats.lastMineTime = time;

		return null;
	}
}