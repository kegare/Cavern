package cavern.client.handler;

import java.util.Random;

import cavern.api.IMagicianStats;
import cavern.client.CaveKeyBindings;
import cavern.client.particle.ParticleMagicSpell;
import cavern.item.ItemMagicalBook;
import cavern.magic.IMagic;
import cavern.network.CaveNetworkRegistry;
import cavern.network.server.MagicExecuteMessage;
import cavern.network.server.MagicResultMessage;
import cavern.network.server.MagicShortMessage;
import cavern.stats.MagicianRank;
import cavern.stats.MagicianStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MagicEventHooks
{
	private static final Random RANDOM = new Random();

	private boolean pressFlag;
	private boolean magicSpelled;

	private long spellingStartTime;
	private long spellingTime;
	private int spellingSoundTime;

	private int spellingSlot;

	private EnumHand spellingHand;
	private IMagic spellingMagic;

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

		boolean isSpellingKeyDown = CaveKeyBindings.KEY_MAGIC_SPELLING.isKeyDown();

		if (mc.gameSettings.keyBindUseItem.isKeyDown() || isSpellingKeyDown)
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

						if (hand == null && isSpellingKeyDown)
						{
							for (int i = 0; i < mc.player.inventory.getSizeInventory(); ++i)
							{
								ItemStack stack = mc.player.inventory.getStackInSlot(i);

								if (!stack.isEmpty() && stack.getItem() instanceof ItemMagicalBook)
								{
									mc.playerController.pickItem(i);

									return;
								}
							}
						}

						if (hand != null)
						{
							spellingMagic = ((ItemMagicalBook)held.getItem()).getMagic(mc.player, held);

							if (spellingMagic != null)
							{
								spellingSlot = hand == EnumHand.MAIN_HAND ? mc.player.inventory.currentItem : -1;
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
					if (spellingHand == EnumHand.MAIN_HAND && spellingSlot != mc.player.inventory.currentItem)
					{
						mc.player.inventory.currentItem = spellingSlot;
					}

					if (mc.player.getHeldItem(spellingHand) != spellingBook)
					{
						stopSpelling();
					}
					else
					{
						spelling();
					}
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
		spellingStartTime = Minecraft.getSystemTime();
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

		spellingTime = Minecraft.getSystemTime() - spellingStartTime;

		Minecraft mc = FMLClientHandler.instance().getClient();
		World world = mc.world;
		EntityPlayer player = mc.player;
		IMagicianStats stats = MagicianStats.get(player);
		MagicianRank rank = MagicianRank.get(stats.getRank());
		long magicSpellTime = MathHelper.lfloor((double)spellingMagic.getMagicSpellTime(spellingBook, spellingHand) * rank.getBoost());

		if (spellingMagic.isFinishedSpelling(spellingBook, spellingHand, spellingTime, magicSpellTime))
		{
			finishSpelling();
			stopSpelling();

			return;
		}

		spellingProgress = (double)spellingTime / (double)magicSpellTime;

		if (spellingMagic.shouldCauseSpellingParticles(spellingBook, spellingHand, spellingTime, magicSpellTime, spellingProgress))
		{
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
		}

		spellingMagic.onSpellingTick(spellingBook, spellingHand, spellingTime, magicSpellTime, spellingProgress);

		if (++spellingSoundTime >= spellingMagic.getSpellingSpeed(rank.getSpellingSpeed()))
		{
			spellingSoundTime = 0;

			SoundEvent sound = spellingMagic.getSpellingSound();

			if (sound != null)
			{
				mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, 1.0F));
			}
		}
	}

	private void stopSpelling()
	{
		spellingMagic.onStopSpelling(spellingBook, spellingHand, spellingTime, spellingProgress);

		spellingStartTime = 0L;
		magicSpelled = true;

		spellingBook = null;
		spellingProgress = 0.0D;

		Minecraft mc = FMLClientHandler.instance().getClient();
		SoundEvent sound = spellingMagic.getStopSpellingSound();

		if (sound != null)
		{
			mc.getSoundHandler().playDelayedSound(PositionedSoundRecord.getMasterRecord(sound, 1.0F), 3);
		}
	}

	private void finishSpelling()
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (!mc.player.capabilities.isCreativeMode)
		{
			IMagicianStats stats = MagicianStats.get(mc.player);
			boolean flag = false;

			if (spellingMagic.getMagicLevel() > stats.getRank() + 1)
			{
				mc.ingameGUI.setOverlayMessage(I18n.format("cavern.magic.rank.short"), false);

				flag = true;
			}

			int cost = spellingMagic.getMagicCost(mc.player, mc.world, spellingBook, spellingHand);

			if (cost > 0 && stats.getMP() < cost)
			{
				mc.ingameGUI.setOverlayMessage(I18n.format("cavern.magic.mp.short"), false);

				flag = true;
			}

			if (flag)
			{
				CaveNetworkRegistry.sendToServer(new MagicShortMessage(spellingMagic.getMagicShortDamage()));

				return;
			}
		}

		if (spellingMagic.isClientMagic())
		{
			if (spellingMagic.executeMagic(mc.player, mc.world, spellingBook, spellingHand))
			{
				int cost = spellingMagic.getMagicCost(mc.player, mc.world, spellingBook, spellingHand);
				int point = spellingMagic.getMagicPoint(mc.player, mc.world, spellingBook, spellingHand);

				CaveNetworkRegistry.sendToServer(new MagicResultMessage(cost, point));
			}
			else
			{
				ITextComponent message = spellingMagic.getFailedMessage();

				if (message != null)
				{
					mc.ingameGUI.setOverlayMessage(message, false);
				}
			}
		}
		else
		{
			CaveNetworkRegistry.sendToServer(new MagicExecuteMessage(spellingHand));
		}
	}
}