package cavern.item;

import java.util.List;

import com.google.common.collect.Lists;

import cavern.api.IIceEquipment;
import cavern.capability.CaveCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class IceEquipment implements IIceEquipment
{
	public static final IceEquipment EMPTY = new IceEquipment();

	public static final List<Item> EQUIPS = Lists.newArrayList();

	private int charge;

	@Override
	public int getCharge()
	{
		return charge;
	}

	@Override
	public void setCharge(int amount)
	{
		charge = amount;
	}

	@Override
	public void addCharge(int amount)
	{
		charge += amount;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("Charge", charge);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		charge = nbt.getInteger("Charge");
	}

	public static void register(Item item)
	{
		if (item != null && !EQUIPS.contains(item))
		{
			EQUIPS.add(item);
		}
	}

	public static boolean isIceEquipment(Item item)
	{
		return item != null && EQUIPS.contains(item);
	}

	public static boolean isIceEquipment(ItemStack item)
	{
		return item != null && isIceEquipment(item.getItem());
	}

	public static IIceEquipment get(ItemStack item)
	{
		IIceEquipment equip = CaveCapabilities.getCapability(item, CaveCapabilities.ICE_EQUIP);

		if (equip == null)
		{
			return EMPTY;
		}

		return equip;
	}

	public static ItemStack getChargedItem(Item item, int amount)
	{
		ItemStack itemstack = new ItemStack(item);

		get(itemstack).setCharge(amount);

		return itemstack;
	}
}