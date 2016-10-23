package cavern.entity;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.core.Cavern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class CaveEntityRegistry
{
	private static int entityId;

	public static void registerEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
	{
		EntityRegistry.registerModEntity(entityClass, entityName, entityId++, Cavern.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	public static void registerEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int primaryColor, int secondaryColor)
	{
		EntityRegistry.registerModEntity(entityClass, entityName, entityId++, Cavern.instance, trackingRange, updateFrequency, sendsVelocityUpdates, primaryColor, secondaryColor);
	}

	public static void registerMob(Class<? extends Entity> entityClass, String entityName)
	{
		registerEntity(entityClass, entityName, 128, 1, true);
	}

	public static void registerMob(Class<? extends Entity> entityClass, String entityName, int primaryColor, int secondaryColor)
	{
		registerEntity(entityClass, entityName, 128, 1, true, primaryColor, secondaryColor);
	}

	public static void registerEntities()
	{
		registerMob(EntityCavenicSkeleton.class, "CavenicSkeleton", 0xAAAAAA, 0xDDDDDD);
		registerMob(EntityCavenicCreeper.class, "CavenicCreeper", 0xAAAAAA, 0x2E8B57);
		registerMob(EntityCavenicZombie.class, "CavenicZombie", 0xAAAAAA, 0x00A0A0);
		registerMob(EntityCavenicSpider.class, "CavenicSpider", 0xAAAAAA, 0x811F1F);
		registerMob(EntityAquaSquid.class, "Squid");
	}

	public static void addSpawns()
	{
		List<Biome> biomes = Lists.newArrayList();

		for (Biome biome : Biome.REGISTRY)
		{
			if (biome != null)
			{
				biomes.add(biome);
			}
		}

		Biome[] biomeArray = biomes.toArray(new Biome[biomes.size()]);

		EntityRegistry.addSpawn(EntityCavenicSkeleton.class, 15, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCavenicCreeper.class, 30, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCavenicZombie.class, 30, 2, 2, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCavenicSpider.class, 30, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityAquaSquid.class, 100, 4, 4, EnumCreatureType.WATER_CREATURE, biomeArray);
	}
}