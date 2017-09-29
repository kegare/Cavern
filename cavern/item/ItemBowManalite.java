package cavern.item;

import javax.annotation.Nullable;

import cavern.api.IMagicianStats;
import cavern.client.particle.ParticleMagicSpell;
import cavern.core.CaveSounds;
import cavern.core.Cavern;
import cavern.entity.EntityMagicalArrow;
import cavern.stats.MagicianStats;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBowManalite extends ItemBow
{
	public ItemBowManalite()
	{
		super();
		this.setUnlocalizedName("bowManalite");
		this.setCreativeTab(Cavern.TAB_CAVERN);
		this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
			{
				if (entity == null || entity.getActiveItemStack().getItem() != ItemBowManalite.this)
				{
					return 0.0F;
				}

				return (stack.getMaxItemUseDuration() - entity.getItemInUseCount()) / 20.0F;
			}
		});
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count)
	{
		if (!(entity instanceof EntityPlayer))
		{
			return;
		}

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
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft)
	{
		if (!(entityLiving instanceof EntityPlayer))
		{
			return;
		}

		EntityPlayer player = (EntityPlayer)entityLiving;
		IMagicianStats stats = MagicianStats.get(player);
		boolean hasMP = stats.getMP() >= 5;
		int i = getMaxItemUseDuration(stack) - timeLeft;
		i = ForgeEventFactory.onArrowLoose(stack, world, player, i, hasMP);

		if (i < 0 || !player.capabilities.isCreativeMode && !hasMP)
		{
			return;
		}

		float f = getArrowVelocity(i);

		if (f < 0.1D)
		{
			return;
		}

		if (!world.isRemote)
		{
			EntityArrow entityArrow = new EntityMagicalArrow(world, player);

			entityArrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, f * 2.85F, 1.0F);

			if (f >= 1.0D)
			{
				entityArrow.setIsCritical(true);
			}

			int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);

			if (power > 0)
			{
				entityArrow.setDamage(entityArrow.getDamage() + power * 0.5D + 0.5D);
			}

			int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);

			if (punch > 0)
			{
				entityArrow.setKnockbackStrength(punch);
			}

			if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0)
			{
				entityArrow.setFire(100);
			}

			stack.damageItem(1, player);

			entityArrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;

			world.spawnEntity(entityArrow);
		}

		world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
			1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
		world.playSound(null, player.posX, player.posY, player.posZ, CaveSounds.MAGIC_SUCCESS_SHORT, SoundCategory.PLAYERS, 0.15F, 1.0F);

		if (!player.capabilities.isCreativeMode)
		{
			stats.addMP(-5);
		}

		player.addStat(StatList.getObjectUseStats(this));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack held = player.getHeldItem(hand);
		boolean hasMP = MagicianStats.get(player).getMP() >= 5;
		ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(held, world, player, hand, hasMP);

		if (ret != null)
		{
			return ret;
		}

		if (!player.capabilities.isCreativeMode && !hasMP)
		{
			return hasMP ? new ActionResult<>(EnumActionResult.PASS, held) : new ActionResult<>(EnumActionResult.FAIL, held);
		}
		else
		{
			player.setActiveHand(hand);

			return new ActionResult<>(EnumActionResult.SUCCESS, held);
		}
	}

	@SideOnly(Side.CLIENT)
	protected boolean isMagicProcessing(ItemStack stack)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.player != null && mc.player.isHandActive() && mc.player.getHeldItem(mc.player.getActiveHand()) == stack)
		{
			if (mc.player.capabilities.isCreativeMode || MagicianStats.get(mc.player).getMP() >= 5)
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