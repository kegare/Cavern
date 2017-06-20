package cavern.entity;

import cavern.api.CavernAPI;
import cavern.api.ICavenicMob;
import cavern.core.CaveAchievements;
import cavern.entity.ai.EntityAIAttackCavenicBow;
import cavern.item.CaveItems;
import cavern.item.ItemCave;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityCavenicSkeleton extends EntitySkeleton implements ICavenicMob
{
	protected EntityAIAttackRangedBow aiArrowAttack;
	protected EntityAIAttackMelee aiAttackOnCollide;

	public EntityCavenicSkeleton(World world)
	{
		super(world);
		this.experienceValue = 13;
		this.setSize(0.68F, 2.0F);
	}

	protected void initCustomAI()
	{
		aiArrowAttack = new EntityAIAttackCavenicBow(this, 0.975D, 5.0F, 4);
		aiAttackOnCollide = new EntityAIAttackMelee(this, 1.25D, false)
		{
			@Override
			public void resetTask()
			{
				super.resetTask();

				EntityCavenicSkeleton.this.setSwingingArms(false);
			}

			@Override
			public void startExecuting()
			{
				super.startExecuting();

				EntityCavenicSkeleton.this.setSwingingArms(true);
			}
		};
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		applyMobAttributes();
	}

	protected void applyMobAttributes()
	{
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
	}

	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty)
	{
		super.setEquipmentBasedOnDifficulty(difficulty);

		if (rand.nextDouble() < 0.45D)
		{
			setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(CaveItems.CAVENIC_BOW));
		}
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source)
	{
		super.dropLoot(wasRecentlyHit, lootingModifier, source);

		if (rand.nextInt(5) == 0)
		{
			entityDropItem(ItemCave.EnumType.CAVENIC_ORB.getItemStack(), 0.5F);
		}
	}

	@Override
	public void setCombatTask()
	{
		if (aiArrowAttack == null || aiAttackOnCollide == null)
		{
			initCustomAI();
		}

		if (world != null && !world.isRemote)
		{
			tasks.removeTask(aiAttackOnCollide);
			tasks.removeTask(aiArrowAttack);

			if (getHeldItemMainhand().getItem() instanceof ItemBow)
			{
				tasks.addTask(4, aiArrowAttack);
			}
			else
			{
				tasks.addTask(4, aiAttackOnCollide);
			}
		}
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float dist)
	{
		EntityArrow arrow = new EntityCavenicArrow(world, this);
		double d0 = target.posX - posX;
		double d1 = target.getEntityBoundingBox().minY + target.height / 3.0F - arrow.posY;
		double d2 = target.posZ - posZ;
		double d3 = MathHelper.sqrt(d0 * d0 + d2 * d2);
		arrow.setThrowableHeading(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, 14 - world.getDifficulty().getDifficultyId() * 4);
		int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, this);
		int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, this);
		arrow.setDamage(dist * 2.0F + rand.nextGaussian() * 0.25D + world.getDifficulty().getDifficultyId() * 0.11F);

		if (i > 0)
		{
			arrow.setDamage(arrow.getDamage() + i * 0.45D + 0.5D);
		}

		if (j > 0)
		{
			arrow.setKnockbackStrength(j);
		}

		boolean flag = isBurning() && rand.nextBoolean();
		flag = flag || EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, this) > 0;

		if (flag)
		{
			arrow.setFire(50);
		}

		playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (getRNG().nextFloat() * 0.4F + 0.8F));

		world.spawnEntity(arrow);
	}

	@Override
	public void onDeath(DamageSource cause)
	{
		super.onDeath(cause);

		Entity entity = cause.getTrueSource();

		if (entity == null)
		{
			entity = cause.getImmediateSource();
		}

		if (entity != null && entity instanceof EntityPlayer)
		{
			((EntityPlayer)entity).addStat(getKillAchievement());
		}
	}

	protected Achievement getKillAchievement()
	{
		return CaveAchievements.CAVENIC_SKELETON;
	}

	@Override
	public boolean isEntityInvulnerable(DamageSource source)
	{
		if (super.isEntityInvulnerable(source))
		{
			return true;
		}

		if (source.getTrueSource() == this)
		{
			return true;
		}

		if (source.getImmediateSource() == this)
		{
			return true;
		}

		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage)
	{
		if (source == DamageSource.FALL)
		{
			damage *= 0.35F;
		}

		return !source.isFireDamage() && super.attackEntityFrom(source, damage);
	}

	@Override
	public boolean getCanSpawnHere()
	{
		return CavernAPI.dimension.isEntityInCaves(this) && super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return CavernAPI.dimension.isEntityInCavenia(this) ? 4 : 1;
	}

	@Override
	public int getHuntingPoint()
	{
		return 3;
	}
}