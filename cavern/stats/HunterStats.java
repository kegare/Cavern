package cavern.stats;

import cavern.api.IHunterStats;
import cavern.api.event.HunterStatsEvent;
import cavern.capability.CaveCapabilities;
import cavern.core.CaveAchievements;
import cavern.core.CaveSounds;
import cavern.network.CaveNetworkRegistry;
import cavern.network.HunterStatsAdjustMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HunterStats implements IHunterStats
{
	public static int lastHuntPoint;

	@SideOnly(Side.CLIENT)
	public static long lastHuntTime;

	private final EntityPlayer entityPlayer;

	private int point;
	private int rank;

	public HunterStats(EntityPlayer player)
	{
		this.entityPlayer = player;
	}

	@Override
	public int getPoint()
	{
		return point;
	}

	@Override
	public void setPoint(int value)
	{
		setPoint(value, true);
	}

	@Override
	public void setPoint(int value, boolean adjust)
	{
		int prev = point;

		point = Math.max(value, 0);

		if (adjust && point != prev)
		{
			adjustData();
		}
	}

	@Override
	public void addPoint(int value)
	{
		addPoint(value, true);
	}

	@Override
	public void addPoint(int value, boolean adjust)
	{
		HunterStatsEvent.AddPoint event = new HunterStatsEvent.AddPoint(entityPlayer, this, value);

		if (MinecraftForge.EVENT_BUS.post(event))
		{
			return;
		}

		setPoint(point + event.getNewPoint(), adjust);

		if (entityPlayer != null && value > 0 && point > 0 && point % 100 == 0)
		{
			entityPlayer.addExperience(entityPlayer.xpBarCap() / 2);
		}

		HunterRank current = HunterRank.get(rank);
		int max = HunterRank.values().length - 1;
		boolean promoted = false;

		while (current.getRank() < max)
		{
			HunterRank next = HunterRank.get(rank + 1);

			if (point >= next.getPhase())
			{
				++rank;

				promoted = true;
				current = next;

				setPoint(point - current.getPhase(), false);
			}
			else break;
		}

		if (promoted)
		{
			if (adjust)
			{
				adjustData();
			}

			if (entityPlayer != null && entityPlayer instanceof EntityPlayerMP)
			{
				EntityPlayerMP player = (EntityPlayerMP)entityPlayer;
				MinecraftServer server = player.mcServer;

				ITextComponent name = new TextComponentTranslation(current.getUnlocalizedName());
				name.getStyle().setBold(true);

				ITextComponent component = new TextComponentTranslation("cavern.hunterrank.promoted", player.getDisplayName(), name);
				component.getStyle().setColor(TextFormatting.GRAY).setItalic(true);

				server.getPlayerList().sendMessage(component);

				double x = player.posX;
				double y = player.posY + player.getEyeHeight();
				double z = player.posZ;

				player.getServerWorld().playSound(null, x, y, z, CaveSounds.RANK_PROMOTE, SoundCategory.MASTER, 0.85F, 1.0F);

				switch (current)
				{
					case HUNTER:
						player.addStat(CaveAchievements.HUNTER);
						break;
					case CAVENIC_HUNTER:
						player.addStat(CaveAchievements.CAVENIC_HUNTER);
						break;
					case CRAZY_HUNTER:
						player.addStat(CaveAchievements.CRAZY_HUNTER);
						break;
					case CRAZY_RANGER:
						player.addStat(CaveAchievements.CRAZY_RANGER);
						break;
					default:
				}
			}

			MinecraftForge.EVENT_BUS.post(new HunterStatsEvent.PromoteRank(entityPlayer, this));
		}
	}

	@Override
	public int getRank()
	{
		return rank;
	}

	@Override
	public void setRank(int value)
	{
		setRank(value, true);
	}

	@Override
	public void setRank(int value, boolean adjust)
	{
		int prev = rank;

		rank = MathHelper.clamp(value, 0, HunterRank.values().length - 1);

		if (adjust && rank != prev)
		{
			adjustData();
		}
	}

	@Override
	public void adjustData()
	{
		if (entityPlayer != null && entityPlayer instanceof EntityPlayerMP)
		{
			CaveNetworkRegistry.sendTo(new HunterStatsAdjustMessage(this), (EntityPlayerMP)entityPlayer);
		}
	}

	@Override
	public void adjustClientData()
	{
		if (entityPlayer != null)
		{
			CaveNetworkRegistry.sendToServer(new HunterStatsAdjustMessage(this));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("Point", getPoint());
		nbt.setInteger("Rank", getRank());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		setPoint(nbt.getInteger("Point"), false);
		setRank(nbt.getInteger("Rank"), false);
	}

	public static IHunterStats get(EntityPlayer player)
	{
		IHunterStats stats = CaveCapabilities.getCapability(player, CaveCapabilities.HUNTER_STATS);

		if (stats == null)
		{
			return new HunterStats(player);
		}

		return stats;
	}

	public static void setLastHunt(int point)
	{
		lastHuntPoint = point;
	}
}