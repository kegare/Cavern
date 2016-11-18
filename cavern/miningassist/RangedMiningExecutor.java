package cavern.miningassist;

import java.util.Deque;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.Queues;

import cavern.config.MiningAssistConfig;
import cavern.core.Cavern;
import cavern.handler.MiningAssistEventHooks;
import cavern.util.BlockMeta;
import cavern.util.CaveUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RangedMiningExecutor implements IMiningAssistExecutor
{
	private final World world;
	private final EntityPlayer player;
	private final BlockPos originPos;

	private IBlockState originState;
	private int range;

	private Deque<BlockPos> harvestTargets;

	public RangedMiningExecutor(World world, EntityPlayer player, BlockPos origin, @Nullable IBlockState target)
	{
		this.world = world;
		this.player = player;
		this.originPos = origin;
		this.originState = target;
		this.range = 1;
	}

	@Override
	public MiningAssist getType()
	{
		return MiningAssist.RANGED;
	}

	public List<BlockMeta> getTargetBlocks()
	{
		return MiningAssistConfig.rangedTargetBlocks.getBlocks();
	}

	public int getRange()
	{
		return range;
	}

	public RangedMiningExecutor setRange(int amount)
	{
		range = amount;

		return this;
	}

	@Override
	public void start()
	{
		if (world.isRemote)
		{
			return;
		}

		if (player != null && player instanceof EntityPlayerMP)
		{
			EntityPlayerMP thePlayer = (EntityPlayerMP)player;
			PlayerInteractionManager im = thePlayer.interactionManager;

			check();

			MiningAssistEventHooks.captureDrops(true);
			MiningAssistEventHooks.captureExps(true);

			if (harvestTargets != null)
			{
				do
				{
					BlockPos pos = harvestTargets.pollFirst();

					if (pos != null)
					{
						if (Cavern.proxy.isSinglePlayer())
						{
							IBlockState state = world.getBlockState(pos);

							if (im.tryHarvestBlock(pos))
							{
								if (!player.capabilities.isCreativeMode)
								{
									world.playEvent(null, 2001, pos, Block.getStateId(state));
								}

								continue;
							}
						}
						else if (im.tryHarvestBlock(pos))
						{
							continue;
						}
					}

					break;
				}
				while (!harvestTargets.isEmpty());
			}

			List<ItemStack> drops = MiningAssistEventHooks.captureDrops(false);

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

					world.spawnEntityInWorld(new EntityXPOrb(world, originPos.getX() + 0.5D, originPos.getY() + 0.5D, originPos.getZ() + 0.5D, i));
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

		switch (BlockPistonBase.getFacingFromEntity(originPos, player).getAxis())
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
		for (int i = -range; i <= range; ++i)
		{
			for (int j = -range; j <= range; ++j)
			{
				offer(originPos.add(0, i, j));
			}
		}
	}

	protected void checkY()
	{
		for (int i = -range; i <= range; ++i)
		{
			for (int j = -range; j <= range; ++j)
			{
				offer(originPos.add(i, 0, j));
			}
		}
	}

	protected void checkZ()
	{
		for (int i = -range; i <= range; ++i)
		{
			for (int j = -range; j <= range; ++j)
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

		if (state.getBlock().isAir(state, world, target))
		{
			return false;
		}

		if (getTargetBlocks().isEmpty())
		{
			ItemStack held = player.getHeldItemMainhand();

			if (held != null)
			{
				return held.canHarvestBlock(state);
			}
		}
		else if (getTargetBlocks().contains(new BlockMeta(state)))
		{
			return true;
		}

		return CaveUtils.areBlockStatesEqual(originState, state);
	}
}