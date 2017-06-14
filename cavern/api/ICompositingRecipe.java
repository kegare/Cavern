package cavern.api;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public interface ICompositingRecipe
{
	public NonNullList<ItemStack> getMaterialItems();

	public boolean matches(IInventory inventory, World world, @Nullable EntityPlayer player);

	public ItemStack getCompositingResult(IInventory inventory, World world, @Nullable EntityPlayer player);

	public int getCostMP(IInventory inventory, World world, @Nullable EntityPlayer player);

	public default ItemStack getRecipeOutput()
	{
		return ItemStack.EMPTY;
	}
}