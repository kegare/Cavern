package cavern.miningassist;

import java.util.Deque;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Queues;

import cavern.config.MiningAssistConfig;
import cavern.config.property.ConfigBlocks;
import cavern.handler.MiningAssistEventHooks;
import cavern.util.CaveUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RangedMiningExecutor implements IMiningAssistExecutor
{
	private final World world;
	private final EntityPlayer player;
	private final BlockPos originPos;

	private IBlockState originState;
	private int rowRange, columnRange;

	private Deque<BlockPos> harvestTargets;

	public RangedMiningExecutor(World world, EntityPlayer player, BlockPos origin, @Nullable IBlockState target)
	{
		this.world = world;
		this.player = player;
		this.originPos = origin;
		this.originState = target;
		this.rowRange = 1;
		this.columnRange = 1;
	}

	@Override
	public MiningAssist getType()
	{
		return MiningAssist.RANGED;
	}

	public ConfigBlocks getTargetBlocks()
	{
		return MiningAssistConfig.rangedTargetBlocks;
	}

	public int getRowRange()
	{
		return rowRange;
	}

	public int getColumnRange()
	{
		return columnRange;
	}

	public RangedMiningExecutor setRange(int row, int column)
	{
		rowRange = row;
		columnRange = column;

		return this;
	}

	public RangedMiningExecutor setRange(int amount)
	{
		return setRange(amount, amount);
	}

	@Override
	public void execute()
	{
		if (world.isRemote)
		{
			return;
		}

		if (player instanceof EntityPlayerMP)
		{
			EntityPlayerMP thePlayer = (EntityPlayerMP)player;

			check();

			MiningAssistEventHooks.captureDrops(true);
			MiningAssistEventHooks.captureExps(true);

			PlayerInteractionManager im = thePlayer.interactionManager;

			if (harvestTargets != null)
			{
				do
				{
					if (!harvestBlock(im, harvestTargets.pollFirst()))
					{
						break;
					}
				}
				while (!harvestTargets.isEmpty());
			}

			Set<ItemStack> drops = MiningAssistEventHooks.captureDrops(false);

			for (ItemStack item : drops)
			{
				Block.spawnAsEntity(world, originPos, item);
			}

			int exp = MiningAssistEventHooks.captureExps(false);

			if (exp > 0 && !im.isCreative() && world.getGameRules().getBoolean("doTileDrops"))
			{
				while (exp > 0)
				{
					int i = EntityXPOrb.getXPSplit(exp);
					exp -= i;

					world.spawnEntity(new EntityXPOrb(world, originPos.getX() + 0.5D, originPos.getY() + 0.5D, originPos.getZ() + 0.5D, i));
				}
			}
		}
	}

	@Override
	public int calc()
	{
		check();

		if (harvestTargets == null)
		{
			return 0;
		}

		return harvestTargets.size();
	}

	protected void check()
	{
		if (originState == null)
		{
			originState = world.getBlockState(originPos);
		}

		harvestTargets = Queues.newLinkedBlockingDeque(9);

		switch (EnumFacing.getDirectionFromEntityLiving(originPos, player).getAxis())
		{
			case X:
				checkX();
				break;
			case Y:
				checkY();
				break;
			case Z:
				checkZ();
				break;
		}
	}

	protected void checkX()
	{
		for (int i = -columnRange; i <= columnRange; ++i)
		{
			for (int j = -rowRange; j <= rowRange; ++j)
			{
				offer(originPos.add(0, i, j));
			}
		}
	}

	protected void checkY()
	{
		for (int i = -rowRange; i <= rowRange; ++i)
		{
			for (int j = -columnRange; j <= columnRange; ++j)
			{
				offer(originPos.add(i, 0, j));
			}
		}
	}

	protected void checkZ()
	{
		for (int i = -rowRange; i <= rowRange; ++i)
		{
			for (int j = -columnRange; j <= columnRange; ++j)
			{
				offer(originPos.add(i, j, 0));
			}
		}
	}

	protected boolean offer(BlockPos target)
	{
		if (validTarget(target) && !harvestTargets.contains(target))
		{
			return harvestTargets.offerLast(target);
		}

		return false;
	}

	protected boolean validTarget(BlockPos target)
	{
		IBlockState state = world.getBlockState(target);

		if (state.getBlock().isAir(state, world, target) || state.getBlockHardness(world, target) < 0.0F)
		{
			return false;
		}

		if (getTargetBlocks().isEmpty())
		{
			ItemStack held = player.getHeldItemMainhand();

			if (!held.isEmpty())
			{
				return held.canHarvestBlock(state);
			}
		}
		else if (getTargetBlocks().hasBlockState(state))
		{
			return true;
		}

		return CaveUtils.areBlockStatesEqual(originState, state);
	}
}