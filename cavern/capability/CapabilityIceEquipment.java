package cavern.capability;

import cavern.api.IIceEquipment;
import cavern.item.IceEquipment;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityIceEquipment implements ICapabilitySerializable<NBTTagCompound>
{
	private final IIceEquipment equip;

	public CapabilityIceEquipment()
	{
		this.equip = new IceEquipment();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return CaveCapabilities.ICE_EQUIP != null && capability == CaveCapabilities.ICE_EQUIP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (CaveCapabilities.ICE_EQUIP != null && capability == CaveCapabilities.ICE_EQUIP)
		{
			return CaveCapabilities.ICE_EQUIP.cast(equip);
		}

		return null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		if (CaveCapabilities.ICE_EQUIP != null)
		{
			return (NBTTagCompound)CaveCapabilities.ICE_EQUIP.getStorage().writeNBT(CaveCapabilities.ICE_EQUIP, equip, null);
		}

		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (CaveCapabilities.ICE_EQUIP != null)
		{
			CaveCapabilities.ICE_EQUIP.getStorage().readNBT(CaveCapabilities.ICE_EQUIP, equip, null, nbt);
		}
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(IIceEquipment.class,
			new Capability.IStorage<IIceEquipment>()
			{
				@Override
				public NBTBase writeNBT(Capability<IIceEquipment> capability, IIceEquipment instance, EnumFacing side)
				{
					NBTTagCompound nbt = new NBTTagCompound();

					instance.writeToNBT(nbt);

					return nbt;
				}

				@Override
				public void readNBT(Capability<IIceEquipment> capability, IIceEquipment instance, EnumFacing side, NBTBase nbt)
				{
					instance.readFromNBT((NBTTagCompound)nbt);
				}
			},
			IceEquipment::new
		);
	}
}