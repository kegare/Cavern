package cavern.entity.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.util.EnumHand;

public class EntityAIAttackCavenicBow extends EntityAIAttackRangedBow
{
	private final EntitySkeleton entity;
	private final double moveSpeedAmp;
	private final float maxAttackDistance;
	private int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;

	public EntityAIAttackCavenicBow(EntitySkeleton skeleton, double speedAmplifier, int delay, float maxDistance)
	{
		super(skeleton, speedAmplifier, delay, maxDistance);
		this.entity = skeleton;
		this.moveSpeedAmp = speedAmplifier;
		this.maxAttackDistance = maxDistance * maxDistance;
		this.setMutexBits(3);
	}

	@Override
	public void setAttackCooldown(int time) {}

	@Override
	public boolean shouldExecute()
	{
		return entity.getAttackTarget() == null ? false : isBowInMainhand();
	}

	@Override
	protected boolean isBowInMainhand()
	{
		return entity.getHeldItemMainhand() != null && entity.getHeldItemMainhand().getItem() == Items.BOW;
	}

	@Override
	public boolean continueExecuting()
	{
		return (shouldExecute() || !entity.getNavigator().noPath()) && isBowInMainhand();
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();
		entity.setSwingingArms(true);
	}

	@Override
	public void resetTask()
	{
		super.resetTask();
		entity.setSwingingArms(false);
		seeTime = 0;
		entity.resetActiveHand();
	}

	@Override
	public void updateTask()
	{
		EntityLivingBase living = entity.getAttackTarget();

		if (living != null)
		{
			double dist = entity.getDistanceSq(living.posX, living.getEntityBoundingBox().minY, living.posZ);
			boolean canSee = entity.getEntitySenses().canSee(living);
			boolean seeing = seeTime > 0;

			if (canSee != seeing)
			{
				seeTime = 0;
			}

			if (canSee)
			{
				++seeTime;
			}
			else
			{
				--seeTime;
			}

			if (dist <= maxAttackDistance && seeTime >= 15)
			{
				entity.getNavigator().clearPathEntity();
				++strafingTime;
			}
			else
			{
				entity.getNavigator().tryMoveToEntityLiving(living, moveSpeedAmp);
				strafingTime = -1;
			}

			if (strafingTime >= 5)
			{
				if (entity.getRNG().nextFloat() < 0.3D)
				{
					strafingClockwise = !strafingClockwise;
				}

				if (entity.getRNG().nextFloat() < 0.3D)
				{
					strafingBackwards = !strafingBackwards;
				}

				strafingTime = 0;
			}

			if (strafingTime > -1)
			{
				if (dist > maxAttackDistance * 0.75F)
				{
					strafingBackwards = false;
				}
				else if (dist < maxAttackDistance * 0.25F)
				{
					strafingBackwards = true;
				}

				entity.getMoveHelper().strafe(strafingBackwards ? -0.5F : 0.5F, strafingClockwise ? 0.5F : -0.5F);
				entity.faceEntity(living, 30.0F, 30.0F);
			}
			else
			{
				entity.getLookHelper().setLookPositionWithEntity(living, 30.0F, 30.0F);
			}

			if (entity.isHandActive())
			{
				if (!canSee && seeTime < -20)
				{
					entity.resetActiveHand();
				}
				else if (canSee)
				{
					entity.attackEntityWithRangedAttack(living, ItemBow.getArrowVelocity(5));
				}
			}
			else if (seeTime >= -20)
			{
				entity.setActiveHand(EnumHand.MAIN_HAND);
			}
		}
	}
}