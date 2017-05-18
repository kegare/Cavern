package cavern.world;

import cavern.stats.IPortalCache;
import cavern.stats.PortalCache;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterCavenia extends Teleporter
{
	private final WorldServer world;

	public TeleporterCavenia(WorldServer worldServer)
	{
		super(worldServer);
		this.world = worldServer;
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw)
	{
		IPortalCache cache = PortalCache.get(entity);

		if (cache.hasLastPos(10, entity.dimension))
		{
			setLocationAndAngles(entity, cache.getLastPos(10, entity.dimension));
		}

		int x = MathHelper.floor(entity.posX);
		int y = MathHelper.floor(entity.posY) - 1;
		int z = MathHelper.floor(entity.posZ);
		boolean flag = false;

		for (int k = -1; k < 2; ++k)
		{
			boolean isFloor = k < 0;
			int py = y + k;
			BlockPos pos = new BlockPos(x, py, z);

			if (isFloor)
			{
				flag = world.getBlockState(pos).isBlockNormalCube();
			}
			else
			{
				flag = world.isAirBlock(pos);
			}
		}

		if (!flag)
		{
			y = MathHelper.floor(world.getActualHeight() * 0.25D);

			BlockPos pos = new BlockPos(x, y, z);

			while (world.isAirBlock(pos))
			{
				pos = pos.down();
			}

			for (int i = -2; i <= 2; ++i)
			{
				for (int j = -2; j <= 2; ++j)
				{
					for (int k = -1; k < 3; ++k)
					{
						int px = pos.getX() + j * 1 + i * 0;
						int py = pos.getY() + k;
						int pz = pos.getZ() + j * 0 - i * 1;
						boolean isFloor = k < 0;

						world.setBlockState(new BlockPos(px, py, pz), isFloor ? Blocks.MOSSY_COBBLESTONE.getDefaultState() : Blocks.AIR.getDefaultState());
					}
				}
			}

			setLocationAndAngles(entity, x, y, z);

			entity.motionX = 0.0D;
			entity.motionY = 0.0D;
			entity.motionZ = 0.0D;
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
		return false;
	}

	@Override
	public void removeStalePortalLocations(long worldTime) {}

	public void setLocationAndAngles(Entity entity, double posX, double posY, double posZ)
	{
		if (entity instanceof EntityPlayerMP)
		{
			((EntityPlayerMP)entity).connection.setPlayerLocation(posX, posY, posZ, entity.rotationYaw, entity.rotationPitch);
		}
		else
		{
			entity.setLocationAndAngles(posX, posY, posZ, entity.rotationYaw, entity.rotationPitch);
		}
	}

	public void setLocationAndAngles(Entity entity, BlockPos pos)
	{
		if (pos != null)
		{
			setLocationAndAngles(entity, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
		}
	}
}