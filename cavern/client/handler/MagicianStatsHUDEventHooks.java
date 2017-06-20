package cavern.client.handler;

import cavern.api.IMagicianStats;
import cavern.config.GeneralConfig;
import cavern.config.property.ConfigDisplayPos;
import cavern.item.CaveItems;
import cavern.network.server.StatsAdjustRequestMessage;
import cavern.stats.MagicianRank;
import cavern.stats.MagicianStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MagicianStatsHUDEventHooks
{
	private int posX;
	private int posY;

	private double magicianPointPer = -1.0D;

	protected ConfigDisplayPos.Type getDisplayType()
	{
		return GeneralConfig.magicianPointPosition.getType();
	}

	protected boolean canRenderHUD(Minecraft mc)
	{
		if (getDisplayType().isHidden())
		{
			return false;
		}

		if (mc.player == null)
		{
			return false;
		}

		if (mc.currentScreen != null && !GuiChat.class.isInstance(mc.currentScreen))
		{
			return false;
		}

		return CaveItems.hasMagicalItem(mc.player, true);
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

	protected double calcMagicianPointPer(int point, int phase, boolean direct)
	{
		double per = point == 0 ? 0.0D : (double)point / (double)phase * 100.0D;

		if (direct)
		{
			return per;
		}

		double diff = Math.abs(per - magicianPointPer);
		double d1 = 0.0175D;
		double d2 = 0.35D;

		if (magicianPointPer < 0.0D || diff < d1)
		{
			magicianPointPer = per;
		}
		else
		{
			if (per > magicianPointPer)
			{
				if (diff > 1.0D)
				{
					magicianPointPer += d2;
				}
				else
				{
					magicianPointPer += d1;
				}
			}
			else if (per < magicianPointPer)
			{
				if (diff > 1.0D)
				{
					magicianPointPer -= d2 * 2.0D;
				}
				else
				{
					magicianPointPer -= d1 * 1.5D;
				}
			}
		}

		return magicianPointPer;
	}

	@SubscribeEvent
	public void onRenderGamePostOverlay(RenderGameOverlayEvent.Post event)
	{
		if (event.getType() != ElementType.HOTBAR)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();

		if (!canRenderHUD(mc))
		{
			magicianPointPer = -1.0D;

			return;
		}

		ScaledResolution resolution = event.getResolution();
		ConfigDisplayPos.Type displayType = getDisplayType();

		IMagicianStats stats = MagicianStats.get(mc.player, true);

		if (stats == null || !stats.isClientAdjusted())
		{
			StatsAdjustRequestMessage.request();

			return;
		}

		MagicianRank magicianRank = MagicianRank.get(stats.getRank());

		String point = Integer.toString(stats.getPoint());
		String rank = I18n.format(magicianRank.getUnlocalizedName());

		setDisplayPos(displayType, mc, resolution.getScaledWidth(), resolution.getScaledHeight());

		int x = posX;
		int y = posY;

		RenderItem renderItem = mc.getRenderItem();
		FontRenderer renderer = mc.fontRenderer;

		renderItem.renderItemIntoGUI(magicianRank.getItemStack(), x, y);

		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		if (point.length() <= 1)
		{
			point = " " + point;
		}

		MagicianRank nextRank = MagicianRank.get(stats.getRank() + 1);

		if (magicianRank.getRank() < nextRank.getRank())
		{
			String per = String.format("%.2f", calcMagicianPointPer(stats.getPoint(), nextRank.getPhase(), false)) + "%";

			point = displayType.isLeft() ? point + " < " + per : per + " > " + point;
		}

		String mp = TextFormatting.GRAY + String.format("%d / %d", stats.getMP(), magicianRank.getMaxMP(mc.player)) + TextFormatting.RESET;
		boolean showRank = GeneralConfig.showMagicianRank;
		int pointX = displayType.isLeft() ? x + 5 : x + 17 - renderer.getStringWidth(point);
		int pointY = y + 9;
		int rankX = showRank ? displayType.isLeft() ? posX + 5 : posX + 17 - renderer.getStringWidth(rank) : -1;
		int rankY = showRank ? displayType.isTop() ? y + 21 : y - 12 : -1;
		int mpX = displayType.isLeft() ? posX + 5 : posX + 17 - renderer.getStringWidth(mp);
		int mpY = displayType.isTop() ? y + 33 : y - 24;

		renderer.drawStringWithShadow(point, pointX, pointY, 0xCECECE);

		if (showRank)
		{
			renderer.drawStringWithShadow(rank, rankX, rankY, 0xCECECE);
		}

		renderer.drawStringWithShadow(mp, mpX, mpY, 0xFFFFFF);

		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}