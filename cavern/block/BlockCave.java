package cavern.block;

import java.util.List;
import java.util.Random;

import cavern.core.Cavern;
import cavern.item.CaveItems;
import cavern.item.ItemCave;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCave extends Block
{
	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType>create("variant", EnumType.class);

	protected static final Random rand = new Random();

	public BlockCave()
	{
		super(Material.rock);
		this.setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumType.AQUAMARINE_ORE));
		this.setUnlocalizedName("blockCave");
		this.setCreativeTab(Cavern.tabCavern);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {VARIANT});
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(VARIANT).getMetadata();
	}

	@Override
	public MapColor getMapColor(IBlockState state)
	{
		return state.getValue(VARIANT).getMapColor();
	}

	@Override
	public Material getMaterial(IBlockState state)
	{
		return state.getValue(VARIANT).getMaterial();
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos)
	{
		return state.getValue(VARIANT).getBlockHardness();
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
	{
		return world.getBlockState(pos).getValue(VARIANT).getBlockHardness() * 5.0F;
	}

	@Override
	public String getHarvestTool(IBlockState state)
	{
		return "pickaxe";
	}

	@Override
	public int getHarvestLevel(IBlockState state)
	{
		return state.getValue(VARIANT).getHarvestLevel();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
	{
		for (EnumType type : EnumType.values())
		{
			list.add(new ItemStack(item, 1, type.getMetadata()));
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		switch (state.getValue(VARIANT))
		{
			case AQUAMARINE_ORE:
				return CaveItems.cave_item;
			default:
		}

		return Item.getItemFromBlock(this);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		switch (state.getValue(VARIANT))
		{
			case AQUAMARINE_ORE:
				return ItemCave.EnumType.AQUAMARINE.getItemDamage();
			default:
		}

		return getMetaFromState(state);
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random)
	{
		int ret = quantityDropped(random);

		if (fortune > 0)
		{
			switch (state.getValue(VARIANT))
			{
				case AQUAMARINE_ORE:
					return ret * (Math.max(random.nextInt(fortune + 2) - 1, 0) + 1);
				default:
			}
		}

		return ret;
	}

	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune)
	{
		switch (state.getValue(VARIANT))
		{
			case AQUAMARINE_ORE:
				return MathHelper.getRandomIntegerInRange(rand, 1, 3);
			default:
		}

		return 0;
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
	{
		return new ItemStack(this, 1, getMetaFromState(state));
	}

	public enum EnumType implements IStringSerializable
	{
		AQUAMARINE_ORE(0, "aquamarine_ore", "oreAquamarine", MapColor.diamondColor, Material.rock, 3.0F, 1),
		AQUAMARINE_BLOCK(1, "aquamarine_block", "blockAquamarine", MapColor.diamondColor, Material.iron, 3.5F, 1),
		MAGNITE_ORE(2, "magnite_ore", "oreMagnite", MapColor.redColor, Material.rock, 3.0F, 2),
		MAGNITE_BLOCK(3, "magnite_block", "blockMagnite", MapColor.redColor, Material.iron, 2.5F, 2);

		private static final EnumType[] META_LOOKUP = new EnumType[values().length];

		private final int meta;
		private final String name;
		private final String unlocalizedName;
		private final MapColor mapColor;
		private final Material material;
		private final float blockHardness;
		private final int harvestLevel;

		private EnumType(int meta, String name, String unlocalizedName, MapColor color, Material material, float hardness, int harvestLevel)
		{
			this.meta = meta;
			this.name = name;
			this.unlocalizedName = unlocalizedName;
			this.mapColor = color;
			this.material = material;
			this.blockHardness = hardness;
			this.harvestLevel = harvestLevel;
		}

		public int getMetadata()
		{
			return meta;
		}

		public MapColor getMapColor()
		{
			return mapColor;
		}

		public Material getMaterial()
		{
			return material;
		}

		public float getBlockHardness()
		{
			return blockHardness;
		}

		public int getHarvestLevel()
		{
			return harvestLevel;
		}

		@Override
		public String toString()
		{
			return name;
		}

		public static EnumType byMetadata(int meta)
		{
			if (meta < 0 || meta >= META_LOOKUP.length)
			{
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		@Override
		public String getName()
		{
			return name;
		}

		public String getUnlocalizedName()
		{
			return unlocalizedName;
		}

		static
		{
			for (EnumType type : values())
			{
				META_LOOKUP[type.getMetadata()] = type;
			}
		}
	}
}