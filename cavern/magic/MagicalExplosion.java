package cavern.magic;

import javax.annotation.Nullable;

import cavern.api.ISummonMob;
import cavern.world.CustomExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

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

		return true;
	}

	public static MagicalExplosion createExplosion(World world, @Nullable Entity entity, double x, double y, double z, float strength, boolean damagesTerrain)
	{
		return newExplosion(world, entity, x, y, z, strength, false, damagesTerrain);
	}

	public static MagicalExplosion newExplosion(World world, @Nullable Entity entity, double x, double y, double z, float strength, boolean flaming, boolean damagesTerrain)
	{
		MagicalExplosion explosion = new MagicalExplosion(world, entity, x, y, z, strength, flaming, damagesTerrain);

		if (ForgeEventFactory.onExplosionStart(world, explosion))
		{
			return explosion;
		}

		explosion.doExplosionA();
		explosion.doExplosionB(true);

		if (!damagesTerrain)
		{
			explosion.clearAffectedBlockPositions();
		}

		for (EntityPlayer player : world.playerEntities)
		{
			if (player instanceof EntityPlayerMP && player.getDistanceSq(x, y, z) < 4096.0D)
			{
				((EntityPlayerMP)player).connection.sendPacket(new SPacketExplosion(x, y, z, strength, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(player)));
			}
		}

		return explosion;
	}
}