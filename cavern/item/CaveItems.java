package cavern.item;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.core.Cavern;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CaveItems
{
	public static final ToolMaterial AQUAMARINE = EnumHelper.addToolMaterial("AQUAMARINE", 2, 200, 8.0F, 1.5F, 15);
	public static final ToolMaterial MAGNITE = EnumHelper.addToolMaterial("MAGNITE", 3, 10, 100.0F, 11.0F, 50);
	public static final ToolMaterial HEXCITE = EnumHelper.addToolMaterial("HEXCITE", 3, 1041, 10.0F, 5.0F, 15);

	public static final ArmorMaterial HEXCITE_ARMOR = EnumHelper.addArmorMaterial("HEXCITE", "hexcite", 22, new int[] {4, 7, 9, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 1.0F);

	public static final Item CAVE_ITEM = new ItemCave();
	public static final ItemPickaxeAquamarine AQUAMARINE_PICKAXE = new ItemPickaxeAquamarine();
	public static final ItemAxeAquamarine AQUAMARINE_AXE = new ItemAxeAquamarine();
	public static final ItemShovelAquamarine AQUAMARINE_SHOVEL = new ItemShovelAquamarine();
	public static final ItemSwordCave MAGNITE_SWORD = new ItemSwordCave(MAGNITE, "swordMagnite");
	public static final ItemPickaxeCave MAGNITE_PICKAXE = new ItemPickaxeCave(MAGNITE, "pickaxeMagnite");
	public static final ItemAxeCave MAGNITE_AXE = new ItemAxeCave(MAGNITE, 18.0F, -3.0F, "axeMagnite");
	public static final ItemShovelCave MAGNITE_SHOVEL = new ItemShovelCave(MAGNITE, "shovelMagnite");
	public static final ItemSwordCave HEXCITE_SWORD = new ItemSwordCave(HEXCITE, "swordHexcite");
	public static final ItemPickaxeCave HEXCITE_PICKAXE = new ItemPickaxeCave(HEXCITE, "pickaxeHexcite");
	public static final ItemAxeCave HEXCITE_AXE = new ItemAxeCave(HEXCITE, 10.0F, -2.8F, "axeHexcite");
	public static final ItemShovelCave HEXCITE_SHOVEL = new ItemShovelCave(HEXCITE, "shovelHexcite");
	public static final ItemHoeCave HEXCITE_HOE = new ItemHoeCave(HEXCITE, "hoeHexcite");
	public static final ItemArmorCave HEXCITE_HELMET = new ItemArmorCave(HEXCITE_ARMOR, "helmetHexcite", "hexcite", EntityEquipmentSlot.HEAD);
	public static final ItemArmorCave HEXCITE_CHESTPLATE = new ItemArmorCave(HEXCITE_ARMOR, "chestplateHexcite", "hexcite", EntityEquipmentSlot.CHEST);
	public static final ItemArmorCave HEXCITE_LEGGINGS = new ItemArmorCave(HEXCITE_ARMOR, "leggingsHexcite", "hexcite", EntityEquipmentSlot.LEGS);
	public static final ItemArmorCave HEXCITE_BOOTS = new ItemArmorCave(HEXCITE_ARMOR, "bootsHexcite", "hexcite", EntityEquipmentSlot.FEET);

	public static void registerItems()
	{
		GameRegistry.register(CAVE_ITEM.setRegistryName("cave_item"));
		GameRegistry.register(AQUAMARINE_PICKAXE.setRegistryName("aquamarine_pickaxe"));
		GameRegistry.register(AQUAMARINE_AXE.setRegistryName("aquamarine_axe"));
		GameRegistry.register(AQUAMARINE_SHOVEL.setRegistryName("aquamarine_shovel"));
		GameRegistry.register(MAGNITE_SWORD.setRegistryName("magnite_sword"));
		GameRegistry.register(MAGNITE_PICKAXE.setRegistryName("magnite_pickaxe"));
		GameRegistry.register(MAGNITE_AXE.setRegistryName("magnite_axe"));
		GameRegistry.register(MAGNITE_SHOVEL.setRegistryName("magnite_shovel"));
		GameRegistry.register(HEXCITE_SWORD.setRegistryName("hexcite_sword"));
		GameRegistry.register(HEXCITE_PICKAXE.setRegistryName("hexcite_pickaxe"));
		GameRegistry.register(HEXCITE_AXE.setRegistryName("hexcite_axe"));
		GameRegistry.register(HEXCITE_SHOVEL.setRegistryName("hexcite_shovel"));
		GameRegistry.register(HEXCITE_HOE.setRegistryName("hexcite_hoe"));
		GameRegistry.register(HEXCITE_HELMET.setRegistryName("hexcite_helmet"));
		GameRegistry.register(HEXCITE_CHESTPLATE.setRegistryName("hexcite_chestplate"));
		GameRegistry.register(HEXCITE_LEGGINGS.setRegistryName("hexcite_leggings"));
		GameRegistry.register(HEXCITE_BOOTS.setRegistryName("hexcite_boots"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels()
	{
		registerModelWithMeta(CAVE_ITEM, "aquamarine", "magnite_ingot", "hexcite");
		registerModel(AQUAMARINE_PICKAXE, "aquamarine_pickaxe");
		registerModel(AQUAMARINE_AXE, "aquamarine_axe");
		registerModel(AQUAMARINE_SHOVEL, "aquamarine_shovel");
		registerModel(MAGNITE_SWORD, "magnite_sword");
		registerModel(MAGNITE_PICKAXE, "magnite_pickaxe");
		registerModel(MAGNITE_AXE, "magnite_axe");
		registerModel(MAGNITE_SHOVEL, "magnite_shovel");
		registerModel(HEXCITE_SWORD, "hexcite_sword");
		registerModel(HEXCITE_PICKAXE, "hexcite_pickaxe");
		registerModel(HEXCITE_AXE, "hexcite_axe");
		registerModel(HEXCITE_SHOVEL, "hexcite_shovel");
		registerModel(HEXCITE_HOE, "hexcite_hoe");
		registerModel(HEXCITE_HELMET, "hexcite_helmet");
		registerModel(HEXCITE_CHESTPLATE, "hexcite_chestplate");
		registerModel(HEXCITE_LEGGINGS, "hexcite_leggings");
		registerModel(HEXCITE_BOOTS, "hexcite_boots");
	}

	@SideOnly(Side.CLIENT)
	public static void registerModel(Item item, String modelName)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Cavern.MODID + ":" + modelName, "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerModelWithMeta(Item item, String... modelName)
	{
		List<ModelResourceLocation> models = Lists.newArrayList();

		for (String model : modelName)
		{
			models.add(new ModelResourceLocation(Cavern.MODID + ":" + model, "inventory"));
		}

		ModelBakery.registerItemVariants(item, models.toArray(new ResourceLocation[models.size()]));

		for (int i = 0; i < models.size(); ++i)
		{
			ModelLoader.setCustomModelResourceLocation(item, i, models.get(i));
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerVanillaModel(Item item, String modelName)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("minecraft:" + modelName, "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerVanillaModelWithMeta(Item item, String... modelName)
	{
		List<ModelResourceLocation> models = Lists.newArrayList();

		for (String model : modelName)
		{
			models.add(new ModelResourceLocation("minecraft:" + model, "inventory"));
		}

		ModelBakery.registerItemVariants(item, models.toArray(new ResourceLocation[models.size()]));

		for (int i = 0; i < models.size(); ++i)
		{
			ModelLoader.setCustomModelResourceLocation(item, i, models.get(i));
		}
	}

	public static void registerRecipes()
	{
		GameRegistry.addShapelessRecipe(new ItemStack(CAVE_ITEM, 9, ItemCave.EnumType.AQUAMARINE.getItemDamage()),
			new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.AQUAMARINE_BLOCK.getMetadata()));

		GameRegistry.addShapelessRecipe(new ItemStack(CAVE_ITEM, 9, ItemCave.EnumType.MAGNITE_INGOT.getItemDamage()),
			new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.MAGNITE_BLOCK.getMetadata()));

		GameRegistry.addShapelessRecipe(new ItemStack(CAVE_ITEM, 9, ItemCave.EnumType.HEXCITE.getItemDamage()),
				new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.HEXCITE_BLOCK.getMetadata()));

		ItemStack material = new ItemStack(CAVE_ITEM, 1, ItemCave.EnumType.AQUAMARINE.getItemDamage());

		GameRegistry.addRecipe(new ShapedOreRecipe(AQUAMARINE_PICKAXE,
			"AAA", " S ", " S ",
			'A', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(AQUAMARINE_AXE,
			"AA", "AS", " S",
			'A', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(AQUAMARINE_SHOVEL,
			"A", "S", "S",
			'A', material.copy(),
			'S', "stickWood"
		));

		material = new ItemStack(CAVE_ITEM, 1, ItemCave.EnumType.MAGNITE_INGOT.getItemDamage());

		GameRegistry.addRecipe(new ShapedOreRecipe(MAGNITE_SWORD,
			"M", "M", "S",
			'M', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(MAGNITE_PICKAXE,
			"MMM", " S ", " S ",
			'M', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(MAGNITE_AXE,
			"MM", "MS", " S",
			'M', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(MAGNITE_SHOVEL,
			"M", "S", "S",
			'M', material.copy(),
			'S', "stickWood"
		));

		material = new ItemStack(CAVE_ITEM, 1, ItemCave.EnumType.HEXCITE.getItemDamage());

		GameRegistry.addRecipe(new ShapedOreRecipe(HEXCITE_SWORD,
			"H", "H", "S",
			'H', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(HEXCITE_PICKAXE,
			"HHH", " S ", " S ",
			'H', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(HEXCITE_AXE,
			"HH", "HS", " S",
			'H', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(HEXCITE_SHOVEL,
			"H", "S", "S",
			'H', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(HEXCITE_HOE,
			"HH", " S", " S",
			'H', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ItemStack(HEXCITE_HELMET),
			"HHH", "H H",
			'H', material.copy()
		);

		GameRegistry.addRecipe(new ItemStack(HEXCITE_CHESTPLATE),
			"H H", "HHH", "HHH",
			'H', material.copy()
		);

		GameRegistry.addRecipe(new ItemStack(HEXCITE_LEGGINGS),
			"HHH", "H H", "H H",
			'H', material.copy()
		);

		GameRegistry.addRecipe(new ItemStack(HEXCITE_BOOTS),
			"H H", "H H",
			'H', material.copy()
		);
	}
}