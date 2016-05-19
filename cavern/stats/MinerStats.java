package cavern.stats;

import java.util.List;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import cavern.api.IMinerStats;
import cavern.api.event.MinerStatsEvent;
import cavern.capability.CaveCapabilities;
import cavern.core.CaveSounds;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.MinerStatsAdjustMessage;
import cavern.util.BlockMeta;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
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
import net.minecraftforge.oredict.OreDictionary;

public class MinerStats implements IMinerStats
{
	public static final Table<Block, Integer, Integer> pointAmounts = HashBasedTable.create();

	public static BlockMeta lastMine;
	public static int lastMinePoint;

	@SideOnly(Side.CLIENT)
	public static long lastMineDisplayTime;

	private final EntityPlayer entityPlayer;

	private int point;
	private int rank;

	public MinerStats(EntityPlayer player)
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
		MinerStatsEvent.AddPoint event = new MinerStatsEvent.AddPoint(entityPlayer, this, value);

		if (MinecraftForge.EVENT_BUS.post(event))
		{
			return;
		}

		setPoint(point + event.getNewPoint(), adjust);

		if (entityPlayer != null && value > 0 && point > 0 && point % 100 == 0)
		{
			entityPlayer.addExperience(entityPlayer.xpBarCap() / 2);
		}

		MinerRank current = MinerRank.getRank(rank);
		int max = MinerRank.values().length - 1;
		boolean promoted = false;

		while (current.getRank() < max)
		{
			MinerRank next = MinerRank.getRank(rank + 1);

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

				ITextComponent component = new TextComponentTranslation("cavern.minerrank.promoted", player.getDisplayName(), name);
				component.getStyle().setColor(TextFormatting.GRAY).setItalic(true);

				server.getPlayerList().sendChatMsg(component);

				double x = player.posX;
				double y = player.posY + player.getEyeHeight();
				double z = player.posZ;

				player.getServerWorld().playSound(null, x, y, z, CaveSounds.rank_promote, SoundCategory.MASTER, 1.0F, 1.0F);
			}

			MinecraftForge.EVENT_BUS.post(new MinerStatsEvent.PromoteRank(entityPlayer, this));
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

		rank = MathHelper.clamp_int(value, 0, MinerRank.values().length - 1);

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
			CaveNetworkRegistry.sendTo(new MinerStatsAdjustMessage(this), (EntityPlayerMP)entityPlayer);
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

	public static IMinerStats get(EntityPlayer player)
	{
		IMinerStats stats = CaveCapabilities.getEntityCapability(player, CaveCapabilities.MINER_STATS);

		if (stats == null)
		{
			return new MinerStats(player);
		}

		return stats;
	}

	public static int getPointAmount(Block block, int meta)
	{
		Integer ret = pointAmounts.get(block, meta);

		return ret == null ? 0 : ret.intValue();
	}

	public static int getPointAmount(IBlockState state)
	{
		return getPointAmount(state.getBlock(), state.getBlock().getMetaFromState(state));
	}

	public static void setPointAmount(Block block, int meta, int amount)
	{
		if (meta == OreDictionary.WILDCARD_VALUE)
		{
			for (int i = 0; i < 16; ++i)
			{
				pointAmounts.put(block, i, amount);
			}
		}
		else
		{
			pointAmounts.put(block, meta, amount);
		}
	}

	public static void setPointAmount(IBlockState state, int amount)
	{
		setPointAmount(state.getBlock(), state.getBlock().getMetaFromState(state), amount);
	}

	public static void setPointAmount(BlockMeta blockMeta, int amount)
	{
		setPointAmount(blockMeta.getBlock(), blockMeta.getMeta(), amount);
	}

	public static void setPointAmount(String oredict, int amount)
	{
		List<ItemStack> ores = OreDictionary.getOres(oredict);

		if (!ores.isEmpty())
		{
			for (ItemStack entry : ores)
			{
				Block block = Block.getBlockFromItem(entry.getItem());

				if (block != null && block != Blocks.AIR)
				{
					setPointAmount(block, entry.getItemDamage(), amount);
				}
			}
		}
	}
}