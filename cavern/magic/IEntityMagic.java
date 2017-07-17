package cavern.magic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public interface IEntityMagic extends IMagic
{
	public double getMagicRange(EntityPlayer player, World world, ItemStack stack, EnumHand hand);

	public boolean isTargetEntity(EntityPlayer player, Entity entity);

	public boolean execute(EntityPlayer player, Entity entity, World world, ItemStack stack, EnumHand hand);

	public default Class<? extends Entity> getEntityClass()
	{
		return EntityLivingBase.class;
	}

	@Override
	public default boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		double range = getMagicRange(player, world, stack, hand);

		if (range <= 0.0D)
		{
			return false;
		}

		int count = 0;

		for (Entity entity : world.getEntitiesWithinAABB(getEntityClass(), player.getEntityBoundingBox().grow(range)))
		{
			if (isTargetEntity(player, entity) && execute(player, entity, world, stack, hand))
			{
				++count;
			}
		}

		return count > 0;
	}
}