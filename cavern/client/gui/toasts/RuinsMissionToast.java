package cavern.client.gui.toasts;

import cavern.item.ItemCave;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RuinsMissionToast extends CaveToast
{
	@Override
	protected int getTextureIndex()
	{
		return 0;
	}

	@Override
	protected ITextComponent getTitle()
	{
		return new TextComponentTranslation("cavern.toast.mission.title");
	}

	@Override
	protected int getTitleColor()
	{
		return 0xCDCDCD;
	}

	@Override
	protected ITextComponent getDescription()
	{
		return new TextComponentTranslation("cavern.toast.mission.ruins");
	}

	@Override
	protected ItemStack getIconItemStack(long delta)
	{
		return ItemCave.EnumType.MINER_ORB.getItemStack();
	}
}