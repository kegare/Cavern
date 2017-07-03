package cavern.capability;

import cavern.item.OreCompass;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class CapabilityOreCompass implements ICapabilityProvider
{
	private final OreCompass compass;

	public CapabilityOreCompass()
	{
		this.compass = new OreCompass();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return CaveCapabilities.ORE_COMPASS != null && capability == CaveCapabilities.ORE_COMPASS;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (CaveCapabilities.ORE_COMPASS != null && capability == CaveCapabilities.ORE_COMPASS)
		{
			return CaveCapabilities.ORE_COMPASS.cast(compass);
		}

		return null;
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(OreCompass.class,
			new Capability.IStorage<OreCompass>()
			{
				@Override
				public NBTBase writeNBT(Capability<OreCompass> capability, OreCompass instance, EnumFacing side)
				{
					return new NBTTagCompound();
				}

				@Override
				public void readNBT(Capability<OreCompass> capability, OreCompass instance, EnumFacing side, NBTBase nbt) {}
			},
			OreCompass::new
		);
	}
}