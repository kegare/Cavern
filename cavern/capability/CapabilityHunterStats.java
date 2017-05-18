package cavern.capability;

import cavern.api.IHunterStats;
import cavern.stats.HunterStats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityHunterStats implements ICapabilitySerializable<NBTTagCompound>
{
	private final HunterStats stats;

	public CapabilityHunterStats(EntityPlayer player)
	{
		this.stats = new HunterStats(player);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return CaveCapabilities.HUNTER_STATS != null && capability == CaveCapabilities.HUNTER_STATS;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		if (CaveCapabilities.HUNTER_STATS != null && capability == CaveCapabilities.HUNTER_STATS)
		{
			return CaveCapabilities.HUNTER_STATS.cast(stats);
		}

		return null;
	}

	@Override
	public NBTTagCompound serializeNBT()
	{
		if (CaveCapabilities.HUNTER_STATS != null)
		{
			return (NBTTagCompound)CaveCapabilities.HUNTER_STATS.getStorage().writeNBT(CaveCapabilities.HUNTER_STATS, stats, null);
		}

		return new NBTTagCompound();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt)
	{
		if (CaveCapabilities.HUNTER_STATS != null)
		{
			CaveCapabilities.HUNTER_STATS.getStorage().readNBT(CaveCapabilities.HUNTER_STATS, stats, null, nbt);
		}
	}

	public static void register()
	{
		CapabilityManager.INSTANCE.register(IHunterStats.class,
			new Capability.IStorage<IHunterStats>()
			{
				@Override
				public NBTBase writeNBT(Capability<IHunterStats> capability, IHunterStats instance, EnumFacing side)
				{
					NBTTagCompound nbt = new NBTTagCompound();

					instance.writeToNBT(nbt);

					return nbt;
				}

				@Override
				public void readNBT(Capability<IHunterStats> capability, IHunterStats instance, EnumFacing side, NBTBase nbt)
				{
					instance.readFromNBT((NBTTagCompound)nbt);
				}
			},
			() -> new HunterStats(null)
		);
	}
}