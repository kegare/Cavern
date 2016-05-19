package cavern.client.gui;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cavern.client.config.CaveConfigGui;
import cavern.config.Config;
import cavern.util.ArrayListExtended;
import cavern.util.CaveUtils;
import cavern.util.PanoramaPaths;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ArrayEntry;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectMob extends GuiScreen
{
	protected final GuiScreen parent;

	protected ArrayEntry arrayEntry;

	protected MobList mobList;

	protected GuiButton doneButton;

	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;

	protected GuiTextField filterTextField;

	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;
	protected HoverChecker selectedHoverChecker;

	public GuiSelectMob(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectMob(GuiScreen parent, ArrayEntry entry)
	{
		this(parent);
		this.arrayEntry = entry;
	}

	@Override
	public void initGui()
	{
		if (mobList == null)
		{
			mobList = new MobList();
		}

		mobList.setDimensions(width, height, 32, height - 28);

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, 145, 20, I18n.format("gui.done"));
		}

		doneButton.xPosition = width / 2 + 10;
		doneButton.yPosition = height - doneButton.height - 4;

		if (detailInfo == null)
		{
			detailInfo = new GuiCheckBox(1, 0, 5, I18n.format(Config.LANG_KEY + "detail"), true);
		}

		detailInfo.setIsChecked(CaveConfigGui.detailInfo);
		detailInfo.xPosition = width / 2 + 95;

		if (instantFilter == null)
		{
			instantFilter = new GuiCheckBox(2, 0, detailInfo.yPosition + detailInfo.height + 2, I18n.format(Config.LANG_KEY + "instant"), true);
		}

		instantFilter.setIsChecked(CaveConfigGui.instantFilter);
		instantFilter.xPosition = detailInfo.xPosition;

		buttonList.clear();
		buttonList.add(doneButton);
		buttonList.add(detailInfo);
		buttonList.add(instantFilter);

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(0, fontRendererObj, 0, 0, 150, 16);
			filterTextField.setMaxStringLength(100);
		}

		filterTextField.xPosition = width / 2 - filterTextField.width - 5;
		filterTextField.yPosition = height - filterTextField.height - 6;

		detailHoverChecker = new HoverChecker(detailInfo, 800);
		instantHoverChecker = new HoverChecker(instantFilter, 800);
		selectedHoverChecker = new HoverChecker(0, 20, 0, 100, 800);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					if (arrayEntry != null)
					{
						arrayEntry.setListFromChildScreen(mobList.selected.toArray());
					}

					mc.displayGuiScreen(parent);

					mobList.selected.clear();
					mobList.scrollToTop();
					break;
				case 1:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
				case 2:
					CaveConfigGui.instantFilter = instantFilter.isChecked();
					break;
			}
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		filterTextField.updateCursorCounter();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float ticks)
	{
		mobList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(Config.LANG_KEY + "select.mob"), width / 2, 15, 0xFFFFFF);

		super.drawScreen(mouseX, mouseY, ticks);

		filterTextField.drawTextBox();

		if (detailHoverChecker.checkHover(mouseX, mouseY))
		{
			drawHoveringText(fontRendererObj.listFormattedStringToWidth(I18n.format(Config.LANG_KEY + "detail.hover"), 300), mouseX, mouseY);
		}
		else if (instantHoverChecker.checkHover(mouseX, mouseY))
		{
			drawHoveringText(fontRendererObj.listFormattedStringToWidth(I18n.format(Config.LANG_KEY + "instant.hover"), 300), mouseX, mouseY);
		}
		else if (!mobList.selected.isEmpty())
		{
			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRendererObj, I18n.format(Config.LANG_KEY + "select.mob.selected", mobList.selected.size()), 5, 5, 0xEFEFEF);
			}

			if (detailInfo.isChecked() && selectedHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> values = Lists.newArrayList();

				for (String mob : mobList.selected)
				{
					values.add(I18n.format("entity." + mob + ".name"));
				}

				drawHoveringText(values, mouseX, mouseY);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		mobList.handleMouseInput();
	}

	@Override
	protected void mouseClicked(int x, int y, int code) throws IOException
	{
		super.mouseClicked(x, y, code);

		filterTextField.mouseClicked(x, y, code);
	}

	@Override
	protected void keyTyped(char c, int code) throws IOException
	{
		if (filterTextField.isFocused())
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				filterTextField.setFocused(false);
			}

			String prev = filterTextField.getText();

			filterTextField.textboxKeyTyped(c, code);

			String text = filterTextField.getText();
			boolean changed = text != prev;

			if (Strings.isNullOrEmpty(text) && changed)
			{
				mobList.setFilter(null);
			}
			else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
			{
				mobList.setFilter(text);
			}
		}
		else
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				mc.displayGuiScreen(parent);

				if (parent == null)
				{
					mc.setIngameFocus();
				}
			}
			else if (code == Keyboard.KEY_BACK)
			{
				mobList.selected.clear();
			}
			else if (code == Keyboard.KEY_TAB)
			{
				if (++mobList.nameType > 1)
				{
					mobList.nameType = 0;
				}
			}
			else if (code == Keyboard.KEY_UP)
			{
				mobList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				mobList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				mobList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				mobList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				mobList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				mobList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				mobList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
			else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
			{
				mobList.selected.clear();
				mobList.selected.addAll(mobList.contents);
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	protected class MobList extends GuiListSlot implements Comparator<String>
	{
		protected final ArrayListExtended<String> mobs = new ArrayListExtended<>();
		protected final ArrayListExtended<String> contents = new ArrayListExtended<>();
		protected final Set<String> selected = Sets.newTreeSet(this);
		protected final Map<String, List<String>> filterCache = Maps.newHashMap();

		protected int nameType;
		protected boolean clickFlag;

		protected MobList()
		{
			super(GuiSelectMob.this.mc, 0, 0, 0, 0, 18);

			for (Iterator<Entry<String, Class<? extends Entity>>> iterator = EntityList.NAME_TO_CLASS.entrySet().iterator(); iterator.hasNext();)
			{
				Entry<String, Class<? extends Entity>> entry = iterator.next();
				String name = entry.getKey();
				Class<? extends Entity> clazz = entry.getValue();

				if (!Strings.isNullOrEmpty(name) && EntityMob.class != clazz && EntityLiving.class != clazz && EntityLiving.class.isAssignableFrom(clazz))
				{
					mobs.addIfAbsent(name);
				}
			}

			Collections.sort(mobs);

			contents.addAll(mobs);

			if (arrayEntry != null)
			{
				for (Object obj : arrayEntry.getCurrentValues())
				{
					String value = String.valueOf(obj);

					if (!Strings.isNullOrEmpty(value))
					{
						selected.add(value);
					}
				}
			}
		}

		@Override
		public PanoramaPaths getPanoramaPaths()
		{
			return null;
		}

		@Override
		public void scrollToSelected()
		{
			if (!selected.isEmpty())
			{
				int amount = 0;

				for (String entry : selected)
				{
					amount = contents.indexOf(entry) * getSlotHeight();

					if (getAmountScrolled() != amount)
					{
						break;
					}
				}

				scrollToTop();
				scrollBy(amount);
			}
		}

		@Override
		protected int getSize()
		{
			return contents.size();
		}

		@Override
		protected void drawBackground()
		{
			drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int index, int par2, int par3, int par4, int mouseX, int mouseY)
		{
			String entry = contents.get(index, null);

			if (Strings.isNullOrEmpty(entry))
			{
				return;
			}

			String name;

			switch (nameType)
			{
				case 1:
					name = entry;
					break;
				default:
					name = I18n.format("entity." + entry + ".name");
					break;
			}

			drawCenteredString(fontRendererObj, name, width / 2, par3 + 1, 0xFFFFFF);
		}

		@Override
		protected void elementClicked(int index, boolean flag, int mouseX, int mouseY)
		{
			String entry = contents.get(index, null);

			if (!Strings.isNullOrEmpty(entry) && (clickFlag = !clickFlag == true) && !selected.remove(entry))
			{
				selected.add(entry);
			}
		}

		@Override
		protected boolean isSelected(int index)
		{
			String entry = contents.get(index, null);

			return !Strings.isNullOrEmpty(entry) && selected.contains(entry);
		}

		@Override
		public int compare(String o1, String o2)
		{
			int i = CaveUtils.compareWithNull(o1, o2);

			if (i == 0 && o1 != null && o2 != null)
			{
				i = Integer.compare(mobs.indexOf(o1), mobs.indexOf(o2));
			}

			return i;
		}

		protected void setFilter(final String filter)
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<String> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = mobs;
					}
					else if (filter.equals("selected"))
					{
						result = Lists.newArrayList(selected);
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(mobs, new MobFilter(filter))));
						}

						result = filterCache.get(filter);
					}

					if (!contents.equals(result))
					{
						contents.clear();
						contents.addAll(result);
					}
				}
			});
		}
	}

	public static class MobFilter implements Predicate<String>
	{
		private final String filter;

		public MobFilter(String filter)
		{
			this.filter = filter;
		}

		@Override
		public boolean apply(String input)
		{
			return StringUtils.containsIgnoreCase(input, filter) || StringUtils.containsIgnoreCase(I18n.format("entity." + input + ".name"), filter);
		}
	}
}