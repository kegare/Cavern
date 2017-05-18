package cavern.entity;

import cavern.api.CavernAPI;
import cavern.api.ICavenicMob;
import cavern.core.CaveAchievements;
import cavern.item.ItemCave;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityCavenicZombie extends EntityZombie implements ICavenicMob
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

		applyMobAttributes();
	}

	protected void applyMobAttributes()
	{
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(50.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(2.5D);
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(5.0D);
		getEntityAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source)
	{
		super.dropLoot(wasRecentlyHit, lootingModifier, source);

		if (rand.nextInt(8) == 0)
		{
			entityDropItem(ItemCave.EnumType.CAVENIC_ORB.getItemStack(), 0.5F);
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
			((EntityPlayer)entity).addStat(getKillAchievement());
		}
	}

	protected Achievement getKillAchievement()
	{
		return CaveAchievements.CAVENIC_ZOMBIE;
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
		return CavernAPI.dimension.isEntityInCavenia(this) ? 5 : 1;
	}

	@Override
	public int getHuntingPoint()
	{
		return 2;
	}
}