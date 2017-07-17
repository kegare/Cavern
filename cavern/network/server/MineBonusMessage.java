package cavern.network.server;

import cavern.api.IMineBonus;
import cavern.config.GeneralConfig;
import cavern.stats.MinerStats;
import cavern.util.CaveUtils;
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

	public void execute(EntityPlayerMP player)
	{
		if (GeneralConfig.miningCombo)
		{
			for (IMineBonus bonus : MinerStats.MINE_BONUS)
			{
				if (bonus.canMineBonus(combo, player))
				{
					bonus.onMineBonus(false, combo, player);
				}
			}

			if (combo >= 50)
			{
				CaveUtils.grantAdvancement(player, "good_mine");
			}
		}
	}

	@Override
	public IMessage onMessage(MineBonusMessage message, MessageContext ctx)
	{
		message.execute(ctx.getServerHandler().player);

		return null;
	}
}