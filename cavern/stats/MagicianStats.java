package cavern.stats;

import javax.annotation.Nullable;

import cavern.api.IMagicianStats;
import cavern.api.event.MagicianStatsEvent;
import cavern.capability.CaveCapabilities;
import cavern.core.CaveDamageSources;
import cavern.core.CaveSounds;
import cavern.magic.IMagic;
import cavern.magic.IMagic.IEntityMagic;
import cavern.magic.IMagic.IPlainMagic;
import cavern.magic.IMagic.IPlayerMagic;
import cavern.magic.IMagicCallback;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.MagicianStatsAdjustMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class MagicianStats implements IMagicianStats
{
	private final EntityPlayer entityPlayer;

	private int point = -1;
	private int rank;
	private int mp = -1;

	private long refreshTime;
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
		return mp;
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
		setMP(mp + value, adjust);
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
		IMagicianStats stats = CaveCapabilities.getCapability(player, CaveCapabilities.MAGICIAN_STATS);

		if (stats == null)
		{
			return nullable ? null : new MagicianStats(player);
		}

		return stats;
	}

	public static boolean executeMagic(EntityPlayer player, IMagic magic)
	{
		return executeMagic(player, magic, null);
	}

	public static boolean executeMagic(EntityPlayer player, IMagic magic, @Nullable IMagicCallback callback)
	{
		IMagicianStats stats = get(player);
		boolean hasCallback = callback != null;

		if (hasCallback ? callback.isHigherMagic(player, stats, magic) : !player.capabilities.isCreativeMode && magic.getMagicLevel() > stats.getRank() + 1)
		{
			player.sendStatusMessage(new TextComponentTranslation("cavern.magicianstats.rank.short"), true);

			return false;
		}

		int cost = hasCallback ? callback.getMagicCostMP(player, stats, magic) : magic.getCostMP(player);
		int mp = stats.getMP();

		if (cost > 0 && !player.capabilities.isCreativeMode && cost > 0 && mp < cost)
		{
			player.sendStatusMessage(new TextComponentTranslation("cavern.magicianstats.mp.short"), true);
			player.attackEntityFrom(CaveDamageSources.EXHAUST_MP, MathHelper.clamp(3 * magic.getMagicLevel(), 1, 10));

			return false;
		}

		World world = player.world;
		double range = magic.getMagicRange(player);
		boolean success = false;

		if (magic instanceof IEntityMagic)
		{
			IEntityMagic entityMagic = (IEntityMagic)magic;
			int count = 0;

			for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(player, player.getEntityBoundingBox().grow(range)))
			{
				if (entity != null && player.canEntityBeSeen(entity) && entityMagic.isTarget(player, entity) && entityMagic.execute(player, entity))
				{
					++count;
				}
			}

			if (count > 0)
			{
				success = true;
			}
		}

		if (magic instanceof IPlayerMagic)
		{
			IPlayerMagic playerMagic = (IPlayerMagic)magic;
			int count = 0;

			for (EntityPlayer targetPlayer : world.getEntitiesWithinAABB(EntityPlayer.class, player.getEntityBoundingBox().grow(range)))
			{
				if (targetPlayer != null && player.canEntityBeSeen(targetPlayer) && playerMagic.isTarget(player, targetPlayer) && playerMagic.execute(player, targetPlayer))
				{
					++count;
				}
			}

			if (count > 0)
			{
				success = true;
			}
		}

		if (magic instanceof IPlainMagic)
		{
			IPlainMagic plainMagic = (IPlainMagic)magic;

			success = plainMagic.execute(player);
		}

		if (hasCallback && callback.onSuccessMagic(player, stats, magic, success))
		{
			return callback.isSuccessMagic(player, stats, magic, success);
		}

		if (success)
		{
			if (!player.capabilities.isCreativeMode)
			{
				stats.addMP(-cost);
			}

			stats.addPoint(magic.getMagicPoint(player));

			SoundEvent sound = magic.getMagicSound();

			if (sound != null)
			{
				world.playSound(null, player.posX, player.posY, player.posZ, sound, SoundCategory.PLAYERS, 0.35F, 1.0F);
			}

			return !hasCallback || callback.isSuccessMagic(player, stats, magic, success);
		}

		ITextComponent message = magic.getFailedMessage();

		if (message != null)
		{
			player.sendStatusMessage(message, true);
		}

		return hasCallback && callback.isSuccessMagic(player, stats, magic, success);
	}
}