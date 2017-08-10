package cavern.magic;

import cavern.network.CaveNetworkRegistry;
import cavern.network.server.MagicResultMessage;
import cavern.network.server.MagicTeleportMessage;
import cavern.stats.MagicianStats;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicTeleport implements IMagic
{
	private final int magicLevel;
	private final long magicSpellTime;

	private boolean teleport;

	public MagicTeleport(int level, long time)
	{
		this.magicLevel = level;
		this.magicSpellTime = time;
	}

	@Override
	public boolean isClientMagic()
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onStopSpelling(ItemStack stack, EnumHand hand, long spellingTime, double progress)
	{
		int factor = getMagicLevel() + 1;
		int dist = MathHelper.floor(spellingTime / 500) * factor;

		if (dist <= 0)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.player;

		if (!player.capabilities.isCreativeMode)
		{
			dist = Math.min(dist, MathHelper.floor(MagicianStats.get(player).getMP() / 10));

			if (dist <= 0)
			{
				return;
			}
		}

		CaveNetworkRegistry.sendToServer(new MagicTeleportMessage(dist, factor));

		teleport = true;
	}

	@Override
	public int getMagicLevel()
	{
		return magicLevel;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public long getMagicSpellTime(ItemStack stack, EnumHand hand)
	{
		return magicSpellTime;
	}

	@Override
	public int getMagicCost(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return 5;
	}

	@Override
	public int getMagicPoint(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return teleport ? IMagic.super.getMagicPoint(player, world, stack, hand) : 0;
	}

	@Override
	public boolean executeMagic(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void sendMagicResult(int cost, int point, boolean finish)
	{
		if (!finish)
		{
			CaveNetworkRegistry.sendToServer(new MagicResultMessage(0, point));
		}
	}

	@Override
	public SoundEvent getMagicSound()
	{
		return null;
	}
}