package cavern.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.world.World;

public class EntityCavenicArrow extends EntityTippedArrow
{
	public EntityCavenicArrow(World worldIn)
	{
		super(worldIn);
	}

	public EntityCavenicArrow(World worldIn, double x, double y, double z)
	{
		super(worldIn, x, y, z);
	}

	public EntityCavenicArrow(World worldIn, EntityLivingBase shooter)
	{
		super(worldIn, shooter);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (inGround || timeInGround > 0)
		{
			setDead();
		}
	}
}