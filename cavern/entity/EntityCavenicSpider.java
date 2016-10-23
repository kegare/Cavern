package cavern.entity;

import cavern.api.CavernAPI;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityCavenicSpider extends EntitySpider
{
	public EntityCavenicSpider(World world)
	{
		super(world);
		this.experienceValue = 10;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.5D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(2.0D);
	}

	@Override
	protected void playStepSound(BlockPos pos, Block block) {}

	@Override
	protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier)
	{
		super.dropFewItems(wasRecentlyHit, lootingModifier);

		if (rand.nextInt(10) == 0)
		{
			entityDropItem(new ItemStack(Items.DIAMOND), 0.5F);
		}
	}

	@Override
	public boolean attackEntityAsMob(Entity entity)
	{
		if (super.attackEntityAsMob(entity))
		{
			if (entity instanceof EntityLivingBase)
			{
				byte sec = 3;

				if (worldObj.getDifficulty() == EnumDifficulty.NORMAL)
				{
					sec = 7;
				}
				else if (worldObj.getDifficulty() == EnumDifficulty.HARD)
				{
					sec = 14;
				}

				if (sec > 0)
				{
					((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, sec * 20));
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage)
	{
		return !source.isFireDamage() && source != DamageSource.fall && super.attackEntityFrom(source, damage);
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