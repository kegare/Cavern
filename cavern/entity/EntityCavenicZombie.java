package cavern.entity;

import cavern.api.CavernAPI;
import cavern.core.CaveAchievements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCavenicZombie extends EntityZombie
{
	public EntityCavenicZombie(World world)
	{
		super(world);
		this.experienceValue = 12;
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		getEntityAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.5D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
	}

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
			((EntityPlayer)entity).addStat(CaveAchievements.CAVENIC_ZOMBIE);
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