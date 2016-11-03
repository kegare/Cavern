package cavern.api;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface ICavernAPI
{
	public IMinerStats getMinerStats(EntityPlayer player);

	public Set<IMineBonus> getMineBonus();

	public void addMineBonus(IMineBonus bonus);

	public void addRandomiteItem(ItemStack item, int weight);

	public void addFissureBreakEvent(IFissureBreakEvent event, int weight);

	public void registerIceEquipment(Item item);

	public boolean isIceEquipment(Item item);

	public IIceEquipment getIceEquipment(ItemStack item);
}