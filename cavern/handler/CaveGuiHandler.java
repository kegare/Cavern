package cavern.handler;

import cavern.client.gui.GuiStorage;
import cavern.inventory.ContainerStorage;
import cavern.item.InventoryEquipment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
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

				return new ContainerStorage(player.inventory, getStorage(player, x, y), player);
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

				return new GuiStorage(player.inventory, getStorage(player, x, y), player);
			default:
		}

		return null;
	}

	public IInventory getStorage(EntityPlayer player, int index, int size)
	{
		return InventoryEquipment.getInventory(index == 0 ? player.getHeldItemMainhand() : player.getHeldItemOffhand(), size);
	}
}