package cavern.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public class ItemBlockPerverted extends ItemBlock
{
	private final Block basedBlock;

	public ItemBlockPerverted(Block block, Block base)
	{
		super(block);
		this.basedBlock = base;
		this.setRegistryName(block.getRegistryName());
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		String name = Item.getItemFromBlock(basedBlock).getItemStackDisplayName(stack);

		return ("" + I18n.translateToLocal("tile.perverted.name")).trim() + " " + name;
	}
}