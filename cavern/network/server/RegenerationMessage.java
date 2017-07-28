package cavern.network.server;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import cavern.network.CaveNetworkRegistry;
import cavern.network.client.RegenerationGuiMessage;
import cavern.network.client.RegenerationGuiMessage.EnumType;
import cavern.stats.PortalCache;
import cavern.util.CaveUtils;
import cavern.world.CaveType;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class RegenerationMessage implements ISimpleMessage<RegenerationMessage, IMessage>
{
	public boolean backup, cavern, aquaCavern, caveland, iceCavern, ruinsCavern, cavenia, hugeCavern;

	@Override
	public void fromBytes(ByteBuf buf)
	{
		backup = buf.readBoolean();
		cavern = buf.readBoolean();
		aquaCavern = buf.readBoolean();
		caveland = buf.readBoolean();
		iceCavern = buf.readBoolean();
		ruinsCavern = buf.readBoolean();
		cavenia = buf.readBoolean();
		hugeCavern = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(backup);
		buf.writeBoolean(cavern);
		buf.writeBoolean(aquaCavern);
		buf.writeBoolean(caveland);
		buf.writeBoolean(iceCavern);
		buf.writeBoolean(ruinsCavern);
		buf.writeBoolean(cavenia);
		buf.writeBoolean(hugeCavern);
	}

	@Override
	public IMessage process()
	{
		if (cavern)
		{
			regenerateDimension(CaveType.DIM_CAVERN);
		}

		if (aquaCavern)
		{
			regenerateDimension(CaveType.DIM_AQUA_CAVERN);
		}

		if (caveland)
		{
			regenerateDimension(CaveType.DIM_CAVELAND);
		}

		if (iceCavern)
		{
			regenerateDimension(CaveType.DIM_ICE_CAVERN);
		}

		if (ruinsCavern)
		{
			regenerateDimension(CaveType.DIM_RUINS_CAVERN);
		}

		if (cavenia)
		{
			regenerateDimension(CaveType.DIM_CAVENIA);
		}

		if (hugeCavern)
		{
			regenerateDimension(CaveType.DIM_HUGE_CAVERN);
		}

		return null;
	}

	private boolean regenerateDimension(@Nullable DimensionType type)
	{
		if (type == null)
		{
			return false;
		}

		File rootDir = DimensionManager.getCurrentSaveRootDirectory();

		if (rootDir == null || !rootDir.exists())
		{
			sendProgress(EnumType.FAILED);

			return false;
		}

		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		WorldServer world = server.getWorld(type.getId());

		if (!world.playerEntities.isEmpty())
		{
			sendProgress(EnumType.FAILED);

			return false;
		}

		File dimDir = world.provider.getSaveFolder() == null ? rootDir : new File(rootDir, world.provider.getSaveFolder());

		if (!dimDir.exists())
		{
			sendProgress(EnumType.FAILED);

			return false;
		}

		ITextComponent name = new TextComponentString(type.getName());
		name.getStyle().setBold(true);
		ITextComponent message = new TextComponentTranslation("cavern.regeneration.regenerating", name);
		message.getStyle().setColor(TextFormatting.GRAY);

		server.getPlayerList().sendMessage(message);

		if (server.isSinglePlayer())
		{
			sendProgress(EnumType.OPEN);
		}

		sendProgress(EnumType.START);

		MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(world));

		world.flush();
		world.getWorldInfo().setDimensionData(type.getId(), new NBTTagCompound());

		DimensionManager.setWorld(type.getId(), null, server);

		if (backup)
		{
			Calendar calendar = Calendar.getInstance();
			String year = Integer.toString(calendar.get(Calendar.YEAR));
			String month = String.format("%02d", calendar.get(Calendar.MONTH) + 1);
			String day = String.format("%02d", calendar.get(Calendar.DATE));
			String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
			String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
			String second = String.format("%02d", calendar.get(Calendar.SECOND));
			File bak = new File(rootDir, type.getName().replaceAll(" ", "") + "_bak-" + String.join("", year, month, day) + "-" + String.join("", hour, minute, second) + ".zip");

			message = new TextComponentTranslation("cavern.regeneration.backup", name);
			message.getStyle().setColor(TextFormatting.GRAY);

			server.getPlayerList().sendMessage(message);

			sendProgress(EnumType.BACKUP);

			if (CaveUtils.archiveDirectory(dimDir, bak))
			{
				message = new TextComponentTranslation("cavern.regeneration.backup.success", name);
				message.getStyle().setColor(TextFormatting.GRAY).setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, FilenameUtils.normalize(rootDir.getPath())));

				server.getPlayerList().sendMessage(message);
			}
			else
			{
				message = new TextComponentTranslation("cavern.regeneration.backup.failed", name);
				message.getStyle().setColor(TextFormatting.RED);

				server.getPlayerList().sendMessage(message);

				sendProgress(EnumType.FAILED);

				return false;
			}
		}

		try
		{
			FileUtils.deleteDirectory(dimDir);
		}
		catch (IOException e)
		{
			sendProgress(EnumType.FAILED);

			return false;
		}

		if (type.shouldLoadSpawn())
		{
			DimensionManager.initDimension(type.getId());
		}

		for (EntityPlayerMP player : server.getPlayerList().getPlayers())
		{
			PortalCache.get(player).clearLastPos(null, type);
		}

		message = new TextComponentTranslation("cavern.regeneration.regenerated", name);
		message.getStyle().setColor(TextFormatting.GRAY);

		server.getPlayerList().sendMessage(message);

		sendProgress(EnumType.REGENERATED);

		return true;
	}

	private void sendProgress(EnumType type)
	{
		CaveNetworkRegistry.sendToAll(new RegenerationGuiMessage(type));
	}
}