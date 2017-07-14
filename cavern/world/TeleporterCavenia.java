package cavern.world;

import cavern.api.IPortalCache;
import cavern.stats.PortalCache;
import cavern.util.CaveUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterCavenia extends Teleporter
{
	public TeleporterCavenia(WorldServer worldServer)
	{
		super(worldServer);
	}

	public ResourceLocation getKey()
	{
		return CaveUtils.getKey("cavenia");
	}

	@Override
	public void placeInPortal(Entity entity, float rotationYaw)
	{
		if (attemptToLastPos(entity) || attemptRandomly(entity))
		{
			entity.motionX = 0.0D;
			entity.motionY = 0.0D;
			entity.motionZ = 0.0D;

			if (entity instanceof EntityLivingBase)
			{
				((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 25, 0, false, false));
			}
		}
	}

	protected boolean attemptToLastPos(Entity entity)
	{
		IPortalCache cache = PortalCache.get(entity);
		ResourceLocation key = getKey();
		DimensionType type = world.provider.getDimensionType();

		if (cache.hasLastPos(key, type))
		{
			BlockPos pos = cache.getLastPos(key, type);

			if (world.getBlockState(pos.down()).isBlockNormalCube() && world.isAirBlock(pos.up(2)))
			{
				CaveUtils.setPositionAndUpdate(entity, pos);

				return true;
			}

			cache.setLastPos(key, type, null);
		}

		return false;
	}

	protected boolean attemptRandomly(Entity entity)
	{
		int count = 0;

		while (++count < 50)
		{
			int x = MathHelper.floor(entity.posX) + random.nextInt(64) - 32;
			int z = MathHelper.floor(entity.posZ) + random.nextInt(64) - 32;
			BlockPos pos = new BlockPos(x, random.nextInt(20) + 11, z);

			while (pos.getY() > 1 && world.isAirBlock(pos))
			{
				pos = pos.down();
			}

			while (pos.getY() < world.getActualHeight() - 3 && !world.isAirBlock(pos))
			{
				pos = pos.up();
			}

			if (world.getBlockState(pos.down()).isBlockNormalCube() && world.isAirBlock(pos.up(2)))
			{
				CaveUtils.setPositionAndUpdate(entity, pos);

				return true;
			}
		}

		return false;
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
}