package cavern.api.event;

import cavern.api.ICompositingRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerCompositedEvent extends PlayerEvent
{
	private final ICompositingRecipe recipe;
	private final IInventory inventory;

	public PlayerCompositedEvent(EntityPlayer player, ICompositingRecipe recipe, IInventory inventory)
	{
		super(player);
		this.recipe = recipe;
		this.inventory = inventory;
	}

	public ICompositingRecipe getRecipe()
	{
		return recipe;
	}

	public IInventory getInventory()
	{
		return inventory;
	}
}