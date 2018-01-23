package cavern.block;

import cavern.core.Cavern;
import cavern.util.CaveUtils;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSlipperyIce extends BlockPackedIce
{
	public BlockSlipperyIce()
	{
		super();
		this.setUnlocalizedName("slipperyIce");
		this.setSoundType(SoundType.GLASS);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public void onEntityWalk(World world, BlockPos pos, Entity entity)
	{
		super.onEntityWalk(world, pos, entity);

		if (!world.isRemote && entity.ticksExisted % 20 == 0 && entity instanceof EntityPlayer)
		{
			CaveUtils.grantAdvancement((EntityPlayer)entity, "slip_ice");
		}
	}

	@Override
	public float getSlipperiness(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity)
	{
		return 1.05F;
	}
}