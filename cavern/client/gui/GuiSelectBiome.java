package cavern.client.gui;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import cavern.client.config.CaveConfigGui;
import cavern.config.Config;
import cavern.util.ArrayListExtended;
import cavern.util.CaveFilters;
import cavern.util.CaveUtils;
import cavern.util.PanoramaPaths;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ArrayEntry;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectBiome extends GuiScreen
{
	protected final GuiScreen parent;

	protected IBiomeSelector selector;
	protected int selectorId;

	protected GuiTextField biomeField;

	protected ArrayEntry arrayEntry;

	protected BiomeList biomeList;

	protected GuiButton doneButton;

	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;

	protected GuiTextField filterTextField;

	protected HoverChecker selectedHoverChecker;
	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	public GuiSelectBiome(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectBiome(GuiScreen parent, IBiomeSelector selector, int id)
	{
		this(parent);
		this.selector = selector;
		this.selectorId = id;
	}

	public GuiSelectBiome(GuiScreen parent, GuiTextField biomeField)
	{
		this(parent);
		this.biomeField = biomeField;
	}

	public GuiSelectBiome(GuiScreen parent, ArrayEntry entry)
	{
		this(parent);
		this.arrayEntry = entry;
	}

	@Override
	public void initGui()
	{
		if (biomeList == null)
		{
			biomeList = new BiomeList();
		}

		biomeList.setDimensions(width, height, 32, height - 28);

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

		selectedHoverChecker = new HoverChecker(0, 20, 0, 100, 800);
		detailHoverChecker = new HoverChecker(detailInfo, 800);
		instantHoverChecker = new HoverChecker(instantFilter, 800);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button.enabled)
		{
			switch (button.id)
			{
				case 0:
					if (selector != null)
					{
						selector.onBiomeSelected(selectorId, biomeList.selected);
					}

					if (biomeList.selected.isEmpty())
					{
						if (biomeField != null)
						{
							biomeField.setText("");
						}

						if (arrayEntry != null)
						{
							arrayEntry.setListFromChildScreen(new Object[0]);
						}
					}
					else
					{
						Set<Integer> ret = Sets.newTreeSet();

						for (Biome biome : biomeList.selected)
						{
							if (biome != null)
							{
								ret.add(Biome.getIdForBiome(biome));
							}
						}

						if (!ret.isEmpty())
						{
							if (biomeField != null)
							{
								biomeField.setText(Ints.join(", ", Ints.toArray(ret)));
							}

							if (arrayEntry != null)
							{
								arrayEntry.setListFromChildScreen(ret.toArray());
							}
						}
					}

					if (biomeField != null)
					{
						biomeField.setFocused(true);
						biomeField.setCursorPositionEnd();
					}

					mc.displayGuiScreen(parent);

					biomeList.selected.clear();
					biomeList.scrollToTop();
					break;
				case 1:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
				case 2:
					CaveConfigGui.instantFilter = instantFilter.isChecked();
					break;
				default:
					biomeList.actionPerformed(button);
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
		biomeList.drawScreen(mouseX, mouseY, ticks);

		drawCenteredString(fontRendererObj, I18n.format(Config.LANG_KEY + "select.biome"), width / 2, 15, 0xFFFFFF);

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
		else if (biomeList.isMouseYWithinSlotBounds(mouseY) && isCtrlKeyDown())
		{
			Biome biome = biomeList.contents.get(biomeList.getSlotIndexFromScreenCoords(mouseX, mouseY), null);

			if (biome != null)
			{
				List<String> info = Lists.newArrayList();

				info.add(TextFormatting.DARK_GRAY + Integer.toString(Biome.getIdForBiome(biome)) + ": " + Strings.nullToEmpty(biome.getBiomeName()));

				IBlockState state = biome.topBlock;
				Block block = state.getBlock();
				int meta = block.getMetaFromState(state);
				ItemStack itemstack = new ItemStack(block, 1, meta);
				boolean hasItem = itemstack.getItem() != null;

				String text;

				if (hasItem)
				{
					text = itemstack.getDisplayName();
				}
				else
				{
					text = block.getRegistryName() + ":" + meta;;
				}

				info.add(TextFormatting.GRAY + I18n.format(Config.LANG_KEY + "select.biome.info.topBlock") + ": " + text);

				state = biome.fillerBlock;
				block = state.getBlock();
				meta = block.getMetaFromState(state);
				itemstack = new ItemStack(block, 1, meta);
				hasItem = itemstack.getItem() != null;

				if (hasItem)
				{
					text = itemstack.getDisplayName();
				}
				else
				{
					text = block.getRegistryName() + ":" + meta;;
				}

				info.add(TextFormatting.GRAY + I18n.format(Config.LANG_KEY + "select.biome.info.fillerBlock") + ": " + text);

				info.add(TextFormatting.GRAY + I18n.format(Config.LANG_KEY + "select.biome.info.temperature") + ": " + biome.getTemperature());
				info.add(TextFormatting.GRAY + I18n.format(Config.LANG_KEY + "select.biome.info.rainfall") + ": " + biome.getRainfall());

				if (BiomeDictionary.isBiomeRegistered(biome))
				{
					Set<String> types = Sets.newTreeSet();

					for (Type type : BiomeDictionary.getTypesForBiome(biome))
					{
						types.add(type.name());
					}

					info.add(TextFormatting.GRAY + I18n.format(Config.LANG_KEY + "select.biome.info.type") + ": " + Joiner.on(", ").skipNulls().join(types));
				}

				drawHoveringText(info, mouseX, mouseY);
			}
		}

		if (!biomeList.selected.isEmpty())
		{
			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRendererObj, I18n.format(Config.LANG_KEY + "select.biome.selected", biomeList.selected.size()), 5, 5, 0xEFEFEF);
			}

			if (selectedHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> biomes = Lists.newArrayList();

				for (Biome biome : biomeList.selected)
				{
					if (biome != null)
					{
						biomes.add(Biome.getIdForBiome(biome) + ": " + biome.getBiomeName());
					}
				}

				drawHoveringText(biomes, mouseX, mouseY);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		biomeList.handleMouseInput();
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
				biomeList.setFilter(null);
			}
			else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
			{
				biomeList.setFilter(text);
			}
		}
		else
		{
			if (code == Keyboard.KEY_ESCAPE)
			{
				mc.displayGuiScreen(parent);
			}
			else if (code == Keyboard.KEY_BACK)
			{
				biomeList.selected.clear();
			}
			else if (code == Keyboard.KEY_UP)
			{
				biomeList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				biomeList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				biomeList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				biomeList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				biomeList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				biomeList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				biomeList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
			else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
			{
				biomeList.selected.addAll(biomeList.contents);
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	protected class BiomeList extends GuiListSlot
	{
		protected final ArrayListExtended<Biome> biomes = new ArrayListExtended<>();
		protected final ArrayListExtended<Biome> contents = new ArrayListExtended<>();
		protected final Set<Biome> selected = Sets.newTreeSet(CaveUtils.biomeComparator);
		protected final Map<String, List<Biome>> filterCache = Maps.newHashMap();

		protected boolean clickFlag;

		protected BiomeList()
		{
			super(GuiSelectBiome.this.mc, 0, 0, 0, 0, 18);

			for (Iterator<Biome> iterator = Biome.REGISTRY.iterator(); iterator.hasNext();)
			{
				Biome biome = iterator.next();

				if (biome != null && (selector == null || selector.canSelectBiome(selectorId, biome)))
				{
					biomes.add(biome);
					contents.add(biome);
				}
			}

			if (biomeField != null)
			{
				String text = biomeField.getText();

				if (!Strings.isNullOrEmpty(text))
				{
					if (text.contains(","))
					{
						for (String str : Splitter.on(',').trimResults().omitEmptyStrings().split(text))
						{
							int i = NumberUtils.toInt(str, -1);

							if (i >= 0 && i <= 255)
							{
								Biome biome = Biome.getBiome(i);

								if (biome != null)
								{
									selected.add(biome);
								}
							}
						}
					}
					else
					{
						int i = NumberUtils.toInt(text, -1);

						if (i >= 0 && i <= 255)
						{
							Biome biome = Biome.getBiome(i);

							if (biome != null)
							{
								selected.add(biome);
							}
						}
					}
				}
			}

			if (arrayEntry != null)
			{
				for (Object obj : arrayEntry.getCurrentValues())
				{
					String str = String.valueOf(obj);
					int i = NumberUtils.toInt(str, -1);

					if (i >= 0 && i <= 255)
					{
						Biome biome = Biome.getBiome(i);

						if (biome != null)
						{
							selected.add(biome);
						}
					}
				}
			}

			if (!selected.isEmpty())
			{
				scrollToTop();
				scrollToSelected();
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

				for (Biome biome : selected)
				{
					amount = contents.indexOf(biome) * getSlotHeight();

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
		protected void drawSlot(int slot, int par2, int par3, int par4, int mouseX, int mouseY)
		{
			Biome biome = contents.get(slot, null);

			if (biome == null)
			{
				return;
			}

			String name = biome.getBiomeName();

			if (!Strings.isNullOrEmpty(name))
			{
				drawCenteredString(fontRendererObj, name, width / 2, par3 + 1, 0xFFFFFF);
			}

			if (detailInfo.isChecked() || Keyboard.isKeyDown(Keyboard.KEY_TAB))
			{
				drawString(fontRendererObj, Integer.toString(Biome.getIdForBiome(biome)), width / 2 - 100, par3 + 1, 0xE0E0E0);

				if (Keyboard.isKeyDown(Keyboard.KEY_TAB))
				{
					IBlockState state = biome.topBlock;
					Block block = state.getBlock();
					int meta = block.getMetaFromState(state);
					ItemStack itemstack = new ItemStack(block, 1, meta);
					boolean hasItem = itemstack.getItem() != null;

					if (hasItem)
					{
						try
						{
							GlStateManager.enableRescaleNormal();
							RenderHelper.enableGUIStandardItemLighting();
							itemRender.renderItemIntoGUI(itemstack, width / 2 + 70, par3 - 1);
							RenderHelper.disableStandardItemLighting();
							GlStateManager.disableRescaleNormal();
						}
						catch (Throwable e) {}
					}

					state = biome.fillerBlock;
					block = state.getBlock();
					meta = block.getMetaFromState(state);
					itemstack = new ItemStack(block, 1, meta);
					hasItem = itemstack.getItem() != null;

					if (hasItem)
					{
						try
						{
							GlStateManager.enableRescaleNormal();
							RenderHelper.enableGUIStandardItemLighting();
							itemRender.renderItemIntoGUI(itemstack, width / 2 + 90, par3 - 1);
							RenderHelper.disableStandardItemLighting();
							GlStateManager.disableRescaleNormal();
						}
						catch (Throwable e) {}
					}
				}
			}
		}

		@Override
		protected void elementClicked(int slot, boolean flag, int mouseX, int mouseY)
		{
			Biome biome = contents.get(slot, null);

			if (biome != null && (clickFlag = !clickFlag == true) && !selected.add(biome))
			{
				selected.remove(biome);
			}
		}

		@Override
		protected boolean isSelected(int slot)
		{
			Biome biome = contents.get(slot, null);

			return biome != null && selected.contains(biome);
		}

		protected void setFilter(final String filter)
		{
			CaveUtils.getPool().execute(new RecursiveAction()
			{
				@Override
				protected void compute()
				{
					List<Biome> result;

					if (Strings.isNullOrEmpty(filter))
					{
						result = biomes;
					}
					else if (filter.equals("selected"))
					{
						result = Lists.newArrayList(selected);
					}
					else
					{
						if (!filterCache.containsKey(filter))
						{
							filterCache.put(filter, Lists.newArrayList(Collections2.filter(biomes, new BiomeFilter(filter))));
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

	public static class BiomeFilter implements Predicate<Biome>
	{
		private final String filter;

		public BiomeFilter(String filter)
		{
			this.filter = filter;
		}

		@Override
		public boolean apply(Biome biome)
		{
			return CaveFilters.biomeFilter(biome, filter);
		}
	}
}