package cavern.core;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;

import cavern.api.IHunterStats;
import cavern.api.IMagicianStats;
import cavern.api.IMinerStats;
import cavern.network.CaveNetworkRegistry;
import cavern.network.client.RegenerationGuiMessage;
import cavern.network.client.RegenerationGuiMessage.EnumType;
import cavern.stats.HunterRank;
import cavern.stats.HunterStats;
import cavern.stats.MagicianRank;
import cavern.stats.MagicianStats;
import cavern.stats.MinerRank;
import cavern.stats.MinerStats;
import cavern.util.Version;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandCavern extends CommandBase
{
	@Override
	public String getName()
	{
		return "cavern";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return String.format("/%s <%s>", getName(), Joiner.on('|').join(getCommands()));
	}

	public String[] getCommands()
	{
		return Version.DEV_DEBUG ? new String[] {"regenerate", "miner", "hunter", "magician"} : new String[] {"regenerate"};
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length <= 0)
		{
			return;
		}

		boolean isPlayer = sender instanceof EntityPlayerMP;

		if (args[0].equalsIgnoreCase("regenerate") && isPlayer)
		{
			EntityPlayerMP player = (EntityPlayerMP)sender;

			if (server.isSinglePlayer() || server.getPlayerList().canSendCommands(player.getGameProfile()))
			{
				CaveNetworkRegistry.sendTo(new RegenerationGuiMessage(EnumType.OPEN), player);
			}
			else throw new CommandException("commands.generic.permission");
		}
		else if (args[0].equalsIgnoreCase("miner") && isPlayer)
		{
			EntityPlayerMP player = (EntityPlayerMP)sender;

			if (server.isSinglePlayer() || server.getPlayerList().canSendCommands(player.getGameProfile()))
			{
				IMinerStats stats = MinerStats.get(player);

				stats.setPoint(0, false);
				stats.addPoint(MinerRank.get(stats.getRank() + 1).getPhase());
			}
			else throw new CommandException("commands.generic.permission");
		}
		else if (args[0].equalsIgnoreCase("hunter") && isPlayer)
		{
			EntityPlayerMP player = (EntityPlayerMP)sender;

			if (server.isSinglePlayer() || server.getPlayerList().canSendCommands(player.getGameProfile()))
			{
				IHunterStats stats = HunterStats.get(player);

				stats.setPoint(0, false);
				stats.addPoint(HunterRank.get(stats.getRank() + 1).getPhase());
			}
			else throw new CommandException("commands.generic.permission");
		}
		else if (args[0].equalsIgnoreCase("magician") && isPlayer)
		{
			EntityPlayerMP player = (EntityPlayerMP)sender;

			if (server.isSinglePlayer() || server.getPlayerList().canSendCommands(player.getGameProfile()))
			{
				IMagicianStats stats = MagicianStats.get(player);

				stats.setPoint(0, false);
				stats.addPoint(MagicianRank.get(stats.getRank() + 1).getPhase());
			}
			else throw new CommandException("commands.generic.permission");
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return sender instanceof MinecraftServer || sender instanceof EntityPlayerMP;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
	{
		return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, getCommands()) : Collections.emptyList();
	}
}