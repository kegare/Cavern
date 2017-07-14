package cavern.item;

import javax.annotation.Nullable;

import cavern.api.CavernAPI;
import cavern.api.IPortalCache;
import cavern.core.Cavern;
import cavern.stats.PortalCache;
import cavern.util.CaveUtils;
import cavern.world.CaveType;
import cavern.world.TeleporterCavenia;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class ItemCave extends Item
{
	public ItemCave()
	{
		super();
		this.setUnlocalizedName("itemCave");
		this.setHasSubtypes(true);
		this.setCreativeTab(Cavern.TAB_CAVERN);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		return "item." + EnumType.byItemStack(stack).getUnlocalizedName();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		if (!isInCreativeTab(tab))
		{
			return;
		}

		for (EnumType type : EnumType.VALUES)
		{
			subItems.add(type.getItemStack());
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack held = player.getHeldItem(hand);

		switch (EnumType.byItemStack(held))
		{
			case CAVENIC_ORB:
				if (!transferByCavenia(player))
				{
					break;
				}

				if (!player.capabilities.isCreativeMode)
				{
					held.shrink(1);
				}

				return new ActionResult<>(EnumActionResult.SUCCESS, held);
			default:
		}

		return super.onItemRightClick(world, player, hand);
	}

	public boolean transferByCavenia(@Nullable EntityPlayer entityPlayer)
	{
		if (!CavernAPI.dimension.isEntityInCaves(entityPlayer) || CavernAPI.dimension.isCaveniaDisabled())
		{
			return false;
		}

		if (!(entityPlayer instanceof EntityPlayerMP))
		{
			return false;
		}

		EntityPlayerMP player = (EntityPlayerMP)entityPlayer;
		IPortalCache cache = PortalCache.get(player);
		ResourceLocation key = CaveUtils.getKey("cavenia");
		MinecraftServer server = player.mcServer;
		DimensionType dimOld = player.world.provider.getDimensionType();
		DimensionType dimNew = CavernAPI.dimension.isEntityInCavenia(player) ? cache.getLastDim(key, CaveType.DIM_CAVERN) : CaveType.DIM_CAVENIA;
		WorldServer worldOld = server.getWorld(dimOld.getId());
		WorldServer worldNew = server.getWorld(dimNew.getId());
		BlockPos prevPos = player.getPosition();

		double x = player.posX;
		double y = player.posY + player.getEyeHeight();
		double z = player.posZ;

		worldOld.playSound(player, x, y, z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.5F, 0.5F);

		server.getPlayerList().transferPlayerToDimension(player, dimNew.getId(), new TeleporterCavenia(worldNew));

		x = player.posX;
		y = player.posY + player.getEyeHeight();
		z = player.posZ;

		worldNew.playSound(null, x, y, z, SoundEvents.BLOCK_GLASS_FALL, SoundCategory.BLOCKS, 0.75F, 1.0F);

		cache.setLastDim(key, dimOld);
		cache.setLastPos(key, dimOld, prevPos);

		if (player.getBedLocation(dimNew.getId()) == null)
		{
			player.setSpawnChunk(player.getPosition(), true, dimNew.getId());
		}

		return true;
	}

	public enum EnumType
	{
		AQUAMARINE(0, "aquamarine"),
		MAGNITE_INGOT(1, "ingotMagnite"),
		HEXCITE(2, "hexcite"),
		ICE_STICK(3, "stickIce"),
		MINER_ORB(4, "orbMiner"),
		CAVENIC_ORB(5, "orbCavenic"),
		MANALITE(6, "manalite");

		public static final EnumType[] VALUES = new EnumType[values().length];

		private final int meta;
		private final String unlocalizedName;

		private EnumType(int meta, String name)
		{
			this.meta = meta;
			this.unlocalizedName = name;
		}

		public int getMetadata()
		{
			return meta;
		}

		public String getUnlocalizedName()
		{
			return unlocalizedName;
		}

		public ItemStack getItemStack()
		{
			return getItemStack(1);
		}

		public ItemStack getItemStack(int amount)
		{
			return new ItemStack(CaveItems.CAVE_ITEM, amount, getMetadata());
		}

		public static EnumType byMetadata(int meta)
		{
			if (meta < 0 || meta >= VALUES.length)
			{
				meta = 0;
			}

			return VALUES[meta];
		}

		public static EnumType byItemStack(ItemStack stack)
		{
			return byMetadata(stack.isEmpty() ? 0 : stack.getMetadata());
		}

		static
		{
			for (EnumType type : values())
			{
				VALUES[type.getMetadata()] = type;
			}
		}
	}
}