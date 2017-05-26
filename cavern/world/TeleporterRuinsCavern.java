package cavern.world;

import cavern.util.CaveUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
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
		entity.rotationYaw = 90.0F;
		entity.rotationPitch = 10.0F;

		CaveUtils.setLocationAndAngles(entity, 0.5D, 80.0D, 0.5D);

		if (entity instanceof EntityLivingBase)
		{
			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 0, false, false));
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
}