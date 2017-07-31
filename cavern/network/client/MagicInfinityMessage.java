package cavern.network.client;

import cavern.core.CaveSounds;
import cavern.stats.MagicianStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicInfinityMessage implements IPlayerMessage<MagicInfinityMessage, IMessage>
{
	private int level;

	public MagicInfinityMessage() {}

	public MagicInfinityMessage(int level)
	{
		this.level = level;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		level = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(level);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage process(EntityPlayerSP player)
	{
		SoundEvent sound;

		if (level > 0)
		{
			sound = CaveSounds.MAGIC_INFINITY;
		}
		else
		{
			sound = CaveSounds.MAGIC_SUCCESS_MISC;
		}

		FMLClientHandler.instance().getClient().getSoundHandler().playDelayedSound(PositionedSoundRecord.getMasterRecord(sound, 1.0F), 20);

		MagicianStats.get(player).setInfinity(level, level > 0 ? Integer.MAX_VALUE : 0);

		return null;
	}
}