package cavern.block;

import cavern.core.Cavern;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.SoundType;

public class BlockSlipperyIce extends BlockPackedIce
{
	public BlockSlipperyIce()
	{
		super();
		this.slipperiness = 1.05F;
		this.setUnlocalizedName("slipperyIce");
		this.setSoundType(SoundType.GLASS);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}
}