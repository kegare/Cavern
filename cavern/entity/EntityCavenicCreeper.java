package cavern.entity;

import cavern.api.CavernAPI;
import cavern.api.ICavenicMob;
import cavern.core.CaveAchievements;
import cavern.item.ItemCave;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class EntityCavenicCreeper extends EntityCreeper implements ICavenicMob
{
	protected int fuseTime = 15;
	protected int explosionRadius = 5;

	public EntityCavenicCreeper(World world)
	{
		super(world);
		this.experienceValue = 13;
		this.applyCustomValues();
	}

	protected void applyCustomValues()
	{
		ObfuscationReflectionHelper.setPrivateValue(EntityCreeper.class, this, fuseTime, "fuseTime", "field_82225_f");
		ObfuscationReflectionHelper.setPrivateValue(EntityCreeper.class, this, explosionRadius, "explosionRadius", "field_82226_g");
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();

		applyMobAttributes();
	}

	protected void applyMobAttributes()
	{
		getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
		getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(0.85D);
		getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
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
		return CaveAchievements.CAVENIC_CREEPER;
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
		return CavernAPI.dimension.isEntityInCavenia(this) ? 2 : 1;
	}

	@Override
	public int getHuntingPoint()
	{
		return 3;
	}
}