package cavern.network.server;

import cavern.api.IMinerStats;
import cavern.config.MiningAssistConfig;
import cavern.miningassist.MiningAssist;
import cavern.stats.MinerRank;
import cavern.stats.MinerStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MiningAssistMessage implements IMessage, IMessageHandler<MiningAssistMessage, IMessage>
{
	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	@Override
	public IMessage onMessage(MiningAssistMessage message, MessageContext ctx)
	{
		EntityPlayer player = ctx.getServerHandler().playerEntity;
		IMinerStats stats = MinerStats.get(player);

		if (stats.getRank() < MiningAssistConfig.minerRank.getValue())
		{
			ITextComponent component = new TextComponentTranslation(MinerRank.get(MiningAssistConfig.minerRank.getValue()).getUnlocalizedName());
			component.getStyle().setItalic(Boolean.valueOf(true));
			component = new TextComponentTranslation("cavern.miningassist.toggle.failed.message", component);
			component.getStyle().setColor(TextFormatting.RED);

			player.sendMessage(component);
		}
		else
		{
			stats.toggleMiningAssist();
			stats.adjustData();

			ITextComponent component = new TextComponentTranslation(MiningAssist.get(stats.getMiningAssist()).getUnlocalizedName());
			component.getStyle().setColor(TextFormatting.GRAY).setItalic(Boolean.valueOf(true));
			component = new TextComponentTranslation("cavern.miningassist.toggle.message", component);

			player.sendMessage(component);
		}

		return null;
	}
}