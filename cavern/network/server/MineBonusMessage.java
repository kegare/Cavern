package cavern.network.server;

import cavern.api.IMineBonus;
import cavern.config.GeneralConfig;
import cavern.stats.MinerStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MineBonusMessage implements IMessage, IMessageHandler<MineBonusMessage, IMessage>
{
	private int combo;

	public MineBonusMessage() {}

	public MineBonusMessage(int combo)
	{
		this.combo = combo;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		combo = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(combo);
	}

	@Override
	public IMessage onMessage(MineBonusMessage message, MessageContext ctx)
	{
		if (GeneralConfig.miningCombo)
		{
			EntityPlayerMP player = ctx.getServerHandler().player;
			int combo = message.combo;

			for (IMineBonus bonus : MinerStats.MINE_BONUS)
			{
				if (bonus.canMineBonus(combo, player))
				{
					bonus.onMineBonus(false, combo, player);
				}
			}
		}

		return null;
	}
}