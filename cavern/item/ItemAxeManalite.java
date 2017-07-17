package cavern.item;

import cavern.api.IMagicianStats;
import cavern.api.ISummonMob;
import cavern.client.particle.ParticleMagicSpell;
import cavern.core.CaveSounds;
import cavern.stats.MagicianStats;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAxeManalite extends ItemAxeCave
{
	public ItemAxeManalite()
	{
		super(CaveItems.MANALITE, 8.0F, -3.05F, "axeManalite");
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)
	{
		if (!(attacker instanceof EntityPlayer))
		{
			return false;
		}

		World world = target.world;

		if (!world.isRemote)
		{
			EntityPlayer player = (EntityPlayer)attacker;
			IMagicianStats stats = MagicianStats.get(player);

			if (player.capabilities.isCreativeMode || stats.getMP() >= 10)
			{
				for (EntityLivingBase entity : world.getEntitiesWithinAABB(EntityLivingBase.class, target.getEntityBoundingBox().grow(2.5D)))
				{
					if (!(entity instanceof IMob))
					{
						continue;
					}

					if (entity instanceof ISummonMob)
					{
						continue;
					}

					if (entity instanceof IEntityOwnable && ((IEntityOwnable)entity).getOwner() != null)
					{
						continue;
					}

					double dist = target.getDistanceSqToEntity(entity);
					Vec3d vec = getSmashVector(player, dist <= 2.0D, (itemRand.nextDouble() + 1.0D) * 1.15D, 0.1D);

					entity.attackEntityFrom(DamageSource.MAGIC, 5.0F);
					entity.addVelocity(vec.x, vec.y, vec.z);
				}

				world.playSound(null, target.posX, target.posY + 0.85D, target.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK,
					SoundCategory.PLAYERS, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 0.8F) + 0.25F);
				world.playSound(null, player.posX, player.posY, player.posZ, CaveSounds.MAGIC_SUCCESS_SHORT, SoundCategory.PLAYERS, 0.15F, 1.0F);

				if (!player.capabilities.isCreativeMode)
				{
					stats.addMP(-10);
				}
			}
		}

		return super.hitEntity(stack, target, attacker);
	}

	public Vec3d getSmashVector(Entity entity, boolean isCritical, double bashPow, double bashUpRatio)
	{
		double pow = bashPow;
		double upRatio = bashUpRatio * pow;

		if (isCritical)
		{
			upRatio *= 1.5D;
		}

		double vecX = -MathHelper.sin(entity.rotationYaw * 3.141593F / 180F) * (float)pow * 0.5F;
		double vecZ = MathHelper.cos(entity.rotationYaw * 3.141593F / 180F) * (float)pow * 0.5F;

		if (!isCritical)
		{
			double var = 1.0F - itemRand.nextFloat() * 0.35F;

			vecX *= var;
			upRatio *= var;
			vecZ *= var;
		}

		return new Vec3d(vecX, upRatio, vecZ);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		for (int i = 0; i < 2; ++i)
		{
			int var1 = itemRand.nextInt(2) * 2 - 1;
			int var2 = itemRand.nextInt(2) * 2 - 1;
			double ptX = entity.posX + 0.25D * var1;
			double ptY = entity.posY + 0.7D + itemRand.nextFloat();
			double ptZ = entity.posZ + 0.25D * var2;
			double motionX = itemRand.nextFloat() * 1.0F * var1;
			double motionY = (itemRand.nextFloat() - 0.25D) * 0.125D;
			double motionZ = itemRand.nextFloat() * 1.0F * var2;
			ParticleMagicSpell particle = new ParticleMagicSpell(entity.world, ptX, ptY, ptZ, motionX, motionY, motionZ);

			mc.effectRenderer.addEffect(particle);
		}

		return super.onEntitySwing(entity, stack);
	}

	@SideOnly(Side.CLIENT)
	protected boolean isMagicProcessing(ItemStack stack)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.player != null && mc.player.isSwingInProgress && mc.player.getHeldItem(mc.player.swingingHand) == stack)
		{
			if (mc.player.capabilities.isCreativeMode || MagicianStats.get(mc.player).getMP() >= 10)
			{
				return true;
			}
		}

		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		if (isMagicProcessing(stack))
		{
			return true;
		}

		return super.showDurabilityBar(stack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack)
	{
		if (isMagicProcessing(stack))
		{
			return 0x00A2D0;
		}

		return super.getRGBDurabilityForDisplay(stack);
	}
}