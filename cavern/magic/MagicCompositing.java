package cavern.magic;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.api.CavernAPI;
import cavern.api.ICompositingRecipe;
import cavern.api.IMagicianStats;
import cavern.api.event.PlayerCompositedEvent;
import cavern.core.CaveSounds;
import cavern.core.Cavern;
import cavern.item.InventoryEquipment;
import cavern.magic.IMagic.IPlainMagic;
import cavern.recipe.CompositingRecipeBasic;
import cavern.stats.MagicianStats;
import cavern.util.CaveUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class MagicCompositing implements IPlainMagic
{
	private final int magicLevel;
	private final long magicSpellTime;
	private final ItemStack bookItem;

	private int soundType;

	public MagicCompositing(int level, long time, ItemStack stack)
	{
		this.magicLevel = level;
		this.magicSpellTime = time;
		this.bookItem = stack;
	}

	@Override
	public int getMagicLevel()
	{
		return magicLevel;
	}

	@Override
	public long getMagicSpellTime()
	{
		IInventory inventory = InventoryEquipment.get(bookItem).getInventory();

		return inventory == null || inventory.isEmpty() ? magicSpellTime / 4 : magicSpellTime;
	}

	@Override
	public double getMagicRange()
	{
		return 0.0F;
	}

	@Override
	public int getCostMP()
	{
		return 50 * getMagicLevel();
	}

	@Override
	public int getMagicPoint()
	{
		IInventory inventory = InventoryEquipment.get(bookItem).getInventory();

		return inventory == null || inventory.isEmpty() ? 1 * getMagicLevel() : 5 * getMagicLevel();
	}

	@Override
	public SoundEvent getMagicSound()
	{
		switch (soundType)
		{
			case 1:
				return SoundEvents.UI_BUTTON_CLICK;
		}

		return CaveSounds.MAGIC_SUCCESS_MISC;
	}

	@Override
	public boolean execute(EntityPlayer player)
	{
		int index = -1;
		ItemStack held = player.getHeldItemMainhand();

		if (ItemStack.areItemStacksEqual(held, bookItem))
		{
			index = 0;
		}
		else
		{
			held = player.getHeldItemOffhand();

			if (ItemStack.areItemStacksEqual(held, bookItem))
			{
				index = 1;
			}
		}

		if (index < 0)
		{
			return false;
		}

		IInventory inventory = InventoryEquipment.get(bookItem).getInventory();

		if (inventory == null || inventory.isEmpty())
		{
			player.openGui(Cavern.instance, 1, player.world, index, getMagicLevel(), 0);

			soundType = 1;

			return true;
		}

		World world = player.world;
		ICompositingRecipe resultRecipe = null;

		for (ICompositingRecipe recipe : CavernAPI.compositing.getRecipes())
		{
			if (recipe.matches(inventory, world, player))
			{
				resultRecipe = recipe;

				break;
			}
		}

		if (resultRecipe == null)
		{
			InventoryHelper.dropInventoryItems(world, player, inventory);

			return false;
		}

		if (!player.capabilities.isCreativeMode)
		{
			int cost = resultRecipe.getCostMP(inventory, world, player);
			IMagicianStats magician = MagicianStats.get(player);

			if (magician.getMP() >= cost + getCostMP(player))
			{
				magician.addMP(cost);
			}
			else
			{
				InventoryHelper.dropInventoryItems(world, player, inventory);

				return false;
			}
		}

		ItemStack result = resultRecipe.getCompositingResult(inventory, world, player);

		if (result.isEmpty())
		{
			player.sendStatusMessage(new TextComponentTranslation("item.magicalBook.compositing.failed"), true);

			InventoryHelper.dropInventoryItems(world, player, inventory);

			soundType = 1;

			return true;
		}

		NonNullList<ItemStack> materials = resultRecipe.getMaterialItems();
		List<ItemStack> checkList = Lists.newArrayList(materials);

		for (int i = 0; i < inventory.getSizeInventory(); ++i)
		{
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack.isEmpty())
			{
				continue;
			}

			for (ItemStack material : materials)
			{
				if (checkList.contains(material) && CompositingRecipeBasic.isItemMatch(material, stack))
				{
					stack.shrink(material.getCount());

					if (stack.getCount() <= 0)
					{
						inventory.setInventorySlotContents(i, ItemStack.EMPTY);
					}

					checkList.remove(material);

					break;
				}
			}
		}

		PlayerCompositedEvent event = new PlayerCompositedEvent(player, resultRecipe, inventory);

		MinecraftForge.EVENT_BUS.post(event);

		InventoryHelper.spawnItemStack(world, player.posX, player.posY, player.posZ, result);
		InventoryHelper.dropInventoryItems(world, player, inventory);

		soundType = 0;

		CaveUtils.grantAdvancement(player, "magic_compositing");

		return true;
	}
}