package cavern.stats;

import org.apache.commons.lang3.ObjectUtils;

import cavern.api.IMagicianStats;
import cavern.api.event.MagicianStatsEvent;
import cavern.capability.CaveCapabilities;
import cavern.core.CaveSounds;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.MagicInfinityMessage;
import cavern.network.client.MagicianStatsAdjustMessage;
import cavern.util.CaveUtils;
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

public class MagicianStats implements IMagicianStats
{
	private final EntityPlayer entityPlayer;

	private int point = -1;
	private int rank;
	private int mp = -1;

	private long refreshTime;

	private int infinityLevel;
	private int infinityTime;

	private boolean clientAdjusted;

	public MagicianStats(EntityPlayer player)
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

		if (point != prev)
		{
			if (adjust)
			{
				adjustData();
			}

			if (entityPlayer != null && entityPlayer.world.isRemote)
			{
				clientAdjusted = true;
			}
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
		MagicianStatsEvent.AddPoint event = new MagicianStatsEvent.AddPoint(entityPlayer, this, value);

		if (MinecraftForge.EVENT_BUS.post(event))
		{
			return;
		}

		setPoint(point + event.getNewPoint(), adjust);

		if (entityPlayer != null && value > 0 && point > 0 && point % 100 == 0)
		{
			entityPlayer.addExperience(entityPlayer.xpBarCap() / 2);
		}

		MagicianRank current = MagicianRank.get(rank);
		int max = MagicianRank.values().length - 1;
		boolean promoted = false;

		while (current.getRank() < max)
		{
			MagicianRank next = MagicianRank.get(rank + 1);

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

				ITextComponent component = new TextComponentTranslation("cavern.magicianrank.promoted", player.getDisplayName(), name);
				component.getStyle().setColor(TextFormatting.GRAY).setItalic(true);

				server.getPlayerList().sendMessage(component);

				double x = player.posX;
				double y = player.posY + player.getEyeHeight();
				double z = player.posZ;

				player.getServerWorld().playSound(null, x, y, z, CaveSounds.RANK_PROMOTE, SoundCategory.MASTER, 0.85F, 1.0F);

				switch (current)
				{
					case MAGICIAN:
						CaveUtils.grantAdvancement(player, "magician");
						break;
					case MAGE:
						CaveUtils.grantAdvancement(player, "mage");
						break;
					case GRAND_MAGE:
						CaveUtils.grantAdvancement(player, "grand_mage");
						break;
					default:
				}
			}

			MinecraftForge.EVENT_BUS.post(new MagicianStatsEvent.PromoteRank(entityPlayer, this));
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

		rank = MagicianRank.get(value).getRank();

		if (rank != prev)
		{
			if (adjust)
			{
				adjustData();
			}

			if (entityPlayer != null && entityPlayer.world.isRemote)
			{
				clientAdjusted = true;
			}
		}
	}

	@Override
	public int getMP()
	{
		return getInfinity() > 0 ? Integer.MAX_VALUE : mp;
	}

	@Override
	public void setMP(int value)
	{
		setMP(value, true);
	}

	@Override
	public void setMP(int value, boolean adjust)
	{
		int max = MagicianRank.get(rank).getMaxMP(entityPlayer);
		int prev = mp;

		mp = MathHelper.clamp(value, 0, max);

		if (mp != prev)
		{
			if (adjust)
			{
				adjustData();
			}

			if (entityPlayer != null && entityPlayer.world.isRemote)
			{
				clientAdjusted = true;
			}
		}
	}

	@Override
	public void addMP(int value)
	{
		addMP(value, true);
	}

	@Override
	public void addMP(int value, boolean adjust)
	{
		if (value < 0 && getInfinity() > 0)
		{
			return;
		}

		setMP(mp + value, adjust);
	}

	@Override
	public int getInfinity()
	{
		return infinityTime > 0 ? infinityLevel : 0;
	}

	@Override
	public void setInfinity(int level, int time)
	{
		infinityLevel = level;
		infinityTime = time;

		if (entityPlayer != null && entityPlayer instanceof EntityPlayerMP)
		{
			CaveNetworkRegistry.sendTo(new MagicInfinityMessage(infinityLevel), (EntityPlayerMP)entityPlayer);
		}
	}

	@Override
	public boolean isClientAdjusted()
	{
		return clientAdjusted;
	}

	@Override
	public void adjustData()
	{
		if (entityPlayer != null && entityPlayer instanceof EntityPlayerMP)
		{
			CaveNetworkRegistry.sendTo(new MagicianStatsAdjustMessage(this), (EntityPlayerMP)entityPlayer);
		}
	}

	@Override
	public void onUpdate()
	{
		if (entityPlayer != null && !entityPlayer.world.isRemote)
		{
			long time = System.currentTimeMillis();

			if (refreshTime <= 0L)
			{
				refreshTime = time;
			}
			else if (time - refreshTime >= 10000L)
			{
				addMP(getRank() + 1);

				refreshTime = time;
			}

			if (infinityTime > 0 && --infinityTime <= 0)
			{
				setInfinity(0, 0);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("Point", getPoint());
		nbt.setInteger("Rank", getRank());
		nbt.setInteger("MP", getMP());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		setPoint(nbt.getInteger("Point"), false);
		setRank(nbt.getInteger("Rank"), false);
		setMP(nbt.getInteger("MP"), false);
	}

	public static IMagicianStats get(EntityPlayer player)
	{
		return get(player, false);
	}

	public static IMagicianStats get(EntityPlayer player, boolean nullable)
	{
		return ObjectUtils.defaultIfNull(CaveCapabilities.getCapability(player, CaveCapabilities.MAGICIAN_STATS), nullable ? null : new MagicianStats(player));
	}
}