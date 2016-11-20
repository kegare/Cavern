package cavern.handler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cavern.api.IMinerStats;
import cavern.client.CaveKeyBindings;
import cavern.config.GeneralConfig;
import cavern.config.MiningAssistConfig;
import cavern.core.Cavern;
import cavern.miningassist.AditMiningExecutor;
import cavern.miningassist.IMiningAssistExecutor;
import cavern.miningassist.MiningAssist;
import cavern.miningassist.QuickMiningExecutor;
import cavern.miningassist.RangedMiningExecutor;
import cavern.network.CaveNetworkRegistry;
import cavern.network.server.MiningAssistMessage;
import cavern.stats.MinerRank;
import cavern.stats.MinerStats;
import cavern.util.BlockMeta;
import cavern.util.BreakSpeedCache;
import cavern.util.CaveUtils;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MiningAssistEventHooks
{
	protected static final ThreadLocal<Boolean> captureDrops = new ThreadLocal<Boolean>()
	{
		@Override
		protected Boolean initialValue()
		{
			return false;
		}
	};

	protected static final ThreadLocal<List<ItemStack>> capturedDrops = new ThreadLocal<List<ItemStack>>()
	{
		@Override
		protected List<ItemStack> initialValue()
		{
			return Lists.newArrayList();
		}
	};

	public static List<ItemStack> captureDrops(boolean start)
	{
		if (!MiningAssistConfig.collectDrops)
		{
			return Collections.emptyList();
		}

		if (start)
		{
			captureDrops.set(true);
			capturedDrops.get().clear();

			return null;
		}
		else
		{
			captureDrops.set(false);

			return capturedDrops.get();
		}
	}

	protected static final ThreadLocal<Boolean> captureExps = new ThreadLocal<Boolean>()
	{
		@Override
		protected Boolean initialValue()
		{
			return false;
		}
	};

	protected static final ThreadLocal<Integer> capturedExps = new ThreadLocal<Integer>()
	{
		@Override
		protected Integer initialValue()
		{
			return Integer.valueOf(0);
		}
	};

	public static int captureExps(boolean start)
	{
		if (!MiningAssistConfig.collectExps)
		{
			return 0;
		}

		if (start)
		{
			captureExps.set(true);
			capturedExps.set(Integer.valueOf(0));

			return 0;
		}
		else
		{
			captureExps.set(false);

			return capturedExps.get().intValue();
		}
	}

	public static boolean canMiningAssist(EntityPlayer player, IBlockState state)
	{
		if (GeneralConfig.isMiningPointItem(player.getHeldItemMainhand()))
		{
			IMinerStats stats = MinerStats.get(player);

			if (stats.getMiningAssist() > MiningAssist.DISABLED.getType() && stats.getRank() >= MiningAssistConfig.minerRank.getValue())
			{
				MiningAssist type = MiningAssist.get(stats.getMiningAssist());
				List<BlockMeta> targets = null;

				switch (type)
				{
					case QUICK:
						targets = MiningAssistConfig.quickTargetBlocks.getBlocks();
						break;
					case RANGED:
						targets = MiningAssistConfig.rangedTargetBlocks.getBlocks();
						break;
					case ADIT:
						targets = MiningAssistConfig.aditTargetBlocks.getBlocks();
						break;
					default:
				}

				if (targets == null || targets.isEmpty())
				{
					switch (type)
					{
						case QUICK:
							return state.getBlock() instanceof BlockOre || state.getBlock() instanceof BlockRedstoneOre || MinerStats.getPointAmount(state) > 0;
						case RANGED:
						case ADIT:
							return player.getHeldItemMainhand().canHarvestBlock(state);
						default:
					}

					return false;
				}

				return targets.contains(new BlockMeta(state));
			}
		}

		return false;
	}

	public static IMiningAssistExecutor createExecutor(@Nullable MiningAssist type, World world, EntityPlayer player, BlockPos origin, @Nullable IBlockState target)
	{
		if (type == null)
		{
			type = MiningAssist.get(MinerStats.get(player).getMiningAssist());
		}

		switch (type)
		{
			case QUICK:
				return new QuickMiningExecutor(world, player, origin, target).setTargetBlockLimit(MiningAssistConfig.quickMiningLimit);
			case RANGED:
				return new RangedMiningExecutor(world, player, origin, target).setRange(MiningAssistConfig.rangedMining);
			case ADIT:
				return new AditMiningExecutor(world, player, origin, target);
			default:
		}

		return null;
	}

	private static final Map<BlockPos, BreakSpeedCache> SPEED_CACHES = Maps.newHashMap();

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onHarvestDrops(HarvestDropsEvent event)
	{
		World world = event.getWorld();

		if (captureDrops.get())
		{
			List<ItemStack> drops = event.getDrops();
			float chance = event.getDropChance();

			for (ItemStack stack : drops)
			{
				if (world.rand.nextFloat() < chance)
				{
					capturedDrops.get().add(stack);
				}
			}

			drops.clear();
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBlockBreak(BreakEvent event)
	{
		if (captureExps.get())
		{
			int i = capturedExps.get();

			capturedExps.set(Integer.valueOf(i + event.getExpToDrop()));

			event.setExpToDrop(0);
		}

		EntityPlayer player = event.getPlayer();
		World world = event.getWorld();

		if (player != null && !world.isRemote)
		{
			IBlockState state = event.getState();

			if (canMiningAssist(player, state))
			{
				if (captureDrops.get())
				{
					return;
				}

				BlockPos pos = event.getPos();
				IMiningAssistExecutor executor = createExecutor(null, world, player, pos, state);

				executor.start();
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBreakSpeed(BreakSpeed event)
	{
		EntityPlayer player = event.getEntityPlayer();
		IBlockState state = event.getState();

		if (MiningAssistConfig.modifiedHardness && Cavern.proxy.isSinglePlayer() && canMiningAssist(player, state))
		{
			BlockPos pos = event.getPos();
			BreakSpeedCache cache = SPEED_CACHES.get(pos);
			long time = System.currentTimeMillis();
			float cachedSpeed = 0.0F;

			if (cache != null)
			{
				if (CaveUtils.areBlockStatesEqual(state, cache.getBlockState()) && time - cache.getCachedTime() <= 3000L)
				{
					cachedSpeed = cache.getBreakSpeed();
				}
				else
				{
					SPEED_CACHES.remove(pos);

					return;
				}
			}

			if (cachedSpeed > 0.0F)
			{
				event.setNewSpeed(cachedSpeed);

				return;
			}

			IMiningAssistExecutor executor = createExecutor(null, player.worldObj, player, pos, state);
			MinerRank rank = MinerRank.get(MinerStats.get(player).getRank());
			float speed = event.getNewSpeed();
			float power = rank.getBoost() * 1.7145F;
			float newSpeed = Math.min(speed / (executor.calc() * (0.5F - power * 0.1245F)), speed);

			event.setNewSpeed(newSpeed);

			SPEED_CACHES.put(pos, new BreakSpeedCache(state, newSpeed, time));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event)
	{
		if (!Keyboard.getEventKeyState())
		{
			return;
		}

		int key = Keyboard.getEventKey();
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (CaveKeyBindings.KEY_MINING_ASSIST.isActiveAndMatches(key))
		{
			if (mc.thePlayer != null)
			{
				CaveNetworkRegistry.sendToServer(new MiningAssistMessage());
			}
		}
	}
}