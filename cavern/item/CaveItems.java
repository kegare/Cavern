package cavern.item;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.core.Cavern;
import cavern.recipe.RecipeChargeIceEquipment;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
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
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class CaveItems
{
	public static final ToolMaterial AQUAMARINE = EnumHelper.addToolMaterial("AQUAMARINE", 2, 200, 8.0F, 1.5F, 15);
	public static final ToolMaterial MAGNITE = EnumHelper.addToolMaterial("MAGNITE", 3, 10, 100.0F, 11.0F, 50);
	public static final ToolMaterial HEXCITE = EnumHelper.addToolMaterial("HEXCITE", 3, 1041, 10.0F, 5.0F, 15);
	public static final ToolMaterial ICE = EnumHelper.addToolMaterial("ice", 1, 120, 5.0F, 1.0F, 0);

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
	public static final ItemSwordIce ICE_SWORD = new ItemSwordIce();
	public static final ItemPickaxeIce ICE_PICKAXE = new ItemPickaxeIce();
	public static final ItemAxeIce ICE_AXE = new ItemAxeIce();
	public static final ItemShovelIce ICE_SHOVEL = new ItemShovelIce();
	public static final ItemHoeIce ICE_HOE = new ItemHoeIce();
	public static final ItemBowIce ICE_BOW = new ItemBowIce();

	public static void registerItems(IForgeRegistry<Item> registry)
	{
		registry.register(CAVE_ITEM.setRegistryName("cave_item"));
		registry.register(AQUAMARINE_PICKAXE.setRegistryName("aquamarine_pickaxe"));
		registry.register(AQUAMARINE_AXE.setRegistryName("aquamarine_axe"));
		registry.register(AQUAMARINE_SHOVEL.setRegistryName("aquamarine_shovel"));
		registry.register(MAGNITE_SWORD.setRegistryName("magnite_sword"));
		registry.register(MAGNITE_PICKAXE.setRegistryName("magnite_pickaxe"));
		registry.register(MAGNITE_AXE.setRegistryName("magnite_axe"));
		registry.register(MAGNITE_SHOVEL.setRegistryName("magnite_shovel"));
		registry.register(HEXCITE_SWORD.setRegistryName("hexcite_sword"));
		registry.register(HEXCITE_PICKAXE.setRegistryName("hexcite_pickaxe"));
		registry.register(HEXCITE_AXE.setRegistryName("hexcite_axe"));
		registry.register(HEXCITE_SHOVEL.setRegistryName("hexcite_shovel"));
		registry.register(HEXCITE_HOE.setRegistryName("hexcite_hoe"));
		registry.register(HEXCITE_HELMET.setRegistryName("hexcite_helmet"));
		registry.register(HEXCITE_CHESTPLATE.setRegistryName("hexcite_chestplate"));
		registry.register(HEXCITE_LEGGINGS.setRegistryName("hexcite_leggings"));
		registry.register(HEXCITE_BOOTS.setRegistryName("hexcite_boots"));
		registry.register(ICE_SWORD.setRegistryName("ice_sword"));
		registry.register(ICE_PICKAXE.setRegistryName("ice_pickaxe"));
		registry.register(ICE_AXE.setRegistryName("ice_axe"));
		registry.register(ICE_SHOVEL.setRegistryName("ice_shovel"));
		registry.register(ICE_HOE.setRegistryName("ice_hoe"));
		registry.register(ICE_BOW.setRegistryName("ice_bow"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels()
	{
		registerModelWithMeta(CAVE_ITEM, "aquamarine", "magnite_ingot", "hexcite", "ice_stick");
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
		registerModel(ICE_SWORD, "ice_sword");
		registerModel(ICE_PICKAXE, "ice_pickaxe");
		registerModel(ICE_AXE, "ice_axe");
		registerModel(ICE_SHOVEL, "ice_shovel");
		registerModel(ICE_HOE, "ice_hoe");
		registerModel(ICE_BOW, "ice_bow");
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

	public static void registerOreDicts()
	{
		OreDictionary.registerOre("gemAquamarine", new ItemStack(CAVE_ITEM, 1, ItemCave.EnumType.AQUAMARINE.getItemDamage()));
		OreDictionary.registerOre("ingotMagnite", new ItemStack(CAVE_ITEM, 1, ItemCave.EnumType.MAGNITE_INGOT.getItemDamage()));
		OreDictionary.registerOre("gemHexcite", new ItemStack(CAVE_ITEM, 1, ItemCave.EnumType.HEXCITE.getItemDamage()));
	}

	public static void registerEquipments()
	{
		IceEquipment.register(ICE_SWORD);
		IceEquipment.register(ICE_PICKAXE);
		IceEquipment.register(ICE_AXE);
		IceEquipment.register(ICE_SHOVEL);
		IceEquipment.register(ICE_HOE);
		IceEquipment.register(ICE_BOW);
	}

	public static void registerRecipes()
	{
		GameRegistry.addShapelessRecipe(new ItemStack(CAVE_ITEM, 9, ItemCave.EnumType.AQUAMARINE.getItemDamage()),
			new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.AQUAMARINE_BLOCK.getMetadata()));

		GameRegistry.addShapelessRecipe(new ItemStack(CAVE_ITEM, 9, ItemCave.EnumType.MAGNITE_INGOT.getItemDamage()),
			new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.MAGNITE_BLOCK.getMetadata()));

		GameRegistry.addShapelessRecipe(new ItemStack(CAVE_ITEM, 9, ItemCave.EnumType.HEXCITE.getItemDamage()),
				new ItemStack(CaveBlocks.CAVE_BLOCK, 1, BlockCave.EnumType.HEXCITE_BLOCK.getMetadata()));

		GameRegistry.addRecipe(new ItemStack(CAVE_ITEM, 2, ItemCave.EnumType.ICE_STICK.getItemDamage()),
			"I", "I",
			'I', Blocks.ICE
		);
		GameRegistry.addRecipe(new ItemStack(CAVE_ITEM, 8, ItemCave.EnumType.ICE_STICK.getItemDamage()),
			"I", "I",
			'I', Blocks.PACKED_ICE
		);

		ItemStack material = new ItemStack(CAVE_ITEM, 1, ItemCave.EnumType.AQUAMARINE.getItemDamage());
		ItemStack subMaterial;

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

		material = new ItemStack(Blocks.ICE);
		subMaterial = new ItemStack(CAVE_ITEM, 1, ItemCave.EnumType.ICE_STICK.getItemDamage());

		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_SWORD, 3),
			"I", "I", "S",
			'I', material.copy(),
			'S', subMaterial.copy()
		);

		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_PICKAXE, 4),
			"III", " S ", " S ",
			'I', material.copy(),
			'S', subMaterial.copy()
		);

		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_AXE, 4),
			"II", "IS", " S",
			'I', material.copy(),
			'S', subMaterial.copy()
		);

		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_SHOVEL, 2),
			"I", "S", "S",
			'I', material.copy(),
			'S', subMaterial.copy()
		);

		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_HOE, 3),
			"II", " S", " S",
			'I', material.copy(),
			'S', subMaterial.copy()
		);

		material = new ItemStack(Blocks.PACKED_ICE);

		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_SWORD, 19),
			"I", "I", "S",
			'I', material.copy(),
			'S', subMaterial.copy()
		);

		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_PICKAXE, 28),
			"III", " S ", " S ",
			'I', material.copy(),
			'S', subMaterial.copy()
		);

		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_AXE, 28),
			"II", "IS", " S",
			'I', material.copy(),
			'S', subMaterial.copy()
		);

		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_SHOVEL, 10),
			"I", "S", "S",
			'I', material.copy(),
			'S', subMaterial.copy()
		);

		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_HOE, 19),
			"II", " S", " S",
			'I', material.copy(),
			'S', subMaterial.copy()
		);

		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_BOW, 1),
			"SI ", "S I", "SI ",
			'I', subMaterial.copy(),
			'S', Items.STRING
		);

		material = new ItemStack(Items.BOW, 1, OreDictionary.WILDCARD_VALUE);

		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_BOW, 4),
			" I ", "IBI", " I ",
			'I', Blocks.ICE,
			'B', material.copy()
		);
		GameRegistry.addRecipe(IceEquipment.getChargedItem(ICE_BOW, 36),
			" I ", "IBI", " I ",
			'I', Blocks.PACKED_ICE,
			'B', material.copy()
		);

		GameRegistry.addRecipe(new RecipeChargeIceEquipment());

		RecipeSorter.register(Cavern.MODID + ":charge_ice_equip", RecipeChargeIceEquipment.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
	}
}