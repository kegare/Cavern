package cavern.block;

import cavern.api.CavernAPI;
import cavern.client.gui.GuiRegeneration;
import cavern.config.RuinsCavernConfig;
import cavern.world.CaveType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPortalRuinsCavern extends BlockPortalCavern
{
	public BlockPortalRuinsCavern()
	{
		super();
		this.setUnlocalizedName("portal.ruinsCavern");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void displayGui(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side)
	{
		FMLClientHandler.instance().showGuiScreen(new GuiRegeneration().setRuinsCavern());
	}

	@Override
	public int getType()
	{
		return CaveType.RUINS_CAVERN;
	}

	@Override
	public int getDimension()
	{
		return RuinsCavernConfig.dimensionId;
	}

	@Override
	public boolean isEntityInCave(Entity entity)
	{
		return CavernAPI.dimension.isEntityInRuinsCavern(entity);
	}

	@Override
	public boolean isDimensionDisabled()
	{
		return CavernAPI.dimension.isRuinsCavernDisabled();
	}
}