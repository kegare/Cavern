package cavern.entity;

import java.util.List;

import cavern.core.Cavern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class CaveEntityRegistry
{
	private static int entityId;

	public static void registerEntity(Class<? extends Entity> entityClass, String registryName, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(Cavern.MODID, registryName), entityClass, entityName, entityId++, Cavern.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
	}

	public static void registerEntity(Class<? extends Entity> entityClass, String registryName, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates, int primaryColor, int secondaryColor)
	{
		EntityRegistry.registerModEntity(new ResourceLocation(Cavern.MODID, registryName), entityClass, entityName, entityId++, Cavern.instance, trackingRange, updateFrequency, sendsVelocityUpdates, primaryColor, secondaryColor);
	}

	public static void registerMob(Class<? extends Entity> entityClass, String registryName, String entityName)
	{
		registerEntity(entityClass, registryName, entityName, 128, 3, true);
	}

	public static void registerMob(Class<? extends Entity> entityClass, String registryName, String entityName, int primaryColor, int secondaryColor)
	{
		registerEntity(entityClass, registryName, entityName, 128, 3, true, primaryColor, secondaryColor);
	}

	public static void registerEntities()
	{
		registerMob(EntityCavenicSkeleton.class, "cavenic_skeleton", "CavenicSkeleton", 0xAAAAAA, 0xDDDDDD);
		registerMob(EntityCavenicCreeper.class, "cavenic_creeper", "CavenicCreeper", 0xAAAAAA, 0x2E8B57);
		registerMob(EntityCavenicZombie.class, "cavenic_zombie", "CavenicZombie", 0xAAAAAA, 0x00A0A0);
		registerMob(EntityCavenicSpider.class, "cavenic_spider", "CavenicSpider", 0xAAAAAA, 0x811F1F);
		registerEntity(EntityAquaSquid.class, "squid", "Squid", 64, 3, true);
	}

	public static void addSpawns()
	{
		List<Biome> biomes = ForgeRegistries.BIOMES.getValues();
		Biome[] biomeArray = biomes.toArray(new Biome[biomes.size()]);

		EntityRegistry.addSpawn(EntityCavenicSkeleton.class, 15, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCavenicCreeper.class, 30, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCavenicZombie.class, 30, 2, 2, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityCavenicSpider.class, 30, 1, 1, EnumCreatureType.MONSTER, biomeArray);
		EntityRegistry.addSpawn(EntityAquaSquid.class, 100, 4, 4, EnumCreatureType.WATER_CREATURE, biomeArray);
	}
}