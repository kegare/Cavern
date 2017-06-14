package cavern.capability;

import cavern.api.IInventoryEquipment;
import cavern.item.InventoryEquipment;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityInventoryEquipment implements ICapabilitySerializable<NBTTagCompound>
{
	private final IInventoryEquipment equip;

	public CapabilityInventoryEquipment()
	{
		this.equip = new InventoryEquipment();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return CaveCapabilities.INVENTORY_EQUIP != null && capability == CaveCapabilities.INVENTORY_EQUIP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (CaveCapabilities.INVENTORY_EQUIP != null && capability == CaveCapabilities.INVENTORY_EQUIP)
		{
			return CaveCapabilities.INVENTORY_EQUIP.cast(equip);
		}

		return null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		if (CaveCapabilities.INVENTORY_EQUIP != null)
		{
			return (NBTTagCompound)CaveCapabilities.INVENTORY_EQUIP.getStorage().writeNBT(CaveCapabilities.INVENTORY_EQUIP, equip, null);
		}

		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (CaveCapabilities.INVENTORY_EQUIP != null)
		{
			CaveCapabilities.INVENTORY_EQUIP.getStorage().readNBT(CaveCapabilities.INVENTORY_EQUIP, equip, null, nbt);
		}
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(IInventoryEquipment.class,
			new Capability.IStorage<IInventoryEquipment>()
			{
				@Override
				public NBTBase writeNBT(Capability<IInventoryEquipment> capability, IInventoryEquipment instance, EnumFacing side)
				{
					NBTTagCompound nbt = new NBTTagCompound();

					instance.writeToNBT(nbt);

					return nbt;
				}

				@Override
				public void readNBT(Capability<IInventoryEquipment> capability, IInventoryEquipment instance, EnumFacing side, NBTBase nbt)
				{
					instance.readFromNBT((NBTTagCompound)nbt);
				}
			},
			() -> new InventoryEquipment()
		);
	}
}