package cavern.block;

import cavern.api.CavernAPI;
import cavern.client.gui.GuiRegeneration;
import cavern.util.CaveUtils;
import cavern.world.CaveType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class BlockPortalHugeCavern extends BlockPortalCavern
{
	public BlockPortalHugeCavern()
	{
		super();
		this.setUnlocalizedName("portal.hugeCavern");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void displayGui(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side)
	{
		GuiRegeneration regeneration = new GuiRegeneration();
		regeneration.hugeCavern = true;

		FMLClientHandler.instance().showGuiScreen(regeneration);
	}

	@Override
	public DimensionType getDimension()
	{
		return CaveType.DIM_HUGE_CAVERN;
	}

	@Override
	public boolean isEntityInCave(Entity entity)
	{
		return CavernAPI.dimension.isEntityInHugeCavern(entity);
	}

	@Override
	public boolean isDimensionDisabled()
	{
		return CavernAPI.dimension.isHugeCavernDisabled();
	}

	@Override
	public boolean isTriggerItem(ItemStack stack)
	{
		if (!stack.isEmpty() && stack.getItem() == Items.DIAMOND)
		{
			return true;
		}

		for (ItemStack dictStack : OreDictionary.getOres("gemDiamond", false))
		{
			if (CaveUtils.isItemEqual(stack, dictStack))
			{
				return true;
			}
		}

		return false;
	}
}