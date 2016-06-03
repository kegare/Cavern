package cavern.core;

import cavern.block.CaveBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabCavern extends CreativeTabs
{
	public CreativeTabCavern()
	{
		super(Cavern.MODID);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Item getTabIconItem()
	{
		return Item.getItemFromBlock(CaveBlocks.cavern_portal);
	}
}