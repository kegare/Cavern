package cavern.handler;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.Sets;

import cavern.api.CavernAPI;
import cavern.block.BlockPortalCavern;
import cavern.config.GeneralConfig;
import cavern.config.RuinsCavernConfig;
import cavern.config.property.ConfigCaveborn;
import cavern.util.ItemMeta;
import cavern.world.TeleporterCavern;
import cavern.world.TeleporterRuinsCavern;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class CavebornEventHooks
{
	public static final Set<String> FIRST_PLAYERS = Sets.newHashSet();

	private static final Random RANDOM = new Random();

	@SubscribeEvent
	public void onPlayerLoadFromFile(PlayerEvent.LoadFromFile event)
	{
		String uuid = event.getPlayerUUID();

		for (String str : event.getPlayerDirectory().list())
		{
			if (str.startsWith(uuid))
			{
				return;
			}
		}

		FIRST_PLAYERS.add(uuid);
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			ConfigCaveborn.Type caveborn = GeneralConfig.caveborn.getType();

			if (caveborn != ConfigCaveborn.Type.DISABLED && FIRST_PLAYERS.contains(player.getCachedUniqueIdString()))
			{
				MinecraftServer server = player.mcServer;

				if (caveborn == ConfigCaveborn.Type.RUINS_CAVERN && !CavernAPI.dimension.isRuinsCavernDisabled())
				{
					int dim = RuinsCavernConfig.dimensionId;
					Teleporter teleporter = new TeleporterRuinsCavern(server.getWorld(dim));

					server.getPlayerList().transferPlayerToDimension(player, dim, teleporter);

					WorldServer world = player.getServerWorld();

					world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_STONE_FALL, SoundCategory.BLOCKS, 1.0F, 1.0F);

					player.setSpawnPoint(BlockPos.ORIGIN.up(80), true);

					if (!RuinsCavernConfig.decorateTorches)
					{
						BlockPos pos = player.getPosition();
						double d0 = world.rand.nextFloat() * 0.5F + 0.25D;
						double d1 = world.rand.nextFloat() * 0.5F + 0.25D;
						double d2 = world.rand.nextFloat() * 0.5F + 0.25D;
						EntityItem entityItem = new EntityItem(world, pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2, new ItemStack(Blocks.TORCH, 64));

						entityItem.setPickupDelay(85);

						world.spawnEntity(entityItem);
					}
				}
				else
				{
					BlockPortalCavern portal = caveborn.getPortalBlock();

					if (portal != null && !portal.isDimensionDisabled())
					{
						int dim = portal.getDimension();
						Teleporter teleporter = new TeleporterCavern(server.getWorld(dim), portal);

						boolean force = player.forceSpawn;

						player.forceSpawn = true;
						player.timeUntilPortal = player.getPortalCooldown();

						server.getPlayerList().transferPlayerToDimension(player, dim, teleporter);

						player.forceSpawn = force;

						WorldServer world = player.getServerWorld();
						BlockPos pos = player.getPosition();

						for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-2, -2, -2), pos.add(2, 2, 2)))
						{
							if (world.getBlockState(blockpos).getBlock() == portal)
							{
								world.setBlockToAir(blockpos);

								break;
							}
						}

						double x = player.posX;
						double y = player.posY + player.getEyeHeight();
						double z = player.posZ;

						world.playSound(null, x, y, z, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 0.65F);
					}
				}

				WorldServer world = player.getServerWorld();
				double x = player.posX;
				double y = player.posY + 0.25D;
				double z = player.posZ;

				for (ItemMeta itemMeta : GeneralConfig.cavebornBonusItems.getItems())
				{
					ItemStack stack = itemMeta.getItemStack();

					if (stack.isStackable())
					{
						stack = itemMeta.getItemStack(MathHelper.getInt(RANDOM, 4, 16));
					}

					InventoryHelper.spawnItemStack(world, x, y, z, stack);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		FIRST_PLAYERS.remove(event.player.getCachedUniqueIdString());
	}
}