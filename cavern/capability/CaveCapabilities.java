package cavern.capability;

import javax.annotation.Nullable;

import cavern.api.IHunterStats;
import cavern.api.IIceEquipment;
import cavern.api.IInventoryEquipment;
import cavern.api.IMagicianStats;
import cavern.api.IMinerStats;
import cavern.api.IPlayerData;
import cavern.api.IPortalCache;
import cavern.item.IceEquipment;
import cavern.item.ItemMagicalBook;
import cavern.item.OreCompass;
import cavern.util.CaveUtils;
import cavern.world.WorldCachedData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CaveCapabilities
{
	@CapabilityInject(IPortalCache.class)
	public static Capability<IPortalCache> PORTAL_CACHE = null;
	@CapabilityInject(IPlayerData.class)
	public static Capability<IPlayerData> PLAYER_DATA = null;
	@CapabilityInject(IMinerStats.class)
	public static Capability<IMinerStats> MINER_STATS = null;
	@CapabilityInject(IHunterStats.class)
	public static Capability<IHunterStats> HUNTER_STATS = null;
	@CapabilityInject(IMagicianStats.class)
	public static Capability<IMagicianStats> MAGICIAN_STATS = null;
	@CapabilityInject(IIceEquipment.class)
	public static Capability<IIceEquipment> ICE_EQUIP = null;
	@CapabilityInject(IInventoryEquipment.class)
	public static Capability<IInventoryEquipment> INVENTORY_EQUIP = null;
	@CapabilityInject(OreCompass.class)
	public static Capability<OreCompass> ORE_COMPASS = null;
	@CapabilityInject(WorldCachedData.class)
	public static Capability<WorldCachedData> WORLD_CACHED_DATA = null;

	public static void registerCapabilities()
	{
		CapabilityPortalCache.register();
		CapabilityPlayerData.register();
		CapabilityMinerStats.register();
		CapabilityHunterStats.register();
		CapabilityMagicianStats.register();
		CapabilityIceEquipment.register();
		CapabilityInventoryEquipment.register();
		CapabilityOreCompass.register();
		CapabilityWorldCachedData.register();

		MinecraftForge.EVENT_BUS.register(new CaveCapabilities());
	}

	public static <T> boolean isValid(Capability<T> capability)
	{
		return capability != null;
	}

	public static <T> boolean hasCapability(ICapabilityProvider entry, Capability<T> capability)
	{
		return entry != null && isValid(capability) && entry.hasCapability(capability, null);
	}

	@Nullable
	public static <T> T getCapability(ICapabilityProvider entry, Capability<T> capability)
	{
		return hasCapability(entry, capability) ? entry.getCapability(capability, null) : null;
	}

	@SubscribeEvent
	public void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
	{
		event.addCapability(CaveUtils.getKey("PortalCache"), new CapabilityPortalCache());

		if (event.getObject() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getObject();

			event.addCapability(CaveUtils.getKey("PlayerData"), new CapabilityPlayerData());
			event.addCapability(CaveUtils.getKey("MinerStats"), new CapabilityMinerStats(player));
			event.addCapability(CaveUtils.getKey("HunterStats"), new CapabilityHunterStats(player));
			event.addCapability(CaveUtils.getKey("MagicianStats"), new CapabilityMagicianStats(player));
		}
	}

	@SubscribeEvent
	public void onAttachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event)
	{
		ItemStack stack = event.getObject();

		if (IceEquipment.isIceEquipment(stack))
		{
			event.addCapability(CaveUtils.getKey("IceEquip"), new CapabilityIceEquipment());
		}

		if (stack.getItem() instanceof ItemMagicalBook)
		{
			switch (ItemMagicalBook.EnumType.byItemStack(stack))
			{
				case STORAGE:
				case COMPOSITING:
					event.addCapability(CaveUtils.getKey("InventoryEquip"), new CapabilityInventoryEquipment());
					break;
				default:
			}
		}
	}

	@SubscribeEvent
	public void onAttachWorldCapabilities(AttachCapabilitiesEvent<World> event)
	{
		World world = event.getObject();

		if (world instanceof WorldServer)
		{
			WorldServer worldServer = (WorldServer)world;

			event.addCapability(CaveUtils.getKey("WorldCachedData"), new CapabilityWorldCachedData(worldServer));
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

		IPlayerData originalPlayerData = getCapability(original, PLAYER_DATA);
		IPlayerData playerData = getCapability(player, PLAYER_DATA);

		if (originalPlayerData != null && playerData != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalPlayerData.writeToNBT(nbt);
			playerData.readFromNBT(nbt);
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

		IMagicianStats originalMagicianStats = getCapability(original, MAGICIAN_STATS);
		IMagicianStats magicianStats = getCapability(player, MAGICIAN_STATS);

		if (originalMagicianStats != null && magicianStats != null)
		{
			NBTTagCompound nbt = new NBTTagCompound();

			originalMagicianStats.writeToNBT(nbt);
			magicianStats.readFromNBT(nbt);
		}
	}
}