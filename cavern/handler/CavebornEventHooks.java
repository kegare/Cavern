package cavern.handler;

import java.util.Set;

import com.google.common.collect.Sets;

import cavern.block.BlockPortalCavern;
import cavern.config.GeneralConfig;
import cavern.config.property.ConfigCaveborn;
import cavern.world.TeleporterCavern;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class CavebornEventHooks
{
	public static final Set<String> FIRST_PLAYERS = Sets.newHashSet();

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

			if (caveborn != ConfigCaveborn.Type.DISABLED && FIRST_PLAYERS.contains(player.getUniqueID().toString()))
			{
				MinecraftServer server = player.mcServer;

				server.addScheduledTask(() ->
				{
					BlockPortalCavern portal = caveborn.getPortalBlock();

					if (portal != null)
					{
						int dim = portal.getDimension();
						Teleporter teleporter = new TeleporterCavern(server.worldServerForDimension(dim), portal);

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
				});
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event)
	{
		FIRST_PLAYERS.remove(event.player.getUniqueID().toString());
	}
}