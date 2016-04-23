package cavern.block;

import cavern.api.CavernAPI;
import cavern.client.gui.GuiRegeneration;
import cavern.config.AquaCavernConfig;
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

public class BlockPortalAquaCavern extends BlockPortalCavern
{
	public BlockPortalAquaCavern()
	{
		super();
		this.setUnlocalizedName("portal.aquaCavern");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (world.isRemote)
		{
			FMLClientHandler.instance().showGuiScreen(new GuiRegeneration(false, true, false));
		}

		return true;
	}

	@Override
	public int getType()
	{
		return CaveType.AQUA_CAVERN;
	}

	@Override
	public int getDimension()
	{
		return AquaCavernConfig.dimensionId;
	}

	@Override
	public boolean isEntityInCave(Entity entity)
	{
		return CavernAPI.dimension.isEntityInAquaCavern(entity);
	}
}