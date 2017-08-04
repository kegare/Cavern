package cavern.magic;

import javax.annotation.Nullable;

import cavern.api.ISummonMob;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.ExplosionEffectMessage;
import cavern.world.CustomExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MagicalExplosion extends CustomExplosion
{
	public MagicalExplosion(World world, Entity entity, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain)
	{
		super(world, entity, x, y, z, size, flaming, damagesTerrain);
	}

	@Override
	public boolean canExplodeEntity(Entity entity)
	{
		if (!super.canExplodeEntity(entity))
		{
			return false;
		}

		if (entity instanceof EntityItem)
		{
			return false;
		}

		if (entity instanceof EntityPlayer)
		{
			return false;
		}

		if (entity instanceof ISummonMob)
		{
			return false;
		}

		if (entity instanceof IEntityOwnable && ((IEntityOwnable)entity).getOwner() != null)
		{
			return false;
		}

		return true;
	}

	@Override
	protected int getExplosionAttackDamage(Entity entity, int damage)
	{
		entity.hurtResistantTime = 0;

		if (entity.isBurning())
		{
			return MathHelper.ceil(damage * 2.0F);
		}

		if (!entity.onGround || entity.isAirBorne)
		{
			return MathHelper.ceil(damage * 1.75F);
		}

		if (entity instanceof IMob)
		{
			return MathHelper.ceil(damage * 1.45F);
		}

		return damage;
	}

	public static MagicalExplosion createExplosion(World world, @Nullable Entity entity, double x, double y, double z, float strength, boolean damagesTerrain)
	{
		return newExplosion(world, entity, x, y, z, strength, false, damagesTerrain);
	}

	public static MagicalExplosion newExplosion(World world, @Nullable Entity entity, double x, double y, double z, float strength, boolean flaming, boolean damagesTerrain)
	{
		if (FMLCommonHandler.instance().getSide().isServer())
		{
			damagesTerrain = false;
		}

		MagicalExplosion explosion = new MagicalExplosion(world, entity, x, y, z, strength, flaming, damagesTerrain);

		if (ForgeEventFactory.onExplosionStart(world, explosion))
		{
			return explosion;
		}

		explosion.doExplosionA();
		explosion.doExplosionB(false);

		if (!damagesTerrain)
		{
			explosion.clearAffectedBlockPositions();
		}

		explosion.doExplosionEntities();

		for (EntityPlayer player : world.playerEntities)
		{
			if (player instanceof EntityPlayerMP && player.getDistanceSq(x, y, z) < 4096.0D)
			{
				CaveNetworkRegistry.sendTo(new ExplosionEffectMessage((float)x, (float)y, (float)z, strength, explosion.getAffectedBlockPositions()), (EntityPlayerMP)player);
			}
		}

		return explosion;
	}
}