package cavern.item;

import javax.annotation.Nullable;

import cavern.core.Cavern;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBowIce extends ItemBow
{
	public ItemBowIce()
	{
		super();
		this.setUnlocalizedName("bowIce");
		this.setCreativeTab(Cavern.TAB_CAVERN);
		this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
		{
			@SideOnly(Side.CLIENT)
			@Override
			public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
			{
				if (entity == null)
				{
					return 0.0F;
				}
				else
				{
					ItemStack itemstack = entity.getActiveItemStack();
					float f = 0.001F  * IceEquipment.get(itemstack).getCharge();

					return !itemstack.isEmpty() && itemstack.getItem() == ItemBowIce.this ? (stack.getMaxItemUseDuration() - entity.getItemInUseCount()) / Math.max(10.0F - f, 6.7F) : 0.0F;
				}
			}
		});
	}

	@Override
	public int getMaxDamage(ItemStack itemstack)
	{
		int max = super.getMaxDamage(itemstack);

		return max + max / 8 * IceEquipment.get(itemstack).getCharge();
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack)
	{
		int duration = super.getMaxItemUseDuration(itemstack);
		int min = duration / 2;
		int max = duration / 3;

		return MathHelper.clamp(min - duration / 8 * IceEquipment.get(itemstack).getCharge(), min, max);
	}

	@Override
	public int getItemEnchantability()
	{
		return 0;
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
	{
		return super.canApplyAtEnchantingTable(stack, enchantment) && IceEquipment.canApplyEnchantments(stack, enchantment);
	}
}