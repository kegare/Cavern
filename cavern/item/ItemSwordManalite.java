package cavern.item;

import com.google.common.collect.Multimap;

import cavern.api.IMagicianStats;
import cavern.client.particle.ParticleMagicSpell;
import cavern.core.CaveSounds;
import cavern.stats.MagicianStats;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSwordManalite extends ItemSwordCave
{
	public ItemSwordManalite()
	{
		super(CaveItems.MANALITE, "swordManalite");
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

			if (player.capabilities.isCreativeMode || stats.getMP() >= 2)
			{
				target.hurtResistantTime = 0;
				target.attackEntityFrom(DamageSource.MAGIC, 3.0F);

				world.playSound(null, target.posX, target.posY + 0.85D, target.posZ, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK,
					SoundCategory.PLAYERS, 0.75F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.35F);
				world.playSound(null, player.posX, player.posY, player.posZ, CaveSounds.MAGIC_SUCCESS_SHORT, SoundCategory.PLAYERS, 0.15F, 1.0F);

				if (!player.capabilities.isCreativeMode)
				{
					stats.addMP(-2);
				}
			}
		}

		return super.hitEntity(stack, target, attacker);
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

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> map = super.getAttributeModifiers(slot, stack);

		if (slot == EntityEquipmentSlot.MAINHAND)
		{
			map.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
			map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -1.0000000953674316D, 0));
		}

		return map;
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