package cavern.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Joiner;

import cavern.network.CaveNetworkRegistry;
import cavern.network.client.RegenerationGuiMessage;
import cavern.network.client.RegenerationGuiMessage.EnumType;
import cavern.stats.PortalCache;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class DimensionRegeneration
{
	public static boolean backup = true;

	public static boolean regenerate(@Nullable DimensionType type, boolean backup)
	{
		if (type == null)
		{
			return false;
		}

		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			if (player.dimension == type.getId())
			{
				CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.FAILED));

				return false;
			}
		}

		WorldServer world = server.getWorld(type.getId());
		File dir = new File(DimensionManager.getCurrentSaveRootDirectory(), world.provider.getSaveFolder());
		ITextComponent name, text;

		name = new TextComponentString(type.getName());
		name.getStyle().setBold(true);

		text = new TextComponentTranslation("cavern.regeneration.regenerating", name);
		text.getStyle().setColor(TextFormatting.GRAY);

		server.getPlayerList().sendMessage(text);

		if (server.isSinglePlayer())
		{
			CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.OPEN));
		}

		CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.START));

		try
		{
			world.disableLevelSaving = false;
			world.saveAllChunks(true, null);
		}
		catch (Exception e)
		{
			CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.FAILED));

			return false;
		}

		MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(world));

		world.flush();
		world.getWorldInfo().setDimensionData(type.getId(), null);

		DimensionManager.setWorld(type.getId(), null, server);

		if (dir.exists())
		{
			if (backup)
			{
				backup(world);
			}

			try
			{
				FileUtils.deleteDirectory(dir);
			}
			catch (IOException e)
			{
				CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.FAILED));

				return false;
			}
		}

		if (type.shouldLoadSpawn())
		{
			DimensionManager.initDimension(type.getId());

			world = server.getWorld(type.getId());

			try
			{
				boolean prevSaving = world.disableLevelSaving;

				world.disableLevelSaving = false;
				world.saveAllChunks(true, null);
				world.disableLevelSaving = prevSaving;
			}
			catch (Exception e) {}

			boolean prevSaving = world.disableLevelSaving;

			world.disableLevelSaving = false;
			world.flushToDisk();
			world.disableLevelSaving = prevSaving;
		}

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			PortalCache.get(player).clearLastPos(null, type);
		}

		text = new TextComponentTranslation("cavern.regeneration.regenerated", name);
		text.getStyle().setColor(TextFormatting.GRAY);

		server.getPlayerList().sendMessage(text);

		CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.REGENERATED));

		return true;
	}

	public static boolean backup(World world)
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		File dir = new File(DimensionManager.getCurrentSaveRootDirectory(), world.provider.getSaveFolder());

		if (!dir.exists())
		{
			return false;
		}

		final Pattern pattern = Pattern.compile("^" + dir.getName() + "_bak-..*\\.zip$");

		File parent = dir.getParentFile();
		File[] files = parent.listFiles((FilenameFilter) (dir1, name) -> pattern.matcher(name).matches());

		if (files != null && files.length >= 5)
		{
			Arrays.sort(files, (o1, o2) ->
			{
				int i = CaveUtils.compareWithNull(o1, o2);

				if (i == 0 && o1 != null && o2 != null)
				{
					try
					{
						i = Files.getLastModifiedTime(o1.toPath()).compareTo(Files.getLastModifiedTime(o2.toPath()));
					}
					catch (IOException e) {}
				}

				return i;
			});

			try
			{
				FileUtils.forceDelete(files[0]);
			}
			catch (IOException e) {}
		}

		Calendar calendar = Calendar.getInstance();
		String year = Integer.toString(calendar.get(Calendar.YEAR));
		String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
		String day = String.format("%02d", calendar.get(Calendar.DATE));
		String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
		String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
		String second = String.format("%02d", calendar.get(Calendar.SECOND));
		File bak = new File(parent, dir.getName() + "_bak-" + Joiner.on("").join(year, month, day) + "-" + Joiner.on("").join(hour, minute, second) + ".zip");

		if (bak.exists())
		{
			FileUtils.deleteQuietly(bak);
		}

		ITextComponent name, text;

		name = new TextComponentString(world.provider.getDimensionType().getName());
		name.getStyle().setBold(true);

		text = new TextComponentTranslation("cavern.regeneration.backup", name);
		text.getStyle().setColor(TextFormatting.GRAY);

		server.getPlayerList().sendMessage(text);

		CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.BACKUP));

		if (CaveUtils.archiveDirZip(dir, bak))
		{
			ClickEvent click = new ClickEvent(ClickEvent.Action.OPEN_FILE, FilenameUtils.normalize(bak.getParentFile().getPath()));

			text = new TextComponentTranslation("cavern.regeneration.backup.success", name);
			text.getStyle().setColor(TextFormatting.GRAY).setClickEvent(click);

			server.getPlayerList().sendMessage(text);

			return true;
		}
		else
		{
			text = new TextComponentTranslation("cavern.regeneration.backup.failed", name);
			text.getStyle().setColor(TextFormatting.RED);

			server.getPlayerList().sendMessage(text);

			CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(EnumType.FAILED));
		}

		return false;
	}
}