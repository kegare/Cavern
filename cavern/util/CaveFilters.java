package cavern.util;

import javax.annotation.Nullable;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;

public class CaveFilters
{
	public static boolean blockFilter(@Nullable BlockMeta blockMeta, @Nullable String filter)
	{
		if (blockMeta == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		if (CaveUtils.containsIgnoreCase(blockMeta.getName(), filter))
		{
			return true;
		}

		if (CaveUtils.containsIgnoreCase(blockMeta.getMetaString(), filter))
		{
			return true;
		}

		Block block = blockMeta.getBlock();
		ItemStack stack = new ItemStack(block, 1, blockMeta.getMeta());

		if (stack.getItem() == Items.AIR)
		{
			if (CaveUtils.containsIgnoreCase(block.getLocalizedName(), filter))
			{
				return true;
			}

			if (CaveUtils.containsIgnoreCase(block.getUnlocalizedName(), filter))
			{
				return true;
			}
		}
		else
		{
			if (CaveUtils.containsIgnoreCase(stack.getDisplayName(), filter))
			{
				return true;
			}

			if (CaveUtils.containsIgnoreCase(stack.getUnlocalizedName(), filter))
			{
				return true;
			}
		}

		if (CaveUtils.containsIgnoreCase(block.getHarvestTool(blockMeta.getBlockState()), filter))
		{
			return true;
		}

		return false;
	}

	public static boolean itemFilter(@Nullable ItemMeta itemMeta, @Nullable String filter)
	{
		if (itemMeta == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		if (CaveUtils.containsIgnoreCase(itemMeta.getName(), filter))
		{
			return true;
		}

		ItemStack stack = itemMeta.getItemStack();

		if (CaveUtils.containsIgnoreCase(stack.getDisplayName(), filter))
		{
			return true;
		}

		if (CaveUtils.containsIgnoreCase(stack.getUnlocalizedName(), filter))
		{
			return true;
		}

		if (stack.getItem().getToolClasses(stack).contains(filter))
		{
			return true;
		}

		return false;
	}

	public static boolean biomeFilter(@Nullable Biome biome, @Nullable String filter)
	{
		if (biome == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		if (Biome.getIdForBiome(biome) == NumberUtils.toInt(filter, -1) || CaveUtils.containsIgnoreCase(biome.getRegistryName().toString(), filter))
		{
			return true;
		}

		for (BiomeDictionary.Type type : BiomeDictionary.getTypes(biome))
		{
			if (type.getName().equalsIgnoreCase(filter))
			{
				return true;
			}
		}

		if (blockFilter(new BlockMeta(biome.topBlock), filter))
		{
			return true;
		}

		if (blockFilter(new BlockMeta(biome.fillerBlock), filter))
		{
			return true;
		}

		BiomeType type;

		try
		{
			type = BiomeType.valueOf(filter.toUpperCase());
		}
		catch (IllegalArgumentException e)
		{
			type = null;
		}

		if (type != null)
		{
			ImmutableList<BiomeEntry> list = BiomeManager.getBiomes(type);

			if (list != null)
			{
				for (BiomeEntry entry : list)
				{
					if (entry != null && entry.biome.getRegistryName().equals(biome.getRegistryName()))
					{
						return true;
					}
				}
			}
		}

		return false;
	}
}