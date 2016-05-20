package cavern.block;

import java.util.List;
import java.util.Random;

import cavern.core.Cavern;
import cavern.world.gen.WorldGenBirchTreePerverted;
import cavern.world.gen.WorldGenSpruceTreePerverted;
import cavern.world.gen.WorldGenTreesPerverted;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSaplingPerverted extends BlockSapling
{
	public BlockSaplingPerverted()
	{
		super();
		this.setUnlocalizedName("pervertedSapling");
		this.setHardness(0.0F);
		this.setSoundType(SoundType.PLANT);
		this.setCreativeTab(Cavern.tabCavern);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if (!world.isRemote)
		{
			checkAndDropBlock(world, pos, state);

			grow(world, pos, state, rand);
		}
	}

	@Override
	public void generateTree(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if (!TerrainGen.saplingGrowTree(world, rand, pos))
		{
			return;
		}

		WorldGenerator treeGen;

		switch (state.getValue(TYPE))
		{
			case SPRUCE:
				treeGen = new WorldGenSpruceTreePerverted(true);
				break;
			case BIRCH:
				treeGen = new WorldGenBirchTreePerverted(true, false);
				break;
			case JUNGLE:
				treeGen = new WorldGenTreesPerverted(true, 3 + rand.nextInt(6), BlockPlanks.EnumType.JUNGLE, false);
				break;
			default:
				treeGen = new WorldGenTreesPerverted(true, 3, BlockPlanks.EnumType.OAK, false);
				break;
		}

		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 4);

		if (!treeGen.generate(world, rand, pos))
		{
			world.setBlockState(pos, state, 4);
		}
	}

	@Override
	public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
	{
		for (EnumType type : BlockOldLog.VARIANT.getAllowedValues())
		{
			list.add(new ItemStack(item, 1, type.getMetadata()));
		}
	}
}