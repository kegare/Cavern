package cavern.block;

import cavern.api.CavernAPI;
import cavern.client.gui.GuiRegeneration;
import cavern.config.CavelandConfig;
import cavern.world.CaveType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPortalCaveland extends BlockPortalCavern
{
	public BlockPortalCaveland()
	{
		super();
		this.setUnlocalizedName("portal.caveland");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			FMLClientHandler.instance().showGuiScreen(new GuiRegeneration(false, false, true));
		}

		return true;
	}

	@Override
	public int getType()
	{
		return CaveType.CAVELAND;
	}

	@Override
	public int getDimension()
	{
		return CavelandConfig.dimensionId;
	}

	@Override
	public boolean isEntityInCave(Entity entity)
	{
		return CavernAPI.dimension.isEntityInCaveland(entity);
	}
}