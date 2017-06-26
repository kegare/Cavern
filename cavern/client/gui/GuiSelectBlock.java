package cavern.client.gui;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cavern.client.config.CaveConfigGui;
import cavern.config.Config;
import cavern.util.ArrayListExtended;
import cavern.util.BlockMeta;
import cavern.util.CaveFilters;
import cavern.util.CaveUtils;
import cavern.util.PanoramaPaths;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import net.minecraftforge.fml.client.config.GuiConfigEntries.ArrayEntry;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectBlock extends GuiScreen
{
	private static final ArrayListExtended<BlockMeta> BLOCKS = new ArrayListExtended<>();

	static
	{
		NonNullList<ItemStack> list = NonNullList.create();

		for (Block block : Block.REGISTRY)
		{
			if (block == null)
			{
				continue;
			}

			try
			{
				list.clear();

				block.getSubBlocks(block.getCreativeTabToDisplayOn(), list);

				if (list.isEmpty())
				{
					if (!block.hasTileEntity(block.getDefaultState()))
					{
						BLOCKS.addIfAbsent(new BlockMeta(block, 0));
					}
				}
				else for (ItemStack stack : list)
				{
					if (stack.isEmpty())
					{
						continue;
					}

					Block sub = Block.getBlockFromItem(stack.getItem());

					if (sub == null || sub == Blocks.AIR)
					{
						continue;
					}

					int meta = stack.getMetadata();

					if (meta < 0 || meta > 15 || sub.hasTileEntity(sub.getStateFromMeta(meta)))
					{
						continue;
					}

					BLOCKS.addIfAbsent(new BlockMeta(sub, meta));
				}
			}
			catch (Throwable e) {}
		}
	}

	protected final GuiScreen parent;

	protected IBlockSelector selector;
	protected int selectorId;

	protected GuiTextField nameField, metaField;

	protected ArrayEntry arrayEntry;

	protected BlockList blockList;

	protected GuiButton doneButton;

	protected GuiCheckBox detailInfo;
	protected GuiCheckBox instantFilter;

	protected GuiTextField filterTextField;

	protected HoverChecker selectedHoverChecker;
	protected HoverChecker detailHoverChecker;
	protected HoverChecker instantHoverChecker;

	public GuiSelectBlock(GuiScreen parent)
	{
		this.parent = parent;
	}

	public GuiSelectBlock(GuiScreen parent, IBlockSelector selector, int id)
	{
		this(parent);
		this.selector = selector;
		this.selectorId = id;
	}

	public GuiSelectBlock(GuiScreen parent, GuiTextField nameField, GuiTextField metaField)
	{
		this(parent);
		this.nameField = nameField;
		this.metaField = metaField;
	}

	public GuiSelectBlock(GuiScreen parent, GuiTextField nameField, GuiTextField metaField, IBlockSelector selector, int id)
	{
		this(parent, nameField, metaField);
		this.selector = selector;
		this.selectorId = id;
	}

	public GuiSelectBlock(GuiScreen parent, ArrayEntry arrayEntry)
	{
		this(parent);
		this.arrayEntry = arrayEntry;
	}

	public GuiSelectBlock(GuiScreen parent, ArrayEntry arrayEntry, IBlockSelector selector, int id)
	{
		this(parent, arrayEntry);
		this.selector = selector;
		this.selectorId = id;
	}

	@Override
	public void initGui()
	{
		if (blockList == null)
		{
			blockList = new BlockList();
		}

		blockList.setDimensions(width, height, 32, height - 28);

		if (doneButton == null)
		{
			doneButton = new GuiButtonExt(0, 0, 0, 145, 20, I18n.format("gui.done"));
		}

		doneButton.x = width / 2 + 10;
		doneButton.y = height - doneButton.height - 4;

		if (detailInfo == null)
		{
			detailInfo = new GuiCheckBox(1, 0, 5, I18n.format(Config.LANG_KEY + "detail"), true);
		}

		detailInfo.setIsChecked(CaveConfigGui.detailInfo);
		detailInfo.x = width / 2 + 95;

		if (instantFilter == null)
		{
			instantFilter = new GuiCheckBox(2, 0, detailInfo.y + detailInfo.height + 2, I18n.format(Config.LANG_KEY + "instant"), true);
		}

		instantFilter.setIsChecked(CaveConfigGui.instantFilter);
		instantFilter.x = detailInfo.x;

		buttonList.clear();
		buttonList.add(doneButton);
		buttonList.add(detailInfo);
		buttonList.add(instantFilter);

		if (filterTextField == null)
		{
			filterTextField = new GuiTextField(0, fontRenderer, 0, 0, 150, 16);
			filterTextField.setMaxStringLength(100);
		}

		filterTextField.x = width / 2 - filterTextField.width - 5;
		filterTextField.y = height - filterTextField.height - 6;

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
						selector.onBlockSelected(selectorId, blockList.selected);
					}

					if (arrayEntry != null)
					{
						if (blockList.selected.isEmpty())
						{
							arrayEntry.setListFromChildScreen(new String[0]);
						}
						else
						{
							Set<String> values = Sets.newLinkedHashSet();

							for (BlockMeta blockMeta : blockList.selected)
							{
								if (blockMeta.getMeta() <= 0)
								{
									values.add(blockMeta.getBlockName());
								}
								else
								{
									values.add(blockMeta.getName());
								}
							}

							arrayEntry.setListFromChildScreen(values.toArray());
						}
					}

					if (blockList.selected.isEmpty())
					{
						if (nameField != null)
						{
							nameField.setText("");
						}

						if (metaField != null)
						{
							metaField.setText("");
						}
					}
					else for (BlockMeta blockMeta : blockList.selected)
					{
						if (nameField != null)
						{
							nameField.setText(blockMeta.getBlockName());
						}

						if (metaField != null)
						{
							metaField.setText(blockMeta.getMetaString());
						}

						break;
					}

					if (nameField != null)
					{
						nameField.setFocused(true);
						nameField.setCursorPositionEnd();
					}
					else if (metaField != null)
					{
						metaField.setFocused(true);
						metaField.setCursorPositionEnd();
					}

					mc.displayGuiScreen(parent);

					blockList.selected.clear();
					blockList.scrollToTop();
					break;
				case 1:
					CaveConfigGui.detailInfo = detailInfo.isChecked();
					break;
				case 2:
					CaveConfigGui.instantFilter = instantFilter.isChecked();
					break;
				default:
					blockList.actionPerformed(button);
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
		blockList.drawScreen(mouseX, mouseY, ticks);

		boolean single = nameField != null || metaField != null;
		String name = null;

		if (single)
		{
			name = I18n.format(Config.LANG_KEY + "select.block");
		}
		else
		{
			name = I18n.format(Config.LANG_KEY + "select.block.multiple");
		}

		if (!Strings.isNullOrEmpty(name))
		{
			drawCenteredString(fontRenderer, name, width / 2, 15, 0xFFFFFF);
		}

		super.drawScreen(mouseX, mouseY, ticks);

		filterTextField.drawTextBox();

		if (detailHoverChecker.checkHover(mouseX, mouseY))
		{
			drawHoveringText(fontRenderer.listFormattedStringToWidth(I18n.format(Config.LANG_KEY + "detail.hover"), 300), mouseX, mouseY);
		}
		else if (instantHoverChecker.checkHover(mouseX, mouseY))
		{
			drawHoveringText(fontRenderer.listFormattedStringToWidth(I18n.format(Config.LANG_KEY + "instant.hover"), 300), mouseX, mouseY);
		}

		if (!single && !blockList.selected.isEmpty())
		{
			if (mouseX <= 100 && mouseY <= 20)
			{
				drawString(fontRenderer, I18n.format(Config.LANG_KEY + "select.block.selected", blockList.selected.size()), 5, 5, 0xEFEFEF);
			}

			if (selectedHoverChecker.checkHover(mouseX, mouseY))
			{
				List<String> texts = Lists.newArrayList();

				for (BlockMeta blockMeta : blockList.selected)
				{
					name = blockList.getBlockMetaTypeName(blockMeta);

					if (!Strings.isNullOrEmpty(name))
					{
						texts.add(name);
					}
				}

				drawHoveringText(texts, mouseX, mouseY);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();

		blockList.handleMouseInput();
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
				blockList.setFilter(null);
			}
			else if (instantFilter.isChecked() && changed || code == Keyboard.KEY_RETURN)
			{
				blockList.setFilter(text);
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
				blockList.selected.clear();
			}
			else if (code == Keyboard.KEY_TAB)
			{
				if (++blockList.nameType > 2)
				{
					blockList.nameType = 0;
				}
			}
			else if (code == Keyboard.KEY_UP)
			{
				blockList.scrollUp();
			}
			else if (code == Keyboard.KEY_DOWN)
			{
				blockList.scrollDown();
			}
			else if (code == Keyboard.KEY_HOME)
			{
				blockList.scrollToTop();
			}
			else if (code == Keyboard.KEY_END)
			{
				blockList.scrollToEnd();
			}
			else if (code == Keyboard.KEY_SPACE)
			{
				blockList.scrollToSelected();
			}
			else if (code == Keyboard.KEY_PRIOR)
			{
				blockList.scrollToPrev();
			}
			else if (code == Keyboard.KEY_NEXT)
			{
				blockList.scrollToNext();
			}
			else if (code == Keyboard.KEY_F || code == mc.gameSettings.keyBindChat.getKeyCode())
			{
				filterTextField.setFocused(true);
			}
			else if (isCtrlKeyDown() && code == Keyboard.KEY_A)
			{
				blockList.selected.addAll(blockList.contents);
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

	protected class BlockList extends GuiListSlot
	{
		protected final ArrayListExtended<BlockMeta> entries = new ArrayListExtended<>();
		protected final ArrayListExtended<BlockMeta> contents = new ArrayListExtended<>();
		protected final Set<BlockMeta> selected = Sets.newLinkedHashSet();
		protected final Map<String, List<BlockMeta>> filterCache = Maps.newHashMap();

		protected int nameType;
		protected boolean clickFlag;

		protected BlockList()
		{
			super(GuiSelectBlock.this.mc, 0, 0, 0, 0, 18);

			Set<BlockMeta> select = Sets.newHashSet();

			if (nameField != null)
			{
				String name = nameField.getText();
				String meta = Integer.toString(-1);

				if (metaField != null)
				{
					meta = metaField.getText();
				}

				if (!Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(meta))
				{
					select.add(new BlockMeta(name, meta));
				}
			}

			if (arrayEntry != null)
			{
				for (Object obj : arrayEntry.getCurrentValues())
				{
					String value = Objects.toString(obj, "");

					if (!Strings.isNullOrEmpty(value))
					{
						value = value.trim();

						if (!value.contains(":"))
						{
							value = "minecraft:" + value;
						}

						BlockMeta blockMeta;

						if (value.indexOf(':') != value.lastIndexOf(':'))
						{
							int i = value.lastIndexOf(':');

							blockMeta = new BlockMeta(value.substring(0, i), value.substring(i + 1));
						}
						else
						{
							blockMeta = new BlockMeta(value, 0);
						}

						if (blockMeta.getBlock() != null)
						{
							select.add(blockMeta);
						}
					}
				}
			}

			for (BlockMeta blockMeta : BLOCKS)
			{
				if (selector == null || selector.canSelectBlock(selectorId, blockMeta))
				{
					entries.addIfAbsent(blockMeta);
					contents.addIfAbsent(blockMeta);

					if (select.contains(blockMeta))
					{
						selected.add(blockMeta);
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

				for (BlockMeta blockMeta : selected)
				{
					amount = contents.indexOf(blockMeta) * getSlotHeight();

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

		public String getBlockMetaTypeName(@Nullable BlockMeta blockMeta, ItemStack stack)
		{
			if (blockMeta == null)
			{
				return null;
			}

			if (stack.isEmpty())
			{
				stack = new ItemStack(blockMeta.getBlock(), 1, blockMeta.getMeta());
			}

			String name = null;

			try
			{
				if (nameType == 1)
				{
					name = blockMeta.getName();
				}
				else if (stack.getItem() != Items.AIR)
				{
					switch (nameType)
					{
						case 2:
							name = stack.getUnlocalizedName();
							name = name.substring(name.indexOf(".") + 1);
							break;
						default:
							name = stack.getDisplayName();
							break;
					}
				}
				else switch (nameType)
				{
					case 2:
						name = blockMeta.getBlock().getUnlocalizedName();
						name = name.substring(name.indexOf(".") + 1);
						break;
					default:
						name = blockMeta.getBlock().getLocalizedName();
						break;
				}
			}
			catch (Throwable e)
			{
				name = null;
			}

			return name;
		}

		public String getBlockMetaTypeName(@Nullable BlockMeta blockMeta)
		{
			return getBlockMetaTypeName(blockMeta, ItemStack.EMPTY);
		}

		@Override
		protected void drawBackground()
		{
			drawDefaultBackground();
		}

		@Override
		protected void drawSlot(int slot, int par2, int par3, int par4, int mouseX, int mouseY, float partialTicks)
		{
			BlockMeta blockMeta = contents.get(slot, null);

			if (blockMeta == null)
			{
				return;
			}

			Block block = blockMeta.getBlock();
			int meta = blockMeta.getMeta();
			ItemStack stack = new ItemStack(block, 1, meta);
			String name = getBlockMetaTypeName(blockMeta, stack);

			if (!Strings.isNullOrEmpty(name))
			{
				drawCenteredString(fontRenderer, name, width / 2, par3 + 1, 0xFFFFFF);
			}

			if (detailInfo.isChecked())
			{
				try
				{
					GlStateManager.enableRescaleNormal();
					RenderHelper.enableGUIStandardItemLighting();
					itemRender.renderItemIntoGUI(stack, width / 2 - 100, par3 - 1);
					RenderHelper.disableStandardItemLighting();
					GlStateManager.disableRescaleNormal();
				}
				catch (Throwable e) {}
			}
		}

		@Override
		protected void elementClicked(int slot, boolean flag, int mouseX, int mouseY)
		{
			BlockMeta blockMeta = contents.get(slot, null);

			if (blockMeta != null && (clickFlag = !clickFlag == true) && !selected.remove(blockMeta))
			{
				if (nameField != null || metaField != null)
				{
					selected.clear();
				}

				selected.add(blockMeta);
			}
		}

		@Override
		protected boolean isSelected(int slot)
		{
			BlockMeta blockMeta = contents.get(slot, null);

			return blockMeta != null && selected.contains(blockMeta);
		}

		protected void setFilter(String filter)
		{
			CaveUtils.getPool().execute(() ->
			{
				List<BlockMeta> result;

				if (Strings.isNullOrEmpty(filter))
				{
					result = entries;
				}
				else if (filter.equals("selected"))
				{
					result = Lists.newArrayList(selected);
				}
				else
				{
					if (!filterCache.containsKey(filter))
					{
						filterCache.put(filter, Lists.newArrayList(Collections2.filter(entries, e -> filterMatch(e, filter))));
					}

					result = filterCache.get(filter);
				}

				if (!contents.equals(result))
				{
					contents.clear();
					contents.addAll(result);
				}
			});
		}

		protected boolean filterMatch(BlockMeta blockMeta, String filter)
		{
			return CaveFilters.blockFilter(blockMeta, filter);
		}
	}
}