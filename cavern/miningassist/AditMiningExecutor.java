package cavern.miningassist;

import java.util.List;

import javax.annotation.Nullable;

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
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class AditMiningExecutor implements IMiningAssistExecutor
{
	private final World world;
	private final EntityPlayer player;
	private final BlockPos originPos;

	private IBlockState originState;

	private BlockPos harvestTarget;

	public AditMiningExecutor(World world, EntityPlayer player, BlockPos origin, @Nullable IBlockState target)
	{
		this.world = world;
		this.player = player;
		this.originPos = origin;
		this.originState = target;
	}

	@Override
	public MiningAssist getType()
	{
		return MiningAssist.ADIT;
	}

	public List<BlockMeta> getTargetBlocks()
	{
		return MiningAssistConfig.aditTargetBlocks.getBlocks();
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

			if (harvestTarget != null)
			{
				if (Cavern.proxy.isSinglePlayer())
				{
					IBlockState state = world.getBlockState(harvestTarget);

					if (im.tryHarvestBlock(harvestTarget))
					{
						if (!player.capabilities.isCreativeMode)
						{
							world.playEvent(null, 2001, harvestTarget, Block.getStateId(state));
						}
					}
				}
				else
				{
					im.tryHarvestBlock(harvestTarget);
				}
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

		if (harvestTarget == null)
		{
			return 0;
		}

		return 1;
	}

	protected void check()
	{
		if (originState == null)
		{
			originState = world.getBlockState(originPos);
		}

		harvestTarget = null;

		if (BlockPistonBase.getFacingFromEntity(originPos, player).getAxis() == Axis.Y)
		{
			return;
		}

		if (originPos.getY() == MathHelper.floor_double(player.posY + 0.5D))
		{
			offer(originPos.up());
		}
		else
		{
			offer(originPos.down());
		}
	}

	protected boolean offer(BlockPos target)
	{
		if (validTarget(target))
		{
			harvestTarget = target;

			return true;
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