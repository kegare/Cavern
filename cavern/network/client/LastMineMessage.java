package cavern.network.client;

import cavern.stats.MinerStats;
import cavern.util.BlockMeta;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LastMineMessage implements IMessage, IMessageHandler<LastMineMessage, IMessage>
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
	public IMessage onMessage(LastMineMessage message, MessageContext ctx)
	{
		MinerStats.lastMine = new BlockMeta(message.name, message.meta);
		MinerStats.lastMinePoint = message.point;
		MinerStats.lastMineDisplayTime = Minecraft.getSystemTime();

		return null;
	}
}