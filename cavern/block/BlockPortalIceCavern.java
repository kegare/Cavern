package cavern.block;

import cavern.api.CavernAPI;
import cavern.client.gui.GuiRegeneration;
import cavern.world.CaveType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPortalIceCavern extends BlockPortalCavern
{
	public BlockPortalIceCavern()
	{
		super();
		this.setUnlocalizedName("portal.iceCavern");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void displayGui(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side)
	{
		GuiRegeneration regeneration = new GuiRegeneration();
		regeneration.iceCavern = true;

		FMLClientHandler.instance().showGuiScreen(regeneration);
	}

	@Override
	public DimensionType getDimension()
	{
		return CaveType.DIM_ICE_CAVERN;
	}

	@Override
	public boolean isEntityInCave(Entity entity)
	{
		return CavernAPI.dimension.isEntityInIceCavern(entity);
	}

	@Override
	public boolean isDimensionDisabled()
	{
		return CavernAPI.dimension.isIceCavernDisabled();
	}

	@Override
	public boolean isTriggerItem(ItemStack stack)
	{
		return !stack.isEmpty() && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).getBlock() == Blocks.PACKED_ICE;
	}
}