package cavern.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemPortalCave extends ItemBlock
{
	public ItemPortalCave(Block block)
	{
		super(block);
		this.setRegistryName(block.getRegistryName());
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		BlockPos blockpos = pos.offset(side);

		if (((BlockPortal)block).trySpawnPortal(world, blockpos))
		{
			world.playSound(null, blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D, block.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 2.0F);

			if (!player.capabilities.isCreativeMode)
			{
				--stack.stackSize;
			}

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}
}