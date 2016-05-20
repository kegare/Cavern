package cavern.item;

import cavern.block.BlockCave.EnumType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockCave extends ItemBlock
{
	public ItemBlockCave(Block block)
	{
		super(block);
		this.setRegistryName(block.getRegistryName());
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage)
	{
		return damage;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "tile." + EnumType.byMetadata(stack.getItemDamage()).getUnlocalizedName();
	}
}