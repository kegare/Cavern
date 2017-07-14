package cavern.client.handler;

import cavern.api.CavernAPI;
import cavern.api.IHunterStats;
import cavern.config.DisplayConfig;
import cavern.config.property.ConfigDisplayPos;
import cavern.network.server.StatsAdjustRequestMessage;
import cavern.stats.HunterRank;
import cavern.stats.HunterStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HunterStatsHUDEventHooks
{
	public static ConfigDisplayPos.Type currentPosition;

	private int posX;
	private int posY;

	private double huntingPointPer = -1.0D;

	protected ConfigDisplayPos.Type getDisplayType()
	{
		return DisplayConfig.huntingPointPosition.getType();
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

		if (type == MinerStatsHUDEventHooks.currentPosition || type == MagicianStatsHUDEventHooks.currentPosition)
		{
			return false;
		}

		return CavernAPI.dimension.isEntityInCavenia(mc.player);
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

	protected double calcHuntingPointPer(int point, int phase, boolean direct)
	{
		double per = point == 0 ? 0.0D : (double)point / (double)phase * 100.0D;

		if (direct)
		{
			return per;
		}

		double diff = Math.abs(per - huntingPointPer);
		double d1 = 0.0175D;
		double d2 = 0.35D;

		if (huntingPointPer < 0.0D || diff < d1)
		{
			huntingPointPer = per;
		}
		else
		{
			if (per > huntingPointPer)
			{
				if (diff > 1.0D)
				{
					huntingPointPer += d2;
				}
				else
				{
					huntingPointPer += d1;
				}
			}
			else if (per < huntingPointPer)
			{
				if (diff > 1.0D)
				{
					huntingPointPer -= d2 * 2.0D;
				}
				else
				{
					huntingPointPer -= d1 * 1.5D;
				}
			}
		}

		return huntingPointPer;
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
			huntingPointPer = -1.0D;

			return;
		}

		ScaledResolution resolution = event.getResolution();

		IHunterStats stats = HunterStats.get(mc.player, true);

		if (stats == null || !stats.isClientAdjusted())
		{
			StatsAdjustRequestMessage.request();

			return;
		}

		HunterRank hunterRank = HunterRank.get(stats.getRank());

		String point = Integer.toString(stats.getPoint());
		String rank = I18n.format(hunterRank.getUnlocalizedName());

		setDisplayPos(displayType, mc, resolution.getScaledWidth(), resolution.getScaledHeight());

		int x = posX;
		int y = posY;

		RenderItem renderItem = mc.getRenderItem();
		FontRenderer renderer = mc.fontRenderer;

		renderItem.renderItemIntoGUI(hunterRank.getItemStack(), x, y);

		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		if (point.length() <= 1)
		{
			point = " " + point;
		}

		HunterRank nextRank = HunterRank.get(stats.getRank() + 1);

		if (hunterRank.getRank() < nextRank.getRank())
		{
			String per = String.format("%.2f", calcHuntingPointPer(stats.getPoint(), nextRank.getPhase(), false)) + "%";

			point = displayType.isLeft() ? point + " < " + per : per + " > " + point;
		}

		boolean showRank = DisplayConfig.showHunterRank;
		int pointX = displayType.isLeft() ? x + 5 : x + 17 - renderer.getStringWidth(point);
		int pointY = y + 9;
		int rankX = showRank ? displayType.isLeft() ? posX + 5 : posX + 17 - renderer.getStringWidth(rank) : -1;
		int rankY = showRank ? displayType.isTop() ? y + 21 : y - 12 : -1;

		renderer.drawStringWithShadow(point, pointX, pointY, 0xCECECE);

		if (showRank)
		{
			renderer.drawStringWithShadow(rank, rankX, rankY, 0xCECECE);
		}

		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
}