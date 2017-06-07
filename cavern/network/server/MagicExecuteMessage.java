package cavern.network.server;

import cavern.api.IMagicianStats;
import cavern.core.CaveDamageSources;
import cavern.item.ItemMagicalBook;
import cavern.magic.IMagic;
import cavern.magic.IMagic.IEntityMagic;
import cavern.magic.IMagic.IPlainMagic;
import cavern.magic.IMagic.IPlayerMagic;
import cavern.stats.MagicianStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MagicExecuteMessage implements IMessage, IMessageHandler<MagicExecuteMessage, IMessage>
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
	public IMessage onMessage(MagicExecuteMessage message, MessageContext ctx)
	{
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		EnumHand hand =  message.heldMain ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND;
		ItemStack held = player.getHeldItem(hand);

		if (!held.isEmpty() && held.getItem() instanceof ItemMagicalBook)
		{
			IMagic magic = ((ItemMagicalBook)held.getItem()).getMagic(held);

			message.executeMagic(player, hand, held, magic);
		}

		return null;
	}

	public void executeMagic(EntityPlayerMP player, EnumHand hand, ItemStack held, IMagic magic)
	{
		IMagicianStats stats = MagicianStats.get(player);
		int rank = stats.getRank();

		if (!player.capabilities.isCreativeMode && magic.getMagicLevel() > rank + 1)
		{
			player.sendStatusMessage(new TextComponentTranslation("cavern.magicianstats.rank.short"), true);

			return;
		}

		int cost = magic.getCostMP(player);
		int mp = stats.getMP();

		if (!player.capabilities.isCreativeMode && cost > 0 && mp < cost)
		{
			player.sendStatusMessage(new TextComponentTranslation("cavern.magicianstats.mp.short"), true);
			player.attackEntityFrom(CaveDamageSources.EXHAUST_MP, MathHelper.clamp(3 * magic.getMagicLevel(), 1, 10));

			return;
		}

		WorldServer world = player.getServerWorld();
		double range = magic.getMagicRange(player);
		boolean success = false;

		if (magic instanceof IEntityMagic)
		{
			IEntityMagic entityMagic = (IEntityMagic)magic;
			int count = 0;

			for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().expandXyz(range)))
			{
				if (entity != null && player.canEntityBeSeen(entity) && entityMagic.isTarget(player, entity) && entityMagic.execute(player, entity))
				{
					++count;
				}
			}

			if (count > 0)
			{
				success = true;
			}
		}

		if (magic instanceof IPlayerMagic)
		{
			IPlayerMagic playerMagic = (IPlayerMagic)magic;
			int count = 0;

			for (EntityPlayer targetPlayer : world.getEntitiesWithinAABB(EntityPlayer.class, player.getEntityBoundingBox().expandXyz(range)))
			{
				if (targetPlayer != null && player.canEntityBeSeen(targetPlayer) && playerMagic.isTarget(player, targetPlayer) && playerMagic.execute(player, targetPlayer))
				{
					++count;
				}
			}

			if (count > 0)
			{
				success = true;
			}
		}

		if (magic instanceof IPlainMagic)
		{
			IPlainMagic plainMagic = (IPlainMagic)magic;

			success = plainMagic.execute(player);
		}

		if (success)
		{
			if (!player.capabilities.isCreativeMode)
			{
				stats.addMP(-cost);
			}

			stats.addPoint(magic.getMagicPoint(player));

			SoundEvent sound = magic.getMagicSound();

			if (sound != null)
			{
				world.playSound(null, player.posX, player.posY, player.posZ, sound, SoundCategory.PLAYERS, 0.35F, 1.0F);
			}
		}
		else
		{
			ITextComponent message = magic.getFailedMessage();

			if (message != null)
			{
				player.sendStatusMessage(message, true);
			}
		}
	}
}