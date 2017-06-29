package cavern.item;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import cavern.recipe.RecipeChargeIceEquipment;
import cavern.recipe.RecipeHelper;
import cavern.util.CaveUtils;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

public class CaveItems
{
	private static final List<Item> ITEMS = Lists.newArrayList();
	private static final List<Item> MAGICAL_ITEMS = Lists.newArrayList();

	public static final ToolMaterial AQUAMARINE = EnumHelper.addToolMaterial("AQUAMARINE", 2, 200, 8.0F, 1.5F, 15);
	public static final ToolMaterial MAGNITE = EnumHelper.addToolMaterial("MAGNITE", 3, 10, 100.0F, 11.0F, 50);
	public static final ToolMaterial HEXCITE = EnumHelper.addToolMaterial("HEXCITE", 3, 1041, 10.0F, 5.0F, 15);
	public static final ToolMaterial ICE = EnumHelper.addToolMaterial("ICE", 1, 120, 5.0F, 1.0F, 0);
	public static final ToolMaterial MANALITE = EnumHelper.addToolMaterial("MANALITE", 3, 583, 6.0F, 2.0F, 8);

	public static final ArmorMaterial HEXCITE_ARMOR = EnumHelper.addArmorMaterial("HEXCITE", "hexcite", 22,
		new int[] {4, 7, 9, 4}, 15, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 1.0F);

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
	public static final ItemMagicalBook MAGICAL_BOOK = new ItemMagicalBook();
	public static final ItemElixir ELIXIR = new ItemElixir();
	public static final ItemCavenicBow CAVENIC_BOW = new ItemCavenicBow();
	public static final ItemSwordManalite MANALITE_SWORD = new ItemSwordManalite();
	public static final ItemAxeManalite MANALITE_AXE = new ItemAxeManalite();
	public static final ItemBowManalite MANALITE_BOW = new ItemBowManalite();

	public static List<Item> getItems()
	{
		return Collections.unmodifiableList(ITEMS);
	}

	public static List<Item> getMagicalItems()
	{
		return MAGICAL_ITEMS;
	}

	public static void registerItem(IForgeRegistry<Item> registry, Item item)
	{
		ITEMS.add(item);

		registry.register(item);
	}

	public static void registerItems(IForgeRegistry<Item> registry)
	{
		registerItem(registry, CAVE_ITEM.setRegistryName("cave_item"));
		registerItem(registry, AQUAMARINE_PICKAXE.setRegistryName("aquamarine_pickaxe"));
		registerItem(registry, AQUAMARINE_AXE.setRegistryName("aquamarine_axe"));
		registerItem(registry, AQUAMARINE_SHOVEL.setRegistryName("aquamarine_shovel"));
		registerItem(registry, MAGNITE_SWORD.setRegistryName("magnite_sword"));
		registerItem(registry, MAGNITE_PICKAXE.setRegistryName("magnite_pickaxe"));
		registerItem(registry, MAGNITE_AXE.setRegistryName("magnite_axe"));
		registerItem(registry, MAGNITE_SHOVEL.setRegistryName("magnite_shovel"));
		registerItem(registry, HEXCITE_SWORD.setRegistryName("hexcite_sword"));
		registerItem(registry, HEXCITE_PICKAXE.setRegistryName("hexcite_pickaxe"));
		registerItem(registry, HEXCITE_AXE.setRegistryName("hexcite_axe"));
		registerItem(registry, HEXCITE_SHOVEL.setRegistryName("hexcite_shovel"));
		registerItem(registry, HEXCITE_HOE.setRegistryName("hexcite_hoe"));
		registerItem(registry, HEXCITE_HELMET.setRegistryName("hexcite_helmet"));
		registerItem(registry, HEXCITE_CHESTPLATE.setRegistryName("hexcite_chestplate"));
		registerItem(registry, HEXCITE_LEGGINGS.setRegistryName("hexcite_leggings"));
		registerItem(registry, HEXCITE_BOOTS.setRegistryName("hexcite_boots"));
		registerItem(registry, ICE_SWORD.setRegistryName("ice_sword"));
		registerItem(registry, ICE_PICKAXE.setRegistryName("ice_pickaxe"));
		registerItem(registry, ICE_AXE.setRegistryName("ice_axe"));
		registerItem(registry, ICE_SHOVEL.setRegistryName("ice_shovel"));
		registerItem(registry, ICE_HOE.setRegistryName("ice_hoe"));
		registerItem(registry, ICE_BOW.setRegistryName("ice_bow"));
		registerItem(registry, MAGICAL_BOOK.setRegistryName("magical_book"));
		registerItem(registry, ELIXIR.setRegistryName("elixir"));
		registerItem(registry, CAVENIC_BOW.setRegistryName("cavenic_bow"));
		registerItem(registry, MANALITE_SWORD.setRegistryName("manalite_sword"));
		registerItem(registry, MANALITE_AXE.setRegistryName("manalite_axe"));
		registerItem(registry, MANALITE_BOW.setRegistryName("manalite_bow"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels()
	{
		registerModels(CAVE_ITEM, "aquamarine", "magnite_ingot", "hexcite", "ice_stick", "miner_orb", "cavenic_orb", "manalite");
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
		registerModels(MAGICAL_BOOK, "magical_book_flame_breath", "magical_book_explosion", "magical_book_thunderbolt",
			"magical_book_venom_blast", "magical_book_return", "magical_book_heal", "magical_book_holy_bless", "magical_book_storage", "magical_book_warp",
			"magical_book_unknown", "magical_book_torch", "magical_book_summon", "magical_book_compositing");
		registerModels(ELIXIR, "elixir", "elixir_medium", "elixir_high");
		registerModel(CAVENIC_BOW, "cavenic_bow");
		registerModel(MANALITE_SWORD, "manalite_sword");
		registerModel(MANALITE_AXE, "manalite_axe");
		registerModel(MANALITE_BOW, "manalite_bow");
	}

	@SideOnly(Side.CLIENT)
	public static void registerModel(Item item, String modelName)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(CaveUtils.getKey(modelName), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels(Item item, String... modelNames)
	{
		List<ModelResourceLocation> models = Lists.newArrayList();

		for (String model : modelNames)
		{
			models.add(new ModelResourceLocation(CaveUtils.getKey(model), "inventory"));
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
	public static void registerVanillaModels(Item item, String... modelNames)
	{
		List<ModelResourceLocation> models = Lists.newArrayList();

		for (String model : modelNames)
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
		OreDictionary.registerOre("gemAquamarine", ItemCave.EnumType.AQUAMARINE.getItemStack());
		OreDictionary.registerOre("ingotMagnite", ItemCave.EnumType.MAGNITE_INGOT.getItemStack());
		OreDictionary.registerOre("gemHexcite", ItemCave.EnumType.HEXCITE.getItemStack());
		OreDictionary.registerOre("stickIce", ItemCave.EnumType.ICE_STICK.getItemStack());
		OreDictionary.registerOre("gemManalite", ItemCave.EnumType.MANALITE.getItemStack());
	}

	public static void registerEquipments()
	{
		AQUAMARINE.setRepairItem(ItemCave.EnumType.AQUAMARINE.getItemStack());
		MAGNITE.setRepairItem(ItemCave.EnumType.MAGNITE_INGOT.getItemStack());
		HEXCITE.setRepairItem(ItemCave.EnumType.HEXCITE.getItemStack());
		ICE.setRepairItem(new ItemStack(Blocks.PACKED_ICE));
		MANALITE.setRepairItem(ItemCave.EnumType.MANALITE.getItemStack());

		IceEquipment.register(ICE_SWORD);
		IceEquipment.register(ICE_PICKAXE);
		IceEquipment.register(ICE_AXE);
		IceEquipment.register(ICE_SHOVEL);
		IceEquipment.register(ICE_HOE);
		IceEquipment.register(ICE_BOW);

		MAGICAL_ITEMS.add(MAGICAL_BOOK);
		MAGICAL_ITEMS.add(ELIXIR);
		MAGICAL_ITEMS.add(MANALITE_SWORD);
		MAGICAL_ITEMS.add(MANALITE_AXE);
		MAGICAL_ITEMS.add(MANALITE_BOW);
	}

	public static void registerRecipes(IForgeRegistry<IRecipe> registry)
	{
		registry.register(RecipeHelper.getSwordRecipe("ice_sword",
			IceEquipment.getChargedItem(ICE_SWORD, 3), new ItemStack(Blocks.ICE), "stickIce"));
		registry.register(RecipeHelper.getPickaxeRecipe("ice_pickaxe",
			IceEquipment.getChargedItem(ICE_PICKAXE, 4), new ItemStack(Blocks.ICE), "stickIce"));
		registry.register(RecipeHelper.getAxeRecipe("ice_axe",
			IceEquipment.getChargedItem(ICE_AXE, 4), new ItemStack(Blocks.ICE), "stickIce"));
		registry.register(RecipeHelper.getShovelRecipe("ice_shovel",
			IceEquipment.getChargedItem(ICE_SHOVEL, 2), new ItemStack(Blocks.ICE), "stickIce"));
		registry.register(RecipeHelper.getHoeRecipe("ice_hoe",
			IceEquipment.getChargedItem(ICE_HOE, 3), new ItemStack(Blocks.ICE), "stickIce"));

		registry.register(RecipeHelper.getSwordRecipe("ice_sword", "ice_sword_packed",
			IceEquipment.getChargedItem(ICE_SWORD, 19), new ItemStack(Blocks.PACKED_ICE), "stickIce"));
		registry.register(RecipeHelper.getPickaxeRecipe("ice_pickaxe", "ice_pickaxe_packed",
			IceEquipment.getChargedItem(ICE_PICKAXE, 28), new ItemStack(Blocks.PACKED_ICE), "stickIce"));
		registry.register(RecipeHelper.getAxeRecipe("ice_axe", "ice_axe_packed",
			IceEquipment.getChargedItem(ICE_AXE, 28), new ItemStack(Blocks.PACKED_ICE), "stickIce"));
		registry.register(RecipeHelper.getShovelRecipe("ice_shovel", "ice_shovel_packed",
			IceEquipment.getChargedItem(ICE_SHOVEL, 10), new ItemStack(Blocks.PACKED_ICE), "stickIce"));
		registry.register(RecipeHelper.getHoeRecipe("ice_hoe", "ice_hoe_packed",
			IceEquipment.getChargedItem(ICE_HOE, 19), new ItemStack(Blocks.PACKED_ICE), "stickIce"));

		registry.register(RecipeHelper.getBowRecipe("ice_bow",
			IceEquipment.getChargedItem(ICE_BOW, 1), ItemCave.EnumType.ICE_STICK.getItemStack()));
		registry.register(RecipeHelper.getSmallSurroundRecipe("ice_bow", "ice_bow_normal",
			IceEquipment.getChargedItem(ICE_BOW, 4), new ItemStack(Items.BOW, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Blocks.ICE)));
		registry.register(RecipeHelper.getSmallSurroundRecipe("ice_bow", "ice_bow_packed",
			IceEquipment.getChargedItem(ICE_BOW, 36), new ItemStack(Items.BOW, 1, OreDictionary.WILDCARD_VALUE), new ItemStack(Blocks.PACKED_ICE)));

		registry.register(RecipeHelper.getRecipe("charge_ice_equip", new RecipeChargeIceEquipment()));
	}

	public static boolean hasMagicalItem(EntityPlayer player, boolean handOnly)
	{
		if (handOnly)
		{
			for (ItemStack held : player.getHeldEquipment())
			{
				if (!held.isEmpty() && MAGICAL_ITEMS.contains(held.getItem()))
				{
					return true;
				}
			}

			return false;
		}

		for (int i = 0; i < player.inventory.getSizeInventory(); ++i)
		{
			ItemStack stack = player.inventory.getStackInSlot(i);

			if (!stack.isEmpty() && MAGICAL_ITEMS.contains(stack.getItem()))
			{
				return true;
			}
		}

		return false;
	}
}