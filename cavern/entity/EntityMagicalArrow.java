package cavern.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.world.World;

public class EntityMagicalArrow extends EntityTippedArrow
{
	public EntityMagicalArrow(World world)
	{
		super(world);
	}

	public EntityMagicalArrow(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	public EntityMagicalArrow(World world, EntityLivingBase shooter)
	{
		super(world, shooter);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (timeInGround > 5)
		{
			setDead();
		}
	}
}