package cavern.client.gui;

import org.lwjgl.input.Keyboard;

import cavern.api.CavernAPI;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.RegenerationGuiMessage.EnumType;
import cavern.network.server.RegenerationMessage;
import cavern.util.DimensionRegeneration;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRegeneration extends GuiScreen
{
	public boolean cavern, aquaCavern, caveland, iceCavern, ruinsCavern, cavenia, hugeCavern;

	protected GuiButton regenButton;
	protected GuiButton cancelButton;

	protected GuiCheckBox backupCheckBox, cavernCheckBox, aquaCavernCheckBox, cavelandCheckBox,
		iceCavernCheckBox, ruinsCavernCheckBox, caveniaCheckBox, hugeCavernCheckBox;

	private HoverChecker backupHoverChecker;

	@Override
	public void initGui()
	{
		if (regenButton == null)
		{
			regenButton = new GuiButtonExt(0, 0, 0, I18n.format("cavern.regeneration.gui.regenerate"));
		}

		regenButton.x = width / 2 - 100;
		regenButton.y = height / 4 + regenButton.height + 65;

		if (cancelButton == null)
		{
			cancelButton = new GuiButtonExt(1, 0, 0, I18n.format("gui.cancel"));
		}

		cancelButton.x = regenButton.x;
		cancelButton.y = regenButton.y + regenButton.height + 5;

		if (backupCheckBox == null)
		{
			backupCheckBox = new GuiCheckBox(2, 10, 0, I18n.format("cavern.regeneration.gui.backup"), DimensionRegeneration.backup);
		}

		backupCheckBox.y = height - 20;

		if (cavernCheckBox == null)
		{
			cavernCheckBox = new GuiCheckBox(3, 10, 8, "Cavern", cavern);
		}

		GuiButton before = cavernCheckBox;

		if (aquaCavernCheckBox == null)
		{
			aquaCavernCheckBox = new GuiCheckBox(4, 10, before.y + before.height + 5, "Aqua Cavern", aquaCavern);
		}

		if (CavernAPI.dimension.isAquaCavernDisabled())
		{
			aquaCavernCheckBox.enabled = false;
			aquaCavernCheckBox.visible = false;
			aquaCavernCheckBox.setIsChecked(false);
		}
		else
		{
			before = aquaCavernCheckBox;
		}

		if (cavelandCheckBox == null)
		{
			cavelandCheckBox = new GuiCheckBox(5, 10, before.y + before.height + 5, "Caveland", caveland);
		}

		if (CavernAPI.dimension.isCavelandDisabled())
		{
			cavelandCheckBox.enabled = false;
			cavelandCheckBox.visible = false;
			cavelandCheckBox.setIsChecked(false);
		}
		else
		{
			before = cavelandCheckBox;
		}

		if (iceCavernCheckBox == null)
		{
			iceCavernCheckBox = new GuiCheckBox(6, 10, before.y + before.height + 5, "Ice Cavern", iceCavern);
		}

		if (CavernAPI.dimension.isIceCavernDisabled())
		{
			iceCavernCheckBox.enabled = false;
			iceCavernCheckBox.visible = false;
			iceCavernCheckBox.setIsChecked(false);
		}
		else
		{
			before = iceCavernCheckBox;
		}

		if (ruinsCavernCheckBox == null)
		{
			ruinsCavernCheckBox = new GuiCheckBox(7, 10, before.y + before.height + 5, "Ruins Cavern", ruinsCavern);
		}

		if (CavernAPI.dimension.isRuinsCavernDisabled())
		{
			ruinsCavernCheckBox.enabled = false;
			ruinsCavernCheckBox.visible = false;
			ruinsCavernCheckBox.setIsChecked(false);
		}
		else
		{
			before = ruinsCavernCheckBox;
		}

		if (caveniaCheckBox == null)
		{
			caveniaCheckBox = new GuiCheckBox(8, 10, before.y + before.height + 5, "Cavenia", cavenia);
		}

		if (CavernAPI.dimension.isCaveniaDisabled())
		{
			caveniaCheckBox.enabled = false;
			caveniaCheckBox.visible = false;
			caveniaCheckBox.setIsChecked(false);
		}
		else
		{
			before = caveniaCheckBox;
		}

		if (hugeCavernCheckBox == null)
		{
			hugeCavernCheckBox = new GuiCheckBox(9, 10, before.y + before.height + 5, "Huge Cavern", hugeCavern);
		}

		if (CavernAPI.dimension.isHugeCavernDisabled())
		{
			hugeCavernCheckBox.enabled = false;
			hugeCavernCheckBox.visible = false;
			hugeCavernCheckBox.setIsChecked(false);
		}
		else
		{
			before = hugeCavernCheckBox;
		}

		buttonList.clear();
		buttonList.add(regenButton);
		buttonList.add(cancelButton);
		buttonList.add(backupCheckBox);
		buttonList.add(cavernCheckBox);
		buttonList.add(aquaCavernCheckBox);
		buttonList.add(cavelandCheckBox);
		buttonList.add(iceCavernCheckBox);
		buttonList.add(ruinsCavernCheckBox);
		buttonList.add(caveniaCheckBox);
		buttonList.add(hugeCavernCheckBox);

		if (backupHoverChecker == null)
		{
			backupHoverChecker = new HoverChecker(backupCheckBox, 800);
		}
	}

	@Override
	protected void keyTyped(char c, int code)
	{
		if (code == Keyboard.KEY_ESCAPE)
		{
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					RegenerationMessage message = new RegenerationMessage();

					message.backup = backupCheckBox.isChecked();
					message.cavern = cavernCheckBox.isChecked();
					message.aquaCavern = aquaCavernCheckBox.isChecked();
					message.caveland = cavelandCheckBox.isChecked();
					message.iceCavern = iceCavernCheckBox.isChecked();
					message.ruinsCavern = ruinsCavernCheckBox.isChecked();
					message.cavenia = caveniaCheckBox.isChecked();
					message.hugeCavern = hugeCavernCheckBox.isChecked();

					if (message.cavern || message.aquaCavern || message.caveland || message.iceCavern || message.ruinsCavern || message.cavenia || message.hugeCavern)
					{
						CaveNetworkRegistry.sendToServer(message);
					}

					regenButton.enabled = false;
					cancelButton.visible = false;
					break;
				case 1:
					mc.displayGuiScreen(null);
					mc.setIngameFocus();
					break;
				case 2:
					DimensionRegeneration.backup = backupCheckBox.isChecked();
					break;
			}
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		drawGradientRect(0, 0, width, height, 0, Integer.MAX_VALUE);

		GlStateManager.pushMatrix();
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		drawCenteredString(fontRenderer, I18n.format("cavern.regeneration.gui.title"), width / 4, 30, 0xFFFFFF);
		GlStateManager.popMatrix();

		drawCenteredString(fontRenderer, I18n.format("cavern.regeneration.gui.info"), width / 2, 100, 0xEEEEEE);

		super.drawScreen(mouseX, mouseY, ticks);

		if (backupHoverChecker.checkHover(mouseX, mouseY))
		{
			drawHoveringText(fontRenderer.listFormattedStringToWidth(I18n.format("cavern.regeneration.gui.backup.tooltip"), 300), mouseX, mouseY);
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	public void updateProgress(EnumType type)
	{
		regenButton.enabled = false;
		cancelButton.visible = false;

		if (type == null)
		{
			regenButton.visible = false;
			cancelButton.visible = true;
		}
		else switch (type)
		{
			case START:
				regenButton.displayString = I18n.format("cavern.regeneration.gui.progress.start");
				break;
			case BACKUP:
				regenButton.displayString = I18n.format("cavern.regeneration.gui.progress.backup");
				break;
			case REGENERATED:
				regenButton.displayString = I18n.format("cavern.regeneration.gui.progress.regenerated");
				cancelButton.displayString = I18n.format("gui.done");
				cancelButton.visible = true;
				break;
			case FAILED:
				regenButton.displayString = I18n.format("cavern.regeneration.gui.progress.failed");
				cancelButton.visible = true;
				break;
			default:
		}
	}
}