package cavern.client.handler;

import cavern.api.CavernAPI;
import cavern.api.IMinerStats;
import cavern.client.CaveRenderingRegistry;
import cavern.config.DisplayConfig;
import cavern.config.GeneralConfig;
import cavern.config.MiningAssistConfig;
import cavern.config.property.ConfigDisplayPos;
import cavern.miningassist.MiningAssist;
import cavern.network.server.StatsAdjustRequestMessage;
import cavern.stats.MinerRank;
import cavern.stats.MinerStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MinerStatsHUDEventHooks
{
	public static ConfigDisplayPos.Type currentPosition;

	private int posX;
	private int posY;

	private double miningPointPer = -1.0D;

	protected ConfigDisplayPos.Type getDisplayType()
	{
		return DisplayConfig.miningPointPosition.getType();
	}

	protected boolean canRenderHUD(Minecraft mc)
	{
		ConfigDisplayPos.Type type = getDisplayType();

		if (type.isHidden())
		{
			return false;
		}

		if (mc.currentScreen != null && !GuiChat.class.isInstance(mc.currentScreen))
		{
			return false;
		}

		if (type == HunterStatsHUDEventHooks.currentPosition || type == MagicianStatsHUDEventHooks.currentPosition)
		{
			return false;
		}

		if (!CavernAPI.dimension.isEntityInCaves(mc.player) || CavernAPI.dimension.isEntityInCavenia(mc.player))
		{
			return false;
		}

		for (ItemStack held : mc.player.getHeldEquipment())
		{
			if (GeneralConfig.isMiningPointItem(held))
			{
				return true;
			}
		}

		return DisplayConfig.alwaysShowMinerStatus || mc.player.capabilities.isCreativeMode || mc.gameSettings.advancedItemTooltips;
	}

	protected void setDisplayPos(ConfigDisplayPos.Type type, Minecraft mc, int scaledWidth, int scaledHeight)
	{
		switch (type)
		{
			case TOP_RIGHT:
				posX = scaledWidth - 20;
				posY = 5;

				if (!mc.player.getActivePotionEffects().isEmpty())
				{
					posY = 30;
				}

				break;
			case TOP_LEFT:
				posX = 5;
				posY = 5;
				break;
			case BOTTOM_RIGHT:
				posX = scaledWidth - 20;
				posY = scaledHeight - 21;
				break;
			case BOTTOM_LEFT:
				posX = 5;
				posY = scaledHeight - 21;
				break;
			default:
		}
	}

	protected double calcMiningPointPer(int point, int phase, boolean direct)
	{
		double per = point == 0 ? 0.0D : (double)point / (double)phase * 100.0D;

		if (direct)
		{
			return per;
		}

		double diff = Math.abs(per - miningPointPer);
		double d1 = 0.0175D;
		double d2 = 0.35D;

		if (miningPointPer < 0.0D || diff < d1)
		{
			miningPointPer = per;
		}
		else
		{
			if (per > miningPointPer)
			{
				if (diff > 1.0D)
				{
					miningPointPer += d2;
				}
				else
				{
					miningPointPer += d1;
				}
			}
			else if (per < miningPointPer)
			{
				if (diff > 1.0D)
				{
					miningPointPer -= d2 * 2.0D;
				}
				else
				{
					miningPointPer -= d1 * 1.5D;
				}
			}
		}

		return miningPointPer;
	}

	@SubscribeEvent
	public void onRenderGamePostOverlay(RenderGameOverlayEvent.Post event)
	{
		if (event.getType() != ElementType.HOTBAR)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();
		ConfigDisplayPos.Type displayType = getDisplayType();

		if (canRenderHUD(mc))
		{
			currentPosition = displayType;
		}
		else
		{
			currentPosition = ConfigDisplayPos.Type.HIDDEN;
			miningPointPer = -1.0D;

			return;
		}

		ScaledResolution resolution = event.getResolution();
		IMinerStats stats = MinerStats.get(mc.player, true);

		if (stats == null || !stats.isClientAdjusted())
		{
			StatsAdjustRequestMessage.request();

			return;
		}

		MinerRank minerRank = MinerRank.get(stats.getRank());
		MiningAssist miningAssist = MiningAssist.get(stats.getMiningAssist());

		String point = Integer.toString(stats.getPoint());
		String rank = I18n.format(minerRank.getUnlocalizedName());

		if (miningAssist != MiningAssist.DISABLED && stats.getRank() >= MiningAssistConfig.minerRank.getValue())
		{
			rank += " / " + I18n.format(miningAssist.getUnlocalizedName());
		}

		setDisplayPos(displayType, mc, resolution.getScaledWidth(), resolution.getScaledHeight());

		int x = posX;
		int y = posY;

		RenderItem renderItem = mc.getRenderItem();
		FontRenderer renderer = mc.fontRenderer;
		boolean flag = false;
		long processTime = Minecraft.getSystemTime() - MinerStats.lastMineTime;

		if (MinerStats.lastMineTime > 0 && processTime < 2000L && MinerStats.lastMine != null && MinerStats.lastMinePoint != 0)
		{
			ItemStack item = new ItemStack(CaveRenderingRegistry.getRenderBlock(MinerStats.lastMine.getBlock()), 1, MinerStats.lastMine.getMeta());

			RenderHelper.enableGUIStandardItemLighting();
			renderItem.renderItemIntoGUI(item, x, y);
			renderItem.renderItemOverlayIntoGUI(renderer, item, x, y, Integer.toString(MinerStats.lastMinePoint));
			RenderHelper.disableStandardItemLighting();

			flag = true;
		}

		if (flag)
		{
			x += displayType.isLeft() ? 20 : -20;
		}

		renderItem.renderItemIntoGUI(minerRank.getItemStack(), x, y);

		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		if (point.length() <= 1)
		{
			point = " " + point;
		}

		MinerRank nextRank = MinerRank.get(stats.getRank() + 1);

		if (minerRank.getRank() < nextRank.getRank())
		{
			String per = String.format("%.2f", calcMiningPointPer(stats.getPoint(), nextRank.getPhase(), false)) + "%";

			point = displayType.isLeft() ? point + " < " + per : per + " > " + point;
		}

		String combo = null;

		if (processTime > 15000L)
		{
			MinerStats.mineCombo = 0;
		}
		else if (MinerStats.mineCombo > 0)
		{
			TextFormatting format = TextFormatting.WHITE;

			if (processTime < 3000L)
			{
				format = TextFormatting.BOLD;
			}
			else if (processTime > 12000L)
			{
				format = TextFormatting.GRAY;
			}

			combo = format + String.format("%d COMBO!", MinerStats.mineCombo) + TextFormatting.RESET;
		}

		boolean showRank = DisplayConfig.showMinerRank;
		boolean hasCombo = combo != null;
		int pointX = displayType.isLeft() ? x + 5 : x + 17 - renderer.getStringWidth(point);
		int pointY = y + 9;
		int rankX = showRank ? displayType.isLeft() ? posX + 5 : posX + 17 - renderer.getStringWidth(rank) : -1;
		int rankY = showRank ? displayType.isTop() ? y + 21 : y - 12 : -1;
		int comboX = hasCombo ? displayType.isLeft() ? posX + 5 : posX + 17 - renderer.getStringWidth(combo) : -1;
		int comboY = hasCombo ? displayType.isTop() ? y + 33 : y - 24 : -1;

		renderer.drawStringWithShadow(point, pointX, pointY, 0xCECECE);

		if (showRank)
		{
			renderer.drawStringWithShadow(rank, rankX, rankY, 0xCECECE);

			if (hasCombo)
			{
				renderer.drawStringWithShadow(combo, comboX, comboY, 0xFFFFFF);
			}
		}

		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}