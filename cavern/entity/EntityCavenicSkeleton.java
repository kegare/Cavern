package cavern.entity;

import cavern.api.CavernAPI;
import cavern.core.CaveAchievements;
import cavern.entity.ai.EntityAIAttackCavenicBow;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class EntityCavenicSkeleton extends EntitySkeleton
{
	protected EntityAIAttackRangedBow aiArrowAttack;

	public EntityCavenicSkeleton(World world)
	{
		super(world);
		this.experienceValue = 13;
		this.setSize(0.68F, 2.0F);
		this.initCustomValues();
		this.applyCustomValues();
	}

	protected void initCustomValues()
	{
		aiArrowAttack = new EntityAIAttackCavenicBow(this, 0.975D, 5.0F);
	}

	protected void applyCustomValues()
	{
		ObfuscationReflectionHelper.setPrivateValue(AbstractSkeleton.class, this, aiArrowAttack, "aiArrowAttack", "field_85037_d");
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.85D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
	}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier)
	{
		super.dropFewItems(wasRecentlyHit, lootingModifier);

		if (rand.nextInt(5) == 0)
		{
			entityDropItem(new ItemStack(Items.DIAMOND), 0.5F);
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

		Entity entity = cause.getEntity();

		if (entity == null)
		{
			entity = cause.getSourceOfDamage();
		}

		if (entity != null && entity instanceof EntityPlayer)
		{
			((EntityPlayer)entity).addStat(CaveAchievements.CAVENIC_SKELETON);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage)
	{
		if (source == DamageSource.FALL)
		{
			damage *= 0.35F;
		}

		return !source.isFireDamage() && source.getEntity() != this && super.attackEntityFrom(source, damage);
	}

	@Override
	public boolean getCanSpawnHere()
	{
		return CavernAPI.dimension.isEntityInCaves(this) && super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk()
	{
		return 1;
	}
}