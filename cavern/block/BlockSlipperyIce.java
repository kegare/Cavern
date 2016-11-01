package cavern.block;

import cavern.core.CaveAchievements;
import cavern.core.Cavern;
import cavern.plugin.HaCPlugin;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IHeatTile;
import defeatedcrow.hac.api.climate.IHumidityTile;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.common.Optional.InterfaceList;

@InterfaceList
({
	@Interface(iface = "defeatedcrow.hac.api.climate.IHeatTile", modid = HaCPlugin.LIB_MODID, striprefs = true),
	@Interface(iface = "defeatedcrow.hac.api.climate.IHumidityTile", modid = HaCPlugin.LIB_MODID, striprefs = true)
})
public class BlockSlipperyIce extends BlockPackedIce implements IHeatTile, IHumidityTile
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

	@Override
	public DCHeatTier getHeatTier(World world, BlockPos to, BlockPos from)
	{
		return DCHeatTier.COLD;
	}

	@Override
	public DCHumidity getHumdiity(World world, BlockPos to, BlockPos from)
	{
		return DCHumidity.WET;
	}
}