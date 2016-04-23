package cavern.client.gui;

import org.lwjgl.input.Keyboard;

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
	protected boolean cavern = true;
	protected boolean aquaCavern = true;
	protected boolean caveland = true;

	protected GuiButton regenButton;
	protected GuiButton cancelButton;

	protected GuiCheckBox backupCheckBox, cavernCheckBox, aquaCavernCheckBox, cavelandCheckBox;

	private HoverChecker backupHoverChecker;

	public GuiRegeneration() {}

	public GuiRegeneration(boolean cavern, boolean aquaCavern, boolean caveland)
	{
		this.cavern = cavern;
		this.aquaCavern = aquaCavern;
		this.caveland = caveland;
	}

	@Override
	public void initGui()
	{
		if (regenButton == null)
		{
			regenButton = new GuiButtonExt(0, 0, 0, I18n.format("cavern.regeneration.gui.regenerate"));
		}

		regenButton.xPosition = width / 2 - 100;
		regenButton.yPosition = height / 4 + regenButton.height + 65;

		if (cancelButton == null)
		{
			cancelButton = new GuiButtonExt(1, 0, 0, I18n.format("gui.cancel"));
		}

		cancelButton.xPosition = regenButton.xPosition;
		cancelButton.yPosition = regenButton.yPosition + regenButton.height + 5;

		if (backupCheckBox == null)
		{
			backupCheckBox = new GuiCheckBox(2, 10, 0, I18n.format("cavern.regeneration.gui.backup"), DimensionRegeneration.backup);
		}

		backupCheckBox.yPosition = height - 20;

		if (cavernCheckBox == null)
		{
			cavernCheckBox = new GuiCheckBox(3, 10, 8, "Cavern", cavern);
		}

		if (aquaCavernCheckBox == null)
		{
			aquaCavernCheckBox = new GuiCheckBox(4, 10, cavernCheckBox.yPosition + cavernCheckBox.height + 5, "Aqua Cavern", aquaCavern);
		}

		if (cavelandCheckBox == null)
		{
			cavelandCheckBox = new GuiCheckBox(5, 10, aquaCavernCheckBox.yPosition + aquaCavernCheckBox.height + 5, "Caveland", caveland);
		}

		buttonList.clear();
		buttonList.add(regenButton);
		buttonList.add(cancelButton);
		buttonList.add(backupCheckBox);
		buttonList.add(cavernCheckBox);
		buttonList.add(aquaCavernCheckBox);
		buttonList.add(cavelandCheckBox);

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
					CaveNetworkRegistry.sendToServer(new RegenerationMessage(backupCheckBox.isChecked(), cavernCheckBox.isChecked(), aquaCavernCheckBox.isChecked(), cavelandCheckBox.isChecked()));

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
		GlStateManager.scale(1.5F, 1.5F, 1.0F);
		drawCenteredString(fontRendererObj, I18n.format("cavern.regeneration.gui.title"), width / 3, 30, 0xFFFFFF);
		GlStateManager.popMatrix();

		drawCenteredString(fontRendererObj, I18n.format("cavern.regeneration.gui.info"), width / 2, 90, 0xEEEEEE);

		super.drawScreen(mouseX, mouseY, ticks);

		if (backupHoverChecker.checkHover(mouseX, mouseY))
		{
			drawHoveringText(fontRendererObj.listFormattedStringToWidth(I18n.format("cavern.regeneration.gui.backup.tooltip"), 300), mouseX, mouseY);
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