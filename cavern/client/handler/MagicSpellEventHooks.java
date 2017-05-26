package cavern.client.handler;

import java.util.Random;

import cavern.api.IMagicianStats;
import cavern.client.particle.ParticleMagicSpell;
import cavern.core.CaveSounds;
import cavern.item.ItemMagicalBook;
import cavern.magic.IMagic;
import cavern.network.CaveNetworkRegistry;
import cavern.network.server.MagicExecuteMessage;
import cavern.stats.MagicianRank;
import cavern.stats.MagicianStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MagicSpellEventHooks
{
	private static final Random RANDOM = new Random();

	private boolean pressFlag;
	private long spellTime;
	private long spellingTime;
	private int spellingSoundTime;
	private EnumHand spellingHand;
	private IMagic spellingMagic;
	private boolean magicSpelled;

	public static ItemStack spellingBook;
	public static double spellingProgress;

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)
	{
		if (event.phase != Phase.END)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.world == null || mc.player == null || mc.currentScreen != null)
		{
			return;
		}

		if (mc.gameSettings.keyBindUseItem.isKeyDown())
		{
			if (!magicSpelled)
			{
				if (!pressFlag)
				{
					if (spellingBook == null)
					{
						ItemStack held = mc.player.getHeldItemMainhand();
						EnumHand hand = null;

						if (!held.isEmpty() && held.getItem() instanceof ItemMagicalBook)
						{
							hand = EnumHand.MAIN_HAND;
						}
						else
						{
							held = mc.player.getHeldItemOffhand();

							if (!held.isEmpty() && held.getItem() instanceof ItemMagicalBook)
							{
								hand = EnumHand.OFF_HAND;
							}
						}

						if (hand != null)
						{
							spellingMagic = ((ItemMagicalBook)held.getItem()).getMagic(held);

							if (spellingMagic != null)
							{
								spellingHand = hand;

								spellingBook = held;
							}
						}
					}

					if (spellingBook != null)
					{
						pressFlag = true;

						startSpelling();
					}
				}
				else
				{
					spelling();
				}
			}
		}
		else if (pressFlag)
		{
			pressFlag = false;

			if (!magicSpelled)
			{
				stopSpelling();
			}

			magicSpelled = false;
		}
	}

	private void startSpelling()
	{
		spellTime = Minecraft.getSystemTime();
		spellingTime = 0L;
		spellingSoundTime = 0;
	}

	private void spelling()
	{
		if (spellingMagic == null || spellingBook == null || spellingHand == null)
		{
			stopSpelling();

			return;
		}

		spellingTime = Minecraft.getSystemTime() - spellTime;

		Minecraft mc = FMLClientHandler.instance().getClient();
		World world = mc.world;
		EntityPlayer player = mc.player;
		IMagicianStats stats = MagicianStats.get(player);
		MagicianRank rank = MagicianRank.get(stats.getRank());
		long time = MathHelper.lfloor((double)spellingMagic.getMagicSpellTime(player) * rank.getBoost());

		if (spellingTime >= time)
		{
			finishSpelling();
			stopSpelling();

			return;
		}

		spellingProgress = (double)spellingTime / (double)time;

		for (int i = 0; i < 2; ++i)
		{
			int var1 = RANDOM.nextInt(2) * 2 - 1;
			int var2 = RANDOM.nextInt(2) * 2 - 1;
			double ptX = player.posX + 0.25D * var1;
			double ptY = player.posY + 0.7D + RANDOM.nextFloat();
			double ptZ = player.posZ + 0.25D * var2;
			double motionX = RANDOM.nextFloat() * 1.0F * var1;
			double motionY = (RANDOM.nextFloat() - 0.25D) * 0.125D;
			double motionZ = RANDOM.nextFloat() * 1.0F * var2;
			ParticleMagicSpell particle = new ParticleMagicSpell(world, ptX, ptY, ptZ, motionX, motionY, motionZ);

			mc.effectRenderer.addEffect(particle);
		}

		if (++spellingSoundTime >= rank.getSpellSpeed())
		{
			spellingSoundTime = 0;

			mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(CaveSounds.SPELLING, 1.0F));
		}
	}

	private void stopSpelling()
	{
		spellTime = 0L;
		magicSpelled = true;

		spellingBook = null;
		spellingProgress = 0.0D;

		Minecraft mc = FMLClientHandler.instance().getClient();

		mc.getSoundHandler().playDelayedSound(PositionedSoundRecord.getMasterRecord(CaveSounds.SPELLING_END, 1.0F), 3);
	}

	private void finishSpelling()
	{
		CaveNetworkRegistry.sendToServer(new MagicExecuteMessage(spellingHand));
	}
}