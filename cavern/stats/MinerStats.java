package cavern.stats;

import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import cavern.api.IMineBonus;
import cavern.api.IMinerStats;
import cavern.api.event.MinerStatsEvent;
import cavern.capability.CaveCapabilities;
import cavern.config.MiningAssistConfig;
import cavern.core.CaveSounds;
import cavern.miningassist.MiningAssist;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.MinerStatsAdjustMessage;
import cavern.stats.bonus.MineBonusExperience;
import cavern.stats.bonus.MineBonusHaste;
import cavern.stats.bonus.MineBonusResistance;
import cavern.util.BlockMeta;
import cavern.util.CaveUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class MinerStats implements IMinerStats
{
	public static final Table<Block, Integer, Integer> MINING_POINTS = HashBasedTable.create();
	public static final Set<IMineBonus> MINE_BONUS = Sets.newHashSet();

	public static BlockMeta lastMine;
	public static int lastMinePoint;

	@SideOnly(Side.CLIENT)
	public static long lastMineTime;
	@SideOnly(Side.CLIENT)
	public static int mineCombo;

	private final EntityPlayer entityPlayer;

	private int point = -1;
	private int rank;
	private int miningAssist;

	private boolean clientAdjusted;

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

		MinerRank current = MinerRank.get(rank);
		int max = MinerRank.values().length - 1;
		boolean promoted = false;

		while (current.getRank() < max)
		{
			MinerRank next = MinerRank.get(rank + 1);

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

				server.getPlayerList().sendMessage(component);

				double x = player.posX;
				double y = player.posY + player.getEyeHeight();
				double z = player.posZ;

				player.getServerWorld().playSound(null, x, y, z, CaveSounds.RANK_PROMOTE, SoundCategory.MASTER, 0.85F, 1.0F);

				switch (current)
				{
					case IRON_MINER:
						CaveUtils.grantAdvancement(player, "iron_miner");
						break;
					case GOLD_MINER:
						CaveUtils.grantAdvancement(player, "gold_miner");
						break;
					case HEXCITE_MINER:
						CaveUtils.grantAdvancement(player, "hexcite_miner");
						break;
					case DIAMOND_MINER:
						CaveUtils.grantAdvancement(player, "diamond_miner");
						break;
					default:
				}

				if (current.getRank() >= MiningAssistConfig.minerRank.getValue())
				{
					CaveUtils.grantToast(player, "mining_assist");
				}
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

		rank = MinerRank.get(value).getRank();

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
	public int getMiningAssist()
	{
		return miningAssist;
	}

	@Override
	public void setMiningAssist(int type)
	{
		setMiningAssist(type, true);
	}

	@Override
	public void setMiningAssist(int type, boolean adjust)
	{
		int prev = miningAssist;

		miningAssist = MiningAssist.get(type).getType();

		if (miningAssist != prev)
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
	public void toggleMiningAssist()
	{
		setMiningAssist(miningAssist + 1);
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
			CaveNetworkRegistry.sendTo(new MinerStatsAdjustMessage(this), (EntityPlayerMP)entityPlayer);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("Point", getPoint());
		nbt.setInteger("Rank", getRank());
		nbt.setInteger("MiningAssist", getMiningAssist());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		setPoint(nbt.getInteger("Point"), false);
		setRank(nbt.getInteger("Rank"), false);
		setMiningAssist(nbt.getInteger("MiningAssist"), false);
	}

	public static IMinerStats get(EntityPlayer player)
	{
		return get(player, false);
	}

	public static IMinerStats get(EntityPlayer player, boolean nullable)
	{
		IMinerStats stats = CaveCapabilities.getCapability(player, CaveCapabilities.MINER_STATS);

		if (stats == null)
		{
			return nullable ? null : new MinerStats(player);
		}

		return stats;
	}

	public static int getPointAmount(Block block, int meta)
	{
		Integer ret = MINING_POINTS.get(block, meta);

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
				MINING_POINTS.put(block, i, amount);
			}
		}
		else
		{
			MINING_POINTS.put(block, meta, amount);
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

	public static void setPointAmount(String name, int amount)
	{
		NonNullList<ItemStack> ores = OreDictionary.getOres(name, false);

		if (ores.isEmpty())
		{
			return;
		}

		for (ItemStack stack : ores)
		{
			if (stack.isEmpty())
			{
				continue;
			}

			Block block = Block.getBlockFromItem(stack.getItem());

			if (block == null || block == Blocks.AIR)
			{
				continue;
			}

			setPointAmount(block, stack.getMetadata(), amount);
		}
	}

	public static void setLastMine(BlockMeta blockMeta, int point)
	{
		lastMine = blockMeta;
		lastMinePoint = point;
	}

	public static void registerMineBonus()
	{
		MINE_BONUS.add(new MineBonusExperience());
		MINE_BONUS.add(new MineBonusHaste());
		MINE_BONUS.add(new MineBonusResistance());
	}
}