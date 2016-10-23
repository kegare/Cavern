package cavern.handler;

import com.google.common.base.Strings;

import cavern.api.CavernAPI;
import cavern.api.IMinerStats;
import cavern.client.ClientProxy;
import cavern.client.gui.GuiDownloadCaveTerrain;
import cavern.client.gui.GuiLoadCaveTerrain;
import cavern.config.AquaCavernConfig;
import cavern.config.CavelandConfig;
import cavern.config.CavernConfig;
import cavern.config.GeneralConfig;
import cavern.config.property.ConfigDisplayPos;
import cavern.core.Cavern;
import cavern.stats.MinerRank;
import cavern.stats.MinerStats;
import cavern.util.Version;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientEventHooks
{
	@SideOnly(Side.CLIENT)
	public static GuiScreen displayGui;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTick(ClientTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END)
		{
			return;
		}

		if (displayGui != null)
		{
			FMLClientHandler.instance().showGuiScreen(displayGui);

			displayGui = null;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		String mod = event.getModID();
		String type = event.getConfigID();

		if (mod.equals(Cavern.MODID))
		{
			if (type == null)
			{
				GeneralConfig.syncConfig();
				CavernConfig.syncConfig();
				AquaCavernConfig.syncConfig();
				CavelandConfig.syncConfig();
			}
			else switch (type)
			{
				case Configuration.CATEGORY_GENERAL:
					GeneralConfig.syncConfig();

					if (event.isWorldRunning())
					{
						GeneralConfig.refreshMiningPointItems();
						GeneralConfig.refreshMiningPoints();
					}

					break;
				case "dimension.cavern":
					CavernConfig.syncConfig();

					if (event.isWorldRunning())
					{
						CavernConfig.refreshDungeonMobs();
					}

					break;
				case "dimension.aquaCavern":
					AquaCavernConfig.syncConfig();

					if (event.isWorldRunning())
					{
						AquaCavernConfig.refreshDungeonMobs();
					}

					break;
				case "dimension.caveland":
					CavelandConfig.syncConfig();
					break;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderGameTextOverlay(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.thePlayer;

		if (mc.gameSettings.showDebugInfo)
		{
			if (CavernAPI.dimension.isEntityInCavern(player))
			{
				event.getLeft().add("Dim: Cavern");
			}
			else if (CavernAPI.dimension.isEntityInAquaCavern(player))
			{
				event.getLeft().add("Dim: Aqua Cavern");
			}
			else if (CavernAPI.dimension.isEntityInCaveland(player))
			{
				event.getLeft().add("Dim: Caveland");
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderGamePostOverlay(RenderGameOverlayEvent.Post event)
	{
		if (event.getType() != ElementType.HOTBAR)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.thePlayer;
		ScaledResolution resolution = event.getResolution();

		if (CavernAPI.dimension.isEntityInCaves(player) && (mc.currentScreen == null || GuiChat.class.isInstance(mc.currentScreen)) &&
			(GeneralConfig.alwaysShowMinerStatus || mc.thePlayer.capabilities.isCreativeMode || mc.gameSettings.advancedItemTooltips ||
			GeneralConfig.isMiningPointItem(player.getHeldItemMainhand()) || GeneralConfig.isMiningPointItem(player.getHeldItemOffhand())))
		{
			ConfigDisplayPos.Type type = GeneralConfig.miningPointPosition.getType();

			if (type.isHidden())
			{
				return;
			}

			IMinerStats stats = MinerStats.get(player);
			MinerRank minerRank = MinerRank.getRank(stats.getRank());
			String point = Integer.toString(stats.getPoint());
			String rank = I18n.format(minerRank.getUnlocalizedName());
			int x, y;

			switch (type)
			{
				case TOP_RIGHT:
					x = resolution.getScaledWidth() - 20;
					y = 5;
					break;
				case TOP_LEFT:
					x = 5;
					y = 5;
					break;
				case BOTTOM_RIGHT:
					x = resolution.getScaledWidth() - 20;
					y = resolution.getScaledHeight() - 21;
					break;
				case BOTTOM_LEFT:
					x = 5;
					y = resolution.getScaledHeight() - 21;
					break;
				default:
					return;
			}

			RenderItem renderItem = mc.getRenderItem();
			FontRenderer renderer = mc.fontRendererObj;
			int originX = x;
			int originY = y;
			boolean flag = false;
			long timeDiff = Minecraft.getSystemTime() - MinerStats.lastMineTime;

			if (MinerStats.lastMineTime > 0 && timeDiff < 2000L && MinerStats.lastMine != null && MinerStats.lastMinePoint != 0)
			{
				Block block = MinerStats.lastMine.getBlock();

				if (ClientProxy.renderBlockMap.containsKey(block))
				{
					block = ClientProxy.renderBlockMap.get(block);
				}

				ItemStack item = new ItemStack(block, 1, MinerStats.lastMine.getMeta());

				if (item != null && item.getItem() != null)
				{
					RenderHelper.enableGUIStandardItemLighting();
					renderItem.renderItemIntoGUI(item, x, y);
					renderItem.renderItemOverlayIntoGUI(renderer, item, x, y, Integer.toString(MinerStats.lastMinePoint));
					RenderHelper.disableStandardItemLighting();

					flag = true;
				}
			}

			if (flag)
			{
				x += type.isLeft() ? 20 : -20;
			}

			renderItem.renderItemIntoGUI(minerRank.getRenderItemStack(), x, y);

			GlStateManager.pushMatrix();
			GlStateManager.disableDepth();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

			if (point.length() <= 1)
			{
				point = " " + point;
			}

			String combo = null;

			if (timeDiff > 15000L)
			{
				MinerStats.mineCombo = 0;
			}
			else if (MinerStats.mineCombo > 0)
			{
				TextFormatting format = TextFormatting.WHITE;

				if (timeDiff < 3000L)
				{
					format = TextFormatting.BOLD;
				}
				else if (timeDiff > 12000L)
				{
					format = TextFormatting.GRAY;
				}

				combo = format + String.format("%d COMBO!", MinerStats.mineCombo) + TextFormatting.RESET;
			}

			if (type.isLeft())
			{
				renderer.drawStringWithShadow(point, x + 5, y + 9, 0xCECECE);

				if (GeneralConfig.showMinerRank)
				{
					x = originX;
					y = originY;

					if (type.isTop())
					{
						if (combo != null)
						{
							renderer.drawStringWithShadow(combo, x + 5, y + 29, 0xFFFFFF);
						}

						renderer.drawStringWithShadow(rank, x + 5, y + 19, 0xCECECE);
					}
					else
					{
						if (combo != null)
						{
							renderer.drawStringWithShadow(combo, x + 5, y - 24, 0xFFFFFF);
						}

						renderer.drawStringWithShadow(rank, x + 5, y - 12, 0xCECECE);
					}
				}
			}
			else
			{
				renderer.drawStringWithShadow(point, x + 17 - renderer.getStringWidth(point), y + 9, 0xCECECE);

				if (GeneralConfig.showMinerRank)
				{
					x = originX;
					y = originY;

					if (type.isTop())
					{
						if (combo != null)
						{
							renderer.drawStringWithShadow(combo, x + 17 - renderer.getStringWidth(combo), y + 29, 0xFFFFFF);
						}

						renderer.drawStringWithShadow(rank, x + 17 - renderer.getStringWidth(rank), y + 19, 0xCECECE);
					}
					else
					{
						if (combo != null)
						{
							renderer.drawStringWithShadow(combo, x + 17 - renderer.getStringWidth(combo), y - 24, 0xFFFFFF);
						}

						renderer.drawStringWithShadow(rank, x + 17 - renderer.getStringWidth(rank), y - 12, 0xCECECE);
					}
				}
			}

			GlStateManager.enableDepth();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		GuiScreen gui = event.getGui();

		if (gui != null && GuiModList.class == gui.getClass())
		{
			String desc = I18n.format("cavern.description");

			if (!Strings.isNullOrEmpty(desc))
			{
				Cavern.metadata.description = desc;
			}
		}
		else if (CavernAPI.dimension.isEntityInCaves(mc.thePlayer) && (mc.currentScreen == null || !(mc.currentScreen instanceof GuiWorldSelection)))
		{
			if (gui == null)
			{
				if (mc.currentScreen != null && GuiDownloadCaveTerrain.class == mc.currentScreen.getClass())
				{
					event.setGui(new GuiLoadCaveTerrain(mc.getConnection()));
				}
			}
			else if (GuiDownloadTerrain.class == gui.getClass())
			{
				event.setGui(new GuiDownloadCaveTerrain(mc.getConnection()));
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlaySound(PlaySoundEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		ISound sound = event.getSound();

		if (sound != null && sound.getCategory() == SoundCategory.MUSIC && CavernAPI.dimension.isEntityInCaves(mc.thePlayer))
		{
			event.setResultSound(null);
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onConnected(ClientConnectedToServerEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (GeneralConfig.versionNotify)
		{
			ITextComponent message;
			ITextComponent name = new TextComponentString(Cavern.metadata.name);
			name.getStyle().setColor(TextFormatting.AQUA);

			if (Version.isOutdated())
			{
				ITextComponent latest = new TextComponentString(Version.getLatest().toString());
				latest.getStyle().setColor(TextFormatting.YELLOW);

				message = new TextComponentTranslation("cavern.version.message", name);
				message.appendText(" : ").appendSibling(latest);
				message.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Cavern.metadata.url));

				mc.ingameGUI.getChatGUI().printChatMessage(message);
			}

			message = null;

			if (Version.DEV_DEBUG)
			{
				message = new TextComponentTranslation("cavern.version.message.dev", name);
			}
			else if (Version.isBeta())
			{
				message = new TextComponentTranslation("cavern.version.message.beta", name);
			}
			else if (Version.isAlpha())
			{
				message = new TextComponentTranslation("cavern.version.message.alpha", name);
			}

			if (message != null)
			{
				mc.ingameGUI.getChatGUI().printChatMessage(message);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogDensity(FogDensity event)
	{
		Entity entity = event.getEntity();
		IBlockState state = event.getState();

		if (CavernAPI.dimension.isEntityInCaves(entity))
		{
			if (state.getMaterial() == Material.WATER)
			{
				if (entity instanceof EntityPlayer)
				{
					EntityPlayer player = (EntityPlayer)entity;

					if (MinerStats.get(player).getRank() >= MinerRank.AQUA_MINER.getRank())
					{
						GlStateManager.setFog(GlStateManager.FogMode.EXP);

						if (player.isPotionActive(MobEffects.WATER_BREATHING))
						{
							event.setDensity(0.005F);
						}
						else
						{
							event.setDensity(0.01F - EnchantmentHelper.getRespirationModifier((EntityLivingBase)entity) * 0.003F);
						}

						event.setCanceled(true);
					}
				}
			}
			else if (CavernAPI.dimension.isEntityInCaveland(entity))
			{
				GlStateManager.setFog(GlStateManager.FogMode.EXP);

				event.setDensity((float)Math.abs(Math.pow((Math.min(entity.posY, 20) - 63) / (255 - 63), 4)));
				event.setCanceled(true);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFogColors(FogColors event)
	{
		Entity entity = event.getEntity();
		float var1 = 0.0F;

		if (CavernAPI.dimension.isEntityInCaveland(entity))
		{
			var1 = 0.7F;
		}

		if (var1 > 0.0F)
		{
			float red = event.getRed();
			float green = event.getGreen();
			float blue = event.getBlue();
			float var2 = 1.0F / red;

			if (var2 > 1.0F / green)
			{
				var2 = 1.0F / green;
			}

			if (var2 > 1.0F / blue)
			{
				var2 = 1.0F / blue;
			}

			event.setRed(red * (1.0F - var1) + red * var2 * var1);
			event.setGreen(green * (1.0F - var1) + green * var2 * var1);
			event.setBlue(blue * (1.0F - var1) + blue * var2 * var1);
		}
	}
}