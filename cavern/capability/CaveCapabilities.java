package cavern.capability;

import cavern.api.IHunterStats;
import cavern.api.IIceEquipment;
import cavern.api.IMinerStats;
import cavern.core.Cavern;
import cavern.item.IceEquipment;
import cavern.stats.IPortalCache;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CaveCapabilities
{
	@CapabilityInject(IPortalCache.class)
	public static Capability<IPortalCache> PORTAL_CACHE = null;
	@CapabilityInject(IMinerStats.class)
	public static Capability<IMinerStats> MINER_STATS = null;
	@CapabilityInject(IHunterStats.class)
	public static Capability<IHunterStats> HUNTER_STATS = null;
	@CapabilityInject(IIceEquipment.class)
	public static Capability<IIceEquipment> ICE_EQUIP = null;

	public static final ResourceLocation PORTAL_CACHE_ID = new ResourceLocation(Cavern.MODID, "PortalCache");
	public static final ResourceLocation MINER_STATS_ID = new ResourceLocation(Cavern.MODID, "MinerStats");
	public static final ResourceLocation HUNTER_STATS_ID = new ResourceLocation(Cavern.MODID, "HunterStats");
	public static final ResourceLocation ICE_EQUIP_ID = new ResourceLocation(Cavern.MODID, "IceEquip");

	public static void registerCapabilities()
	{
		CapabilityPortalCache.register();
		CapabilityMinerStats.register();
		CapabilityHunterStats.register();
		CapabilityIceEquipment.register();

		MinecraftForge.EVENT_BUS.register(new CaveCapabilities());
	}

	public static <T> boolean isValid(Capability<T> capability)
	{
		return capability != null;
	}

	public static <T> boolean hasCapability(ICapabilitySerializable<NBTTagCompound> entry, Capability<T> capability)
	{
		return entry != null && isValid(capability) && entry.hasCapability(capability, null);
	}

	public static <T> T getCapability(ICapabilitySerializable<NBTTagCompound> entry, Capability<T> capability)
	{
		return hasCapability(entry, capability) ? entry.getCapability(capability, null) : null;
	}

	@SubscribeEvent
	public void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
	{
		event.addCapability(PORTAL_CACHE_ID, new CapabilityPortalCache());

		if (event.getObject() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getObject();

			event.addCapability(MINER_STATS_ID, new CapabilityMinerStats(player));
			event.addCapability(HUNTER_STATS_ID, new CapabilityHunterStats(player));
		}
	}

	@SubscribeEvent
	public void onAttachItemCapabilities(AttachCapabilitiesEvent<Item> event)
	{
		if (IceEquipment.isIceEquipment(event.getObject()))
		{
			event.addCapability(ICE_EQUIP_ID, new CapabilityIceEquipment());
		}
	}

	@SubscribeEvent
	public void onPlayerClone(PlayerEvent.Clone event)
	{
		EntityPlayer player = event.getEntityPlayer();

		if (player.world.isRemote)
		{
			return;
		}

		EntityPlayer original = event.getOriginal();

		IPortalCache originalPortalCache = getCapability(original, PORTAL_CACHE);
		IPortalCache portalCache = getCapability(player, PORTAL_CACHE);

		if (originalPortalCache != null && portalCache != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalPortalCache.writeToNBT(nbt);
			portalCache.readFromNBT(nbt);
		}

		IMinerStats originalMinerStats = getCapability(original, MINER_STATS);
		IMinerStats minerStats = getCapability(player, MINER_STATS);

		if (originalMinerStats != null && minerStats != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalMinerStats.writeToNBT(nbt);
			minerStats.readFromNBT(nbt);
		}

		IHunterStats originalHunterStats = getCapability(original, HUNTER_STATS);
		IHunterStats hunterStats = getCapability(player, HUNTER_STATS);

		if (originalHunterStats != null && hunterStats != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalHunterStats.writeToNBT(nbt);
			hunterStats.readFromNBT(nbt);
		}
	}
}