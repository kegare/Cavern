package cavern.magic;

import cavern.api.IMagicianStats;
import cavern.network.CaveNetworkRegistry;
import cavern.network.server.MagicInvisibleMessage;
import cavern.network.server.MagicResultMessage;
import cavern.stats.MagicianStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagicInvisible implements IMagic
{
	private final int magicLevel;
	private final long magicSpellTime;

	private boolean invisible;

	public MagicInvisible(int level, long time)
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
	public boolean onSpellingTick(ItemStack stack, EnumHand hand, long spellingTime, long magicSpellTime, double progress)
	{
		if (!invisible && spellingTime > 300L)
		{
			Minecraft mc = FMLClientHandler.instance().getClient();
			EntityPlayer player = mc.player;
			IMagicianStats stats = MagicianStats.get(player);
			int cost = getMagicCost(player, mc.world, stack, hand);

			if (cost > 0 && stats.getMP() < cost)
			{
				mc.ingameGUI.setOverlayMessage(I18n.format("cavern.magic.mp.short"), false);

				return false;
			}

			stats.setInvisible(true);

			CaveNetworkRegistry.sendToServer(new MagicInvisibleMessage(true, cost));

			invisible = true;
		}

		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void onStopSpelling(ItemStack stack, EnumHand hand, long spellingTime, double progress)
	{
		if (!invisible)
		{
			return;
		}

		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.player;

		MagicianStats.get(player).setInvisible(false);

		CaveNetworkRegistry.sendToServer(new MagicInvisibleMessage(false, 0));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getSpellingSpeed(int spellingSpeed)
	{
		return invisible ? 30 : spellingSpeed;
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
		return 25 * getMagicLevel();
	}

	@Override
	public int getMagicPoint(EntityPlayer player, World world, ItemStack stack, EnumHand hand)
	{
		return invisible ? IMagic.super.getMagicPoint(player, world, stack, hand) : 0;
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