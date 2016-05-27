package cavern.item;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.block.BlockCave;
import cavern.block.CaveBlocks;
import cavern.core.Cavern;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
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

	public static final Item cave_item = new ItemCave();
	public static final ItemPickaxeAquamarine aquamarine_pickaxe = new ItemPickaxeAquamarine();
	public static final ItemAxeAquamarine aquamarine_axe = new ItemAxeAquamarine();
	public static final ItemShovelAquamarine aquamarine_shovel = new ItemShovelAquamarine();
	public static final ItemSwordCave magnite_sword = new ItemSwordCave(MAGNITE, "swordMagnite");
	public static final ItemPickaxeCave magnite_pickaxe = new ItemPickaxeCave(MAGNITE, "pickaxeMagnite");
	public static final ItemAxeCave magnite_axe = new ItemAxeCave(MAGNITE, 12.0F, -2.0F, "axeMagnite");
	public static final ItemShovelCave magnite_shovel = new ItemShovelCave(MAGNITE, "shovelMagnite");

	public static void registerItems()
	{
		cave_item.setRegistryName("cave_item");
		aquamarine_pickaxe.setRegistryName("aquamarine_pickaxe");
		aquamarine_axe.setRegistryName("aquamarine_axe");
		aquamarine_shovel.setRegistryName("aquamarine_shovel");
		magnite_sword.setRegistryName("magnite_sword");
		magnite_pickaxe.setRegistryName("magnite_pickaxe");
		magnite_axe.setRegistryName("magnite_axe");
		magnite_shovel.setRegistryName("magnite_shovel");

		GameRegistry.register(cave_item);
		GameRegistry.register(aquamarine_pickaxe);
		GameRegistry.register(aquamarine_axe);
		GameRegistry.register(aquamarine_shovel);
		GameRegistry.register(magnite_sword);
		GameRegistry.register(magnite_pickaxe);
		GameRegistry.register(magnite_axe);
		GameRegistry.register(magnite_shovel);
	}

	@SideOnly(Side.CLIENT)
	public static void registerModels()
	{
		registerModelWithMeta(cave_item, "aquamarine", "magnite_ingot");
		registerModel(aquamarine_pickaxe, "aquamarine_pickaxe");
		registerModel(aquamarine_axe, "aquamarine_axe");
		registerModel(aquamarine_shovel, "aquamarine_shovel");
		registerModel(magnite_sword, "magnite_sword");
		registerModel(magnite_pickaxe, "magnite_pickaxe");
		registerModel(magnite_axe, "magnite_axe");
		registerModel(magnite_shovel, "magnite_shovel");
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
		GameRegistry.addShapelessRecipe(new ItemStack(cave_item, 9, ItemCave.EnumType.AQUAMARINE.getItemDamage()),
			new ItemStack(CaveBlocks.cave_block, 1, BlockCave.EnumType.AQUAMARINE_BLOCK.getMetadata()));

		GameRegistry.addShapelessRecipe(new ItemStack(cave_item, 9, ItemCave.EnumType.MAGNITE_INGOT.getItemDamage()),
			new ItemStack(CaveBlocks.cave_block, 1, BlockCave.EnumType.MAGNITE_BLOCK.getMetadata()));

		ItemStack material = new ItemStack(cave_item, 1, ItemCave.EnumType.AQUAMARINE.getItemDamage());

		GameRegistry.addRecipe(new ShapedOreRecipe(aquamarine_pickaxe,
			"AAA", " S ", " S ",
			'A', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(aquamarine_axe,
			"AA", "AS", " S",
			'A', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(aquamarine_shovel,
			"A", "S", "S",
			'A', material.copy(),
			'S', "stickWood"
		));

		material = new ItemStack(cave_item, 1, ItemCave.EnumType.MAGNITE_INGOT.getItemDamage());

		GameRegistry.addRecipe(new ShapedOreRecipe(magnite_sword,
			"M", "M", "S",
			'M', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(magnite_pickaxe,
			"MMM", " S ", " S ",
			'M', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(magnite_axe,
			"MM", "MS", " S",
			'M', material.copy(),
			'S', "stickWood"
		));

		GameRegistry.addRecipe(new ShapedOreRecipe(magnite_shovel,
			"M", "S", "S",
			'M', material.copy(),
			'S', "stickWood"
		));
	}
}