package cavern.capability;

import cavern.api.IMagicianStats;
import cavern.stats.MagicianStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityMagicianStats implements ICapabilitySerializable<NBTTagCompound>
{
	private final IMagicianStats stats;

	public CapabilityMagicianStats(EntityPlayer player)
	{
		this.stats = new MagicianStats(player);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return CaveCapabilities.MAGICIAN_STATS != null && capability == CaveCapabilities.MAGICIAN_STATS;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (CaveCapabilities.MAGICIAN_STATS != null && capability == CaveCapabilities.MAGICIAN_STATS)
		{
			return CaveCapabilities.MAGICIAN_STATS.cast(stats);
		}

		return null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		if (CaveCapabilities.MAGICIAN_STATS != null)
		{
			return (NBTTagCompound)CaveCapabilities.MAGICIAN_STATS.getStorage().writeNBT(CaveCapabilities.MAGICIAN_STATS, stats, null);
		}

		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (CaveCapabilities.MAGICIAN_STATS != null)
		{
			CaveCapabilities.MAGICIAN_STATS.getStorage().readNBT(CaveCapabilities.MAGICIAN_STATS, stats, null, nbt);
		}
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(IMagicianStats.class,
			new Capability.IStorage<IMagicianStats>()
			{
				@Override
				public NBTBase writeNBT(Capability<IMagicianStats> capability, IMagicianStats instance, EnumFacing side)
				{
					NBTTagCompound nbt = new NBTTagCompound();

					instance.writeToNBT(nbt);

					return nbt;
				}

				@Override
				public void readNBT(Capability<IMagicianStats> capability, IMagicianStats instance, EnumFacing side, NBTBase nbt)
				{
					instance.readFromNBT((NBTTagCompound)nbt);
				}
			},
			() -> new MagicianStats(null)
		);
	}
}