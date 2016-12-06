package cavern.miningassist;

import java.util.Deque;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Queues;

import cavern.handler.MiningAssistEventHooks;
import cavern.util.CaveUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class QuickMiningExecutor implements IMiningAssistExecutor
{
	private static final int[][] CHECK_OFFSETS = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}, {-1, 0, 0}, {0, -1, 0}, {0, 0, -1}};

	private final World world;
	private final EntityPlayer player;
	private final BlockPos originPos;

	private IBlockState originState;
	private int targetBlockLimit;

	private BlockPos currentPos;

	private Deque<BlockPos> harvestTargets;

	public QuickMiningExecutor(World world, EntityPlayer player, BlockPos origin, @Nullable IBlockState target)
	{
		this.world = world;
		this.player = player;
		this.originPos = origin;
		this.originState = target;
		this.targetBlockLimit = 50;
	}

	@Override
	public MiningAssist getType()
	{
		return MiningAssist.QUICK;
	}

	public int getTargetBlockLimit()
	{
		return targetBlockLimit;
	}

	public QuickMiningExecutor setTargetBlockLimit(int amount)
	{
		targetBlockLimit = amount;

		return this;
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

			if (harvestTargets != null)
			{
				PlayerInteractionManager im = thePlayer.interactionManager;

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

			if (exp > 0 && !player.capabilities.isCreativeMode && world.getGameRules().getBoolean("doTileDrops"))
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

		currentPos = originPos;
		harvestTargets = Queues.newLinkedBlockingDeque(targetBlockLimit);

		checkChain();
	}

	protected void checkChain()
	{
		if (currentPos == null || harvestTargets == null)
		{
			return;
		}

		boolean flag;

		do
		{
			flag = false;

			BlockPos pos = currentPos;

			for (int[] offset : CHECK_OFFSETS)
			{
				if (offer(pos.add(offset[0], offset[1], offset[2])))
				{
					checkChain();

					if (!flag)
					{
						flag = true;
					}
				}
			}
		}
		while (flag);
	}

	protected boolean offer(BlockPos target)
	{
		if (validTarget(target) && !harvestTargets.contains(target))
		{
			currentPos = target;

			return harvestTargets.offerLast(target);
		}

		return false;
	}

	private boolean isRedstoneOre(IBlockState state)
	{
		return state.getBlock() == Blocks.REDSTONE_ORE || state.getBlock() == Blocks.LIT_REDSTONE_ORE;
	}

	protected boolean validTarget(BlockPos target)
	{
		IBlockState state = world.getBlockState(target);

		if (state.getBlock().isAir(state, world, target))
		{
			return false;
		}

		if (isRedstoneOre(originState) && isRedstoneOre(state))
		{
			return true;
		}

		return CaveUtils.areBlockStatesEqual(originState, state);
	}
}