package cavern.client;

import java.util.Map;

import com.google.common.collect.Maps;

import cavern.block.CaveBlocks;
import cavern.client.config.CaveConfigEntries;
import cavern.client.config.CycleIntegerEntry;
import cavern.client.config.SelectItemsEntry;
import cavern.client.config.SelectMobsEntry;
import cavern.client.config.general.MiningPointsEntry;
import cavern.core.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
	public static final Map<Block, Block> renderBlockMap = Maps.newHashMap();

	@Override
	public void initConfigEntries()
	{
		CaveConfigEntries.cycleIntegerEntry = CycleIntegerEntry.class;

		CaveConfigEntries.selectItemsEntry = SelectItemsEntry.class;
		CaveConfigEntries.selectMobsEntry = SelectMobsEntry.class;

		CaveConfigEntries.miningPointsEntry = MiningPointsEntry.class;
	}

	@Override
	public void registerRenderers()
	{
		renderBlockMap.put(Blocks.lit_redstone_ore, Blocks.redstone_ore);
	}

	@Override
	public void registerBlockColors()
	{
		final Minecraft mc = FMLClientHandler.instance().getClient();
		final BlockColors colors = mc.getBlockColors();

		colors.registerBlockColorHandler(new IBlockColor()
		{
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int tintIndex)
			{
				CaveBlocks.perverted_leaves.setGraphicsLevel(mc.gameSettings.fancyGraphics);

				BlockPlanks.EnumType type = state.getValue(BlockOldLeaf.VARIANT);

				return type == BlockPlanks.EnumType.SPRUCE ? ColorizerFoliage.getFoliageColorPine() : type == BlockPlanks.EnumType.BIRCH ? ColorizerFoliage.getFoliageColorBirch() : world != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(world, pos) : ColorizerFoliage.getFoliageColorBasic();
			}
		}, new Block[] {CaveBlocks.perverted_leaves});
	}

	@Override
	public void registerItemColors()
	{
		final Minecraft mc = FMLClientHandler.instance().getClient();
		final BlockColors blockColors = mc.getBlockColors();
		final ItemColors colors = mc.getItemColors();

		colors.registerItemColorHandler(new IItemColor()
		{
			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex)
			{
				IBlockState state = ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());

				return blockColors.colorMultiplier(state, null, null, tintIndex);
			}
		}, new Block[] {CaveBlocks.perverted_leaves});
	}
}