package cavern.capability;

import cavern.api.IMinerStats;
import cavern.core.Cavern;
import cavern.stats.IPortalCache;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CaveCapabilities
{
	@CapabilityInject(IPortalCache.class)
	public static Capability<IPortalCache> PORTAL_CACHE = null;
	@CapabilityInject(IMinerStats.class)
	public static Capability<IMinerStats> MINER_STATS = null;

	public static final ResourceLocation PORTAL_CACHE_ID = new ResourceLocation(Cavern.MODID, "PortalCache");
	public static final ResourceLocation MINER_STATS_ID = new ResourceLocation(Cavern.MODID, "MinerStats");

	public static void registerCapabilities()
	{
		CapabilityPortalCache.register();
		CapabilityMinerStats.register();

		MinecraftForge.EVENT_BUS.register(new CaveCapabilities());
	}

	public static <T> boolean isValid(Capability<T> capability)
	{
		return capability != null;
	}

	public static <T> boolean hasEntityCapability(Entity entity, Capability<T> capability)
	{
		return entity != null && isValid(capability) && entity.hasCapability(capability, null);
	}

	public static <T> T getEntityCapability(Entity entity, Capability<T> capability)
	{
		return hasEntityCapability(entity, capability) ? entity.getCapability(capability, null) : null;
	}

	@SubscribeEvent
	public void onAttachEntityCapabilities(AttachCapabilitiesEvent.Entity event)
	{
		event.addCapability(PORTAL_CACHE_ID, new CapabilityPortalCache());

		if (event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntity();

			event.addCapability(MINER_STATS_ID, new CapabilityMinerStats(player));
		}
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event)
	{
		EntityPlayer player = event.getEntityPlayer();

		if (player.worldObj.isRemote)
		{
			return;
		}

		EntityPlayer original = event.getOriginal();

		IPortalCache originalPortalCache = getEntityCapability(original, PORTAL_CACHE);
		IPortalCache portalCache = getEntityCapability(player, PORTAL_CACHE);

		if (originalPortalCache != null && portalCache != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalPortalCache.writeToNBT(nbt);
			portalCache.readFromNBT(nbt);
		}

		IMinerStats originalMinerStats = getEntityCapability(original, MINER_STATS);
		IMinerStats minerStats = getEntityCapability(player, MINER_STATS);

		if (originalMinerStats != null && minerStats != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalMinerStats.writeToNBT(nbt);
			minerStats.readFromNBT(nbt);
		}
	}
}