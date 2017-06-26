package cavern.block;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;

import cavern.block.bonus.FissureBreakEvent;
import cavern.core.Cavern;
import cavern.item.CaveItems;
import cavern.item.ItemCave;
import cavern.util.WeightedItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.WeightedRandom;
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
	public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);

	public static final List<WeightedItemStack> RANDOMITE_ITEMS = Lists.newArrayList();
	public static final List<FissureBreakEvent> FISSURE_EVENTS = Lists.newArrayList();

	public BlockCave()
	{
		super(Material.ROCK);
		this.setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumType.AQUAMARINE_ORE));
		this.setUnlocalizedName("blockCave");
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, VARIANT);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
	}

	public EnumType getType(IBlockState state)
	{
		if (state == null || state.getBlock() != this || state.getPropertyKeys().isEmpty())
		{
			state = getDefaultState();
		}

		return state.getValue(VARIANT);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return getType(state).getMetadata();
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess blockAccess, BlockPos pos)
	{
		return getType(state).getMapColor();
	}

	@Override
	public Material getMaterial(IBlockState state)
	{
		return getType(state).getMaterial();
	}

	@Override
	public SoundType getSoundType(IBlockState state, World world, BlockPos pos, Entity entity)
	{
		return getType(state).getSoundType();
	}

	@Override
	public float getBlockHardness(IBlockState state, World world, BlockPos pos)
	{
		return getType(state).getBlockHardness();
	}

	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion)
	{
		return getType(world.getBlockState(pos)).getBlockHardness() * 5.0F;
	}

	@Override
	public String getHarvestTool(IBlockState state)
	{
		return "pickaxe";
	}

	@Override
	public int getHarvestLevel(IBlockState state)
	{
		return getType(state).getHarvestLevel();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT_MIPPED;
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list)
	{
		for (EnumType type : EnumType.values())
		{
			list.add(type.getItemStack());
		}
	}

	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune)
	{
		super.dropBlockAsItemWithChance(world, pos, state, chance, fortune);

		if (!world.isRemote && !world.restoringBlockSnapshots)
		{
			EntityPlayer player = harvesters.get();

			switch (getType(state))
			{
				case RANDOMITE_ORE:
					WeightedItemStack randomItem = WeightedRandom.getRandomItem(RANDOM, RANDOMITE_ITEMS);

					if (randomItem == null)
					{
						break;
					}

					ItemStack stack = randomItem.getItemStack();

					if (RANDOM.nextDouble() <= 0.015D)
					{
						Item item = Item.REGISTRY.getRandomObject(RANDOM);

						if (item != null)
						{
							stack = new ItemStack(item);
						}
					}

					if (stack.isEmpty() || RANDOM.nextInt(10) == 0)
					{
						if (player != null)
						{
							player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 20 * 60, 1, false, false));
						}
					}
					else
					{
						spawnAsEntity(world, pos, stack);
					}

					break;
				case FISSURED_STONE:
				case FISSURED_PACKED_ICE:
					FissureBreakEvent event = WeightedRandom.getRandomItem(RANDOM, FISSURE_EVENTS);

					if (event != null)
					{
						event.get().onBreakBlock(world, pos, state, chance, fortune, harvesters.get(), RANDOM);
					}

					break;
				default:
					return;
			}
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		switch (getType(state))
		{
			case AQUAMARINE_ORE:
			case HEXCITE_ORE:
			case MANALITE_ORE:
				return CaveItems.CAVE_ITEM;
			case RANDOMITE_ORE:
			case FISSURED_STONE:
			case FISSURED_PACKED_ICE:
				return Items.AIR;
			default:
		}

		return Item.getItemFromBlock(this);
	}

	@Override
	public int damageDropped(IBlockState state)
	{
		switch (getType(state))
		{
			case AQUAMARINE_ORE:
				return ItemCave.EnumType.AQUAMARINE.getMetadata();
			case HEXCITE_ORE:
				return ItemCave.EnumType.HEXCITE.getMetadata();
			case MANALITE_ORE:
				return ItemCave.EnumType.MANALITE.getMetadata();
			default:
		}

		return getMetaFromState(state);
	}

	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random)
	{
		int ret = quantityDropped(random);
		EnumType type = getType(state);

		if (type == EnumType.RANDOMITE_ORE || type == EnumType.FISSURED_STONE || type == EnumType.FISSURED_PACKED_ICE)
		{
			return 0;
		}

		if (fortune > 0)
		{
			switch (type)
			{
				case AQUAMARINE_ORE:
				case HEXCITE_ORE:
				case MANALITE_ORE:
					return ret * (Math.max(random.nextInt(fortune + 2) - 1, 0) + 1);
				default:
			}
		}

		return ret;
	}

	@Override
	public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune)
	{
		switch (getType(state))
		{
			case AQUAMARINE_ORE:
			case RANDOMITE_ORE:
				return MathHelper.getInt(RANDOM, 1, 3);
			case HEXCITE_ORE:
			case FISSURED_STONE:
			case FISSURED_PACKED_ICE:
				return MathHelper.getInt(RANDOM, 3, 5);
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
		AQUAMARINE_ORE(0, "aquamarine_ore", "oreAquamarine", MapColor.DIAMOND, Material.ROCK, SoundType.STONE, 3.0F, 1),
		AQUAMARINE_BLOCK(1, "aquamarine_block", "blockAquamarine", MapColor.DIAMOND, Material.IRON, SoundType.METAL, 3.5F, 1),
		MAGNITE_ORE(2, "magnite_ore", "oreMagnite", MapColor.RED, Material.ROCK, SoundType.STONE, 3.0F, 2),
		MAGNITE_BLOCK(3, "magnite_block", "blockMagnite", MapColor.RED, Material.IRON, SoundType.METAL, 2.5F, 2),
		RANDOMITE_ORE(4, "randomite_ore", "oreRandomite", MapColor.PURPLE, Material.ROCK, SoundType.STONE, 4.0F, 1),
		HEXCITE_ORE(5, "hexcite_ore", "oreHexcite", MapColor.SNOW, Material.ROCK, SoundType.STONE, 3.0F, 2),
		HEXCITE_BLOCK(6, "hexcite_block", "blockHexcite", MapColor.SNOW, Material.IRON, SoundType.METAL, 3.5F, 2),
		FISSURED_STONE(7, "fissured_stone", "stone.stone", MapColor.STONE, Material.ROCK, SoundType.STONE, 1.0F, 0),
		FISSURED_PACKED_ICE(8, "fissured_packed_ice", "icePacked", MapColor.ICE, Material.ICE, SoundType.GLASS, 1.0F, 0),
		MANALITE_ORE(9, "manalite_ore", "oreManalite", MapColor.DIAMOND, Material.ROCK, SoundType.STONE, 3.5F, 2),
		MANALITE_BLOCK(10, "manalite_block", "blockManalite", MapColor.DIAMOND, Material.IRON, SoundType.METAL, 4.0F, 2);

		private static final EnumType[] META_LOOKUP = new EnumType[values().length];

		private final int meta;
		private final String name;
		private final String unlocalizedName;
		private final MapColor mapColor;
		private final Material material;
		private final SoundType soundType;
		private final float blockHardness;
		private final int harvestLevel;

		private EnumType(int meta, String name, String unlocalizedName, MapColor color, Material material, SoundType soundType, float hardness, int harvestLevel)
		{
			this.meta = meta;
			this.name = name;
			this.unlocalizedName = unlocalizedName;
			this.mapColor = color;
			this.material = material;
			this.soundType = soundType;
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

		public SoundType getSoundType()
		{
			return soundType;
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

		@Override
		public String getName()
		{
			return name;
		}

		public String getUnlocalizedName()
		{
			return unlocalizedName;
		}

		public ItemStack getItemStack()
		{
			return getItemStack(1);
		}

		public ItemStack getItemStack(int amount)
		{
			return new ItemStack(CaveBlocks.CAVE_BLOCK, amount, getMetadata());
		}

		public static EnumType byMetadata(int meta)
		{
			if (meta < 0 || meta >= META_LOOKUP.length)
			{
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		public static EnumType byItemStack(ItemStack stack)
		{
			return byMetadata(stack.isEmpty() ? 0 : stack.getMetadata());
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