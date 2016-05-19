package cavern.network.client;

import cavern.config.GeneralConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CaveMusicMessage implements IMessage, IMessageHandler<CaveMusicMessage, IMessage>
{
	@SideOnly(Side.CLIENT)
	public static ISound prevMusic;

	private String name;
	private boolean stop;

	public CaveMusicMessage() {}

	public CaveMusicMessage(SoundEvent sound)
	{
		this.name = sound.getRegistryName().toString();
		this.stop = true;
	}

	public CaveMusicMessage(SoundEvent sound, boolean stop)
	{
		this(sound);
		this.stop = stop;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		name = ByteBufUtils.readUTF8String(buf);
		stop = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeBoolean(stop);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IMessage onMessage(CaveMusicMessage message, MessageContext ctx)
	{
		final Minecraft mc = FMLClientHandler.instance().getClient();
		final boolean stop = message.stop;
		final String name = message.name;

		mc.addScheduledTask(new Runnable()
		{
			@Override
			public void run()
			{
				SoundHandler handler = mc.getSoundHandler();

				if (prevMusic != null)
				{
					if (stop)
					{
						handler.stopSound(prevMusic);

						prevMusic = null;
					}
					else if (handler.isSoundPlaying(prevMusic))
					{
						return;
					}
				}

				if (GeneralConfig.caveMusicVolume > 0.0D)
				{
					SoundEvent sound = SoundEvent.REGISTRY.getObject(new ResourceLocation(name));

					if (sound != null)
					{
						PositionedSound music = PositionedSoundRecord.getMasterRecord(sound, 1.0F);

						ObfuscationReflectionHelper.setPrivateValue(PositionedSound.class, music, (float)GeneralConfig.caveMusicVolume, "volume", "field_147662_b");

						handler.playSound(music);

						prevMusic = music;
					}
				}
			}
		});

		return null;
	}
}