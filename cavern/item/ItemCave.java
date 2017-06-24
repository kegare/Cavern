package cavern.item;

import cavern.api.CavernAPI;
import cavern.config.CaveniaConfig;
import cavern.core.Cavern;
import cavern.stats.IPortalCache;
import cavern.stats.PortalCache;
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
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCave extends Item
{
	public static final int CAVENIA = 10;

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

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
	{
		if (!isInCreativeTab(tab))
		{
			return;
		}

		for (EnumType type : EnumType.values())
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

	public boolean transferByCavenia(EntityPlayer entityPlayer)
	{
		if (!CavernAPI.dimension.isEntityInCaves(entityPlayer))
		{
			return false;
		}

		if (!(entityPlayer instanceof EntityPlayerMP))
		{
			return false;
		}

		EntityPlayerMP player = (EntityPlayerMP)entityPlayer;
		IPortalCache cache = PortalCache.get(player);
		MinecraftServer server = player.mcServer;
		int dimOld = player.dimension;
		int dimNew = CavernAPI.dimension.isEntityInCavenia(player) ? cache.getLastDim(CAVENIA) : CaveniaConfig.dimensionId;
		WorldServer worldOld = server.getWorld(dimOld);
		WorldServer worldNew = server.getWorld(dimNew);
		BlockPos prevPos = player.getPosition();

		double x = player.posX;
		double y = player.posY + player.getEyeHeight();
		double z = player.posZ;

		worldOld.playSound(player, x, y, z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.5F, 0.5F);

		server.getPlayerList().transferPlayerToDimension(player, dimNew, new TeleporterCavenia(worldNew));

		x = player.posX;
		y = player.posY + player.getEyeHeight();
		z = player.posZ;

		worldNew.playSound(null, x, y, z, SoundEvents.BLOCK_GLASS_FALL, SoundCategory.BLOCKS, 0.75F, 1.0F);

		cache.setLastDim(CAVENIA, dimOld);
		cache.setLastPos(CAVENIA, dimOld, prevPos);

		if (player.getBedLocation(dimNew) == null)
		{
			player.setSpawnChunk(player.getPosition(), true, dimNew);
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

		private static final EnumType[] DAMAGE_LOOKUP = new EnumType[values().length];

		private final int itemDamage;
		private final String unlocalizedName;

		private EnumType(int damage, String name)
		{
			this.itemDamage = damage;
			this.unlocalizedName = name;
		}

		public int getItemDamage()
		{
			return itemDamage;
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
			return new ItemStack(CaveItems.CAVE_ITEM, amount, getItemDamage());
		}

		public static EnumType byDamage(int damage)
		{
			if (damage < 0 || damage >= DAMAGE_LOOKUP.length)
			{
				damage = 0;
			}

			return DAMAGE_LOOKUP[damage];
		}

		public static EnumType byItemStack(ItemStack itemstack)
		{
			return byDamage(itemstack == null ? 0 : itemstack.getItemDamage());
		}

		static
		{
			for (EnumType type : values())
			{
				DAMAGE_LOOKUP[type.getItemDamage()] = type;
			}
		}
	}
}