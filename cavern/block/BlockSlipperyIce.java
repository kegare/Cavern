package cavern.block;

import cavern.core.CaveAchievements;
import cavern.core.Cavern;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity)
	{
		super.onEntityWalk(world, pos, entity);

		if (!world.isRemote && entity instanceof EntityPlayer)
		{
			((EntityPlayer)entity).addStat(CaveAchievements.SLIP_ICE);
		}
	}
}