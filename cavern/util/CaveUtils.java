package cavern.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cavern.core.Cavern;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class CaveUtils
{
	private static ForkJoinPool pool;

	public static ForkJoinPool getPool()
	{
		if (pool == null || pool.isShutdown())
		{
			pool = new ForkJoinPool();
		}

		return pool;
	}

	public static ModContainer getModContainer()
	{
		ModContainer mod = Loader.instance().getIndexedModList().get(Cavern.MODID);

		if (mod == null)
		{
			mod = Loader.instance().activeModContainer();

			if (mod == null || mod.getModId() != Cavern.MODID)
			{
				return new DummyModContainer(Cavern.metadata);
			}
		}

		return mod;
	}

	public static final Comparator<Block> blockComparator = (o1, o2) ->
	{
		int i = compareWithNull(o1, o2);

		if (i == 0 && o1 != null && o2 != null)
		{
			ResourceLocation name1 = o1.getRegistryName();
			ResourceLocation name2 = o2.getRegistryName();

			i = compareWithNull(name1, name2);

			if (i == 0 && name1 != null && name2 != null)
			{
				i = (name1.getResourceDomain().equals("minecraft") ? 0 : 1) - (name2.getResourceDomain().equals("minecraft") ? 0 : 1);

				if (i == 0)
				{
					i = name1.getResourceDomain().compareTo(name2.getResourceDomain());

					if (i == 0)
					{
						i = name1.getResourcePath().compareTo(name2.getResourcePath());
					}
				}
			}
		}

		return i;
	};

	public static final Comparator<Biome> biomeComparator = (o1, o2) ->
	{
		int i = compareWithNull(o1, o2);

		if (i == 0 && o1 != null && o2 != null)
		{
			i = Integer.compare(Biome.getIdForBiome(o1), Biome.getIdForBiome(o2));

			if (i == 0)
			{
				i = compareWithNull(o1.getBiomeName(), o2.getBiomeName());

				if (i == 0 && o1.getBiomeName() != null && o2.getBiomeName() != null)
				{
					i = o1.getBiomeName().compareTo(o2.getBiomeName());

					if (i == 0)
					{
						i = Float.compare(o1.getTemperature(), o2.getTemperature());

						if (i == 0)
						{
							i = Float.compare(o1.getRainfall(), o2.getRainfall());

							if (i == 0)
							{
								i = new BlockMeta(o1.topBlock).compareTo(new BlockMeta(o2.topBlock));

								if (i == 0)
								{
									i = new BlockMeta(o1.fillerBlock).compareTo(new BlockMeta(o2.fillerBlock));
								}
							}
						}
					}
				}
			}
		}

		return i;
	};

	public static final Set<Item>
	pickaxeItems = Sets.newLinkedHashSet(),
	excludeItems = Sets.newHashSet();

	public static boolean isItemPickaxe(Item item)
	{
		if (item != null)
		{
			if (pickaxeItems.contains(item))
			{
				return true;
			}

			if (excludeItems.contains(item))
			{
				pickaxeItems.remove(item);

				return false;
			}

			if (item instanceof ItemPickaxe)
			{
				pickaxeItems.add(item);

				return true;
			}

			if (item.getToolClasses(new ItemStack(item)).contains("pickaxe"))
			{
				pickaxeItems.add(item);

				return true;
			}
		}

		return false;
	}

	public static boolean isItemPickaxe(ItemStack itemstack)
	{
		if (itemstack != null && itemstack.getItem() != null && itemstack.stackSize > 0)
		{
			Item item = itemstack.getItem();

			if (pickaxeItems.contains(item))
			{
				return true;
			}

			if (excludeItems.contains(item))
			{
				pickaxeItems.remove(item);

				return false;
			}

			if (item instanceof ItemPickaxe)
			{
				pickaxeItems.add(item);

				return true;
			}

			if (item.getToolClasses(itemstack).contains("pickaxe"))
			{
				pickaxeItems.add(item);

				return true;
			}
		}

		return false;
	}

	public static int compareWithNull(Object o1, Object o2)
	{
		return (o1 == null ? 1 : 0) - (o2 == null ? 1 : 0);
	}

	public static boolean containsIgnoreCase(String s1, String s2)
	{
		if (Strings.isNullOrEmpty(s1) || Strings.isNullOrEmpty(s2))
		{
			return false;
		}

		return Pattern.compile(Pattern.quote(s2), Pattern.CASE_INSENSITIVE).matcher(s1).find();
	}

	public static boolean archiveDirZip(File dir, File dest)
	{
		Path dirPath = dir.toPath();
		String parent = dir.getName();
		Map<String, String> env = Maps.newHashMap();
		env.put("create", "true");
		URI uri = dest.toURI();

		try
		{
			uri = new URI("jar:" + uri.getScheme(), uri.getPath(), null);
		}
		catch (Exception e)
		{
			return false;
		}

		try (FileSystem zipfs = FileSystems.newFileSystem(uri, env))
		{
			Files.createDirectory(zipfs.getPath(parent));

			for (File file : dir.listFiles())
			{
				if (file.isDirectory())
				{
					Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>()
					{
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
						{
							Files.copy(file, zipfs.getPath(parent, dirPath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);

							return FileVisitResult.CONTINUE;
						}

						@Override
						public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
						{
							Files.createDirectory(zipfs.getPath(parent, dirPath.relativize(dir).toString()));

							return FileVisitResult.CONTINUE;
						}
					});
				}
				else
				{
					Files.copy(file.toPath(), zipfs.getPath(parent, file.getName()), StandardCopyOption.REPLACE_EXISTING);
				}
			}

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return false;
		}
	}

	public static void setDimensionChange(EntityPlayerMP player)
	{
		if (!player.capabilities.isCreativeMode)
		{
			ObfuscationReflectionHelper.setPrivateValue(EntityPlayerMP.class, player, true, "invulnerableDimensionChange", "field_184851_cj");
		}
	}

	public static boolean areBlockStatesEqual(@Nullable IBlockState stateA, @Nullable IBlockState stateB)
	{
		if (stateA == stateB)
		{
			return true;
		}

		if (stateA == null || stateB == null)
		{
			return false;
		}

		return stateA.getBlock() == stateB.getBlock() && stateA.getBlock().getMetaFromState(stateA) == stateB.getBlock().getMetaFromState(stateB);
	}

	public static ItemStack getSpawnEgg(String entityName)
	{
		ItemStack item = new ItemStack(Items.SPAWN_EGG);
		ItemMonsterPlacer.applyEntityIdToItemStack(item, entityName);

		return item;
	}

	public static ItemStack getSpawnEgg(Class<? extends Entity> entityClass)
	{
		String entityName = EntityList.getEntityStringFromClass(entityClass);

		return getSpawnEgg(entityName);
	}
}