package cavern.util;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.base.Strings;

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
	public static boolean blockFilter(BlockMeta blockMeta, String filter)
	{
		if (blockMeta == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		try
		{
			if (CaveUtils.containsIgnoreCase(blockMeta.getName(), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			if (CaveUtils.containsIgnoreCase(blockMeta.getMetaString(), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		Block block = blockMeta.getBlock();
		ItemStack itemstack = new ItemStack(block, 1, blockMeta.getMeta());

		if (itemstack.getItem() == Items.AIR)
		{
			try
			{
				if (CaveUtils.containsIgnoreCase(block.getUnlocalizedName(), filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}

			try
			{
				if (CaveUtils.containsIgnoreCase(block.getLocalizedName(), filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}
		}
		else
		{
			try
			{
				if (CaveUtils.containsIgnoreCase(itemstack.getUnlocalizedName(), filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}

			try
			{
				if (CaveUtils.containsIgnoreCase(itemstack.getDisplayName(), filter))
				{
					return true;
				}
			}
			catch (Throwable e) {}
		}

		try
		{
			if (CaveUtils.containsIgnoreCase(block.getHarvestTool(blockMeta.getBlockState()), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		return false;
	}

	public static boolean itemFilter(ItemMeta itemMeta, String filter)
	{
		if (itemMeta == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		try
		{
			if (CaveUtils.containsIgnoreCase(itemMeta.getName(), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		ItemStack itemstack = itemMeta.getItemStack();

		try
		{
			if (CaveUtils.containsIgnoreCase(itemstack.getUnlocalizedName(), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			if (CaveUtils.containsIgnoreCase(itemstack.getDisplayName(), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			if (itemstack.getItem().getToolClasses(itemstack).contains(filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		return false;
	}

	public static boolean biomeFilter(Biome biome, String filter)
	{
		if (biome == null || Strings.isNullOrEmpty(filter))
		{
			return false;
		}

		if (Biome.getIdForBiome(biome) == NumberUtils.toInt(filter, -1) || CaveUtils.containsIgnoreCase(biome.getBiomeName(), filter))
		{
			return true;
		}

		try
		{
			if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.getType(filter)))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			if (blockFilter(new BlockMeta(biome.topBlock), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			if (blockFilter(new BlockMeta(biome.fillerBlock), filter))
			{
				return true;
			}
		}
		catch (Throwable e) {}

		try
		{
			BiomeType type = BiomeType.valueOf(filter.toUpperCase());

			if (type != null)
			{
				for (BiomeEntry entry : BiomeManager.getBiomes(type))
				{
					if (Biome.getIdForBiome(entry.biome) == Biome.getIdForBiome(biome))
					{
						return true;
					}
				}
			}
		}
		catch (Throwable e) {}

		return false;
	}
}