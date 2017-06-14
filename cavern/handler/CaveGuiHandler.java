package cavern.handler;

import cavern.api.IInventoryEquipment;
import cavern.client.gui.GuiCompositing;
import cavern.client.gui.GuiStorage;
import cavern.inventory.ContainerCompositing;
import cavern.inventory.ContainerStorage;
import cavern.item.InventoryEquipment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CaveGuiHandler implements IGuiHandler
{
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
			case 0:
				if (x < 0 || y <= 0)
				{
					break;
				}

				return new ContainerStorage(player.inventory, getInventory(player, x, y), player);
			case 1:
				if (x < 0 || y <= 0)
				{
					break;
				}

				return new ContainerCompositing(player.inventory, getInventory(player, x, y), player);
			default:
		}

		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		switch (ID)
		{
			case 0:
				if (x < 0 || y <= 0)
				{
					break;
				}

				return new GuiStorage(player.inventory, getInventory(player, x, y), player);
			case 1:
				if (x < 0 || y <= 0)
				{
					break;
				}

				return new GuiCompositing(player.inventory, getInventory(player, x, y), player);
			default:
		}

		return null;
	}

	public IInventory getInventory(EntityPlayer player, int index, int size)
	{
		ItemStack stack = index == 0 ? player.getHeldItemMainhand() : player.getHeldItemOffhand();
		IInventoryEquipment equip = InventoryEquipment.get(stack);
		IInventory inventory = equip.getInventory();

		if (inventory == null)
		{
			inventory = new InventoryBasic("Items", false, 9 * size);

			equip.setInventory(inventory);
		}

		return inventory;
	}
}