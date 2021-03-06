package cavern.network.server;

import cavern.api.IMagicianStats;
import cavern.item.ItemMagicalBook;
import cavern.magic.IMagic;
import cavern.stats.MagicianStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MagicExecuteMessage implements IPlayerMessage<MagicExecuteMessage, IMessage>
{
	private boolean heldMain;

	public MagicExecuteMessage() {}

	public MagicExecuteMessage(EnumHand hand)
	{
		this.heldMain = hand == EnumHand.MAIN_HAND;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		heldMain = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(heldMain);
	}

	@Override
	public IMessage process(EntityPlayerMP player)
	{
		EnumHand hand =  heldMain ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		ItemStack stack = player.getHeldItem(hand);

		if (!stack.isEmpty() && stack.getItem() instanceof ItemMagicalBook)
		{
			IMagic magic = ((ItemMagicalBook)stack.getItem()).getMagic(player, stack);
			IMagicianStats stats = MagicianStats.get(player);
			WorldServer world = player.getServerWorld();
			int infinity = stats.getInfinity();

			if (infinity > 0 && magic.getMagicLevel() > infinity)
			{
				return null;
			}

			if (magic.executeMagic(player, world, stack, hand))
			{
				if (!player.capabilities.isCreativeMode)
				{
					stats.addMP(-magic.getMagicCost(player, world, stack, hand));
				}

				stats.addPoint(magic.getMagicPoint(player, world, stack, hand));

				SoundEvent sound = magic.getMagicSound();

				if (sound != null)
				{
					world.playSound(null, player.posX, player.posY, player.posZ, sound, SoundCategory.PLAYERS, 0.3F, 1.0F);
				}

				player.swingArm(hand);
			}
			else
			{
				ITextComponent msg = magic.getFailedMessage();

				if (msg != null)
				{
					player.sendStatusMessage(msg, true);
				}
			}
		}

		return null;
	}
}