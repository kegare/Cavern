package cavern.world;

import cavern.block.CaveBlocks;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterRuinsCavern extends Teleporter
{
	public TeleporterRuinsCavern(WorldServer worldServer)
	{
		super(worldServer);
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw)
	{
		if (makePortal(entity))
		{
			entity.timeUntilPortal = entity.getPortalCooldown();

			entity.setLocationAndAngles(21.5D, 80.0D, -10.0D, 90.0F, 10.0F);
		}
		else
		{
			entity.setLocationAndAngles(0.5D, 80.0D, 0.5D, 90.0F, 10.0F);
		}

		if (entity instanceof EntityLivingBase)
		{
			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 25, 0, false, false));
		}
	}

	@Override
	public boolean placeInExistingPortal(Entity entity, float rotationYaw)
	{
		return false;
	}

	@Override
	public boolean makePortal(Entity entity)
	{
		IBlockState state = Blocks.MOSSY_COBBLESTONE.getDefaultState();

		for (int i = 0; i <= 3; ++i)
		{
			world.setBlockState(new BlockPos(21, 79, -12 + i), state, 2);
			world.setBlockState(new BlockPos(21, 83, -12 + i), state, 2);
		}

		IBlockState portal = CaveBlocks.RUINS_CAVERN_PORTAL.getDefaultState().withProperty(BlockPortal.AXIS, EnumFacing.Axis.Z);

		for (int i = 0; i <= 2; ++i)
		{
			world.setBlockState(new BlockPos(21, 80 + i, -9), state, 2);
			world.setBlockState(new BlockPos(21, 80 + i, -10), portal, 2);
			world.setBlockState(new BlockPos(21, 80 + i, -11), portal, 2);
			world.setBlockState(new BlockPos(21, 80 + i, -12), state, 2);
		}

		return true;
	}

	@Override
	public void removeStalePortalLocations(long worldTime) {}
}