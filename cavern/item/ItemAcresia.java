package cavern.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemAcresia extends ItemBlock implements IPlantable
{
	public ItemAcresia(Block block)
	{
		super(block);
		this.setRegistryName(block.getRegistryName());
		this.setUnlocalizedName("acresia");
		this.setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "item." + EnumType.byItemStack(stack).getUnlocalizedName();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems)
	{
		for (EnumType type : EnumType.values())
		{
			subItems.add(new ItemStack(item, 1, type.getItemDamage()));
		}
	}

	public boolean isSeeds(ItemStack stack)
	{
		return stack != null && EnumType.byItemStack(stack) == EnumType.SEEDS;
	}

	public boolean isFruits(ItemStack stack)
	{
		return stack != null && EnumType.byItemStack(stack) == EnumType.FRUITS;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP) ? EnumPlantType.Cave : EnumPlantType.Plains;
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos)
	{
		return Block.getBlockFromItem(this).getDefaultState();
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if (isSeeds(stack))
		{
			if (facing == EnumFacing.UP && player.canPlayerEdit(pos, facing, stack) && player.canPlayerEdit(pos.up(), facing, stack))
			{
				IBlockState state = world.getBlockState(pos);
				Block soil = state.getBlock();

				if (soil != Blocks.bedrock && soil.canSustainPlant(state, world, pos, facing, this) && world.isAirBlock(pos.up()))
				{
					world.setBlockState(pos.up(), getPlant(world, pos));

					--stack.stackSize;

					return EnumActionResult.SUCCESS;
				}
			}
		}

		return super.onItemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
	{
		if (isFruits(stack))
		{
			--stack.stackSize;

			if (entityLiving instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)entityLiving;

				player.getFoodStats().addStats(getHealAmount(stack), getSaturationModifier(stack));

				world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.entity_player_burp, SoundCategory.PLAYERS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);

				player.addStat(StatList.func_188057_b(this));
			}

	        return stack;
		}

		return stack;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand)
	{
		if (isFruits(stack))
		{
			if (player.canEat(false))
			{
				player.setActiveHand(hand);

				return new ActionResult<>(EnumActionResult.SUCCESS, stack);
			}
		}

		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
	{
		return isFruits(stack) ? EnumAction.EAT : EnumAction.NONE;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack)
	{
		return isFruits(stack) ? 20 : super.getMaxItemUseDuration(stack);
	}

	public int getHealAmount(ItemStack stack)
	{
		return 1;
	}

	public float getSaturationModifier(ItemStack stack)
	{
		return 0.001F;
	}

	public enum EnumType
	{
		SEEDS(0, "seedsAcresia"),
		FRUITS(1, "fruitsAcresia");

		private static final EnumType[] DAMAGE_LOOKUP = new EnumType[values().length];

		private final int itemDamage;
		private final String unlocalizedName;

		private EnumType(int damage, String name)
		{
			this.itemDamage = damage;
			this.unlocalizedName = name;
		}

		public int getItemDamage()
		{
			return itemDamage;
		}

		public String getUnlocalizedName()
		{
			return unlocalizedName;
		}

		public static EnumType byDamage(int damage)
		{
			if (damage < 0 || damage >= DAMAGE_LOOKUP.length)
			{
				damage = 0;
			}

			return DAMAGE_LOOKUP[damage];
		}

		public static EnumType byItemStack(ItemStack itemstack)
		{
			return byDamage(itemstack == null ? 0 : itemstack.getItemDamage());
		}

		static
		{
			for (EnumType type : values())
			{
				DAMAGE_LOOKUP[type.getItemDamage()] = type;
			}
		}
	}
}