package cavern.core;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;

import cavern.network.CaveNetworkRegistry;
import cavern.network.client.RegenerationGuiMessage;
import cavern.network.client.RegenerationGuiMessage.EnumType;
import cavern.stats.MinerStats;
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
		return new String[] {"regenerate"};
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
		else if (args[0].equalsIgnoreCase("mp") && isPlayer)
		{
			EntityPlayerMP player = (EntityPlayerMP)sender;

			if (player.getServerWorld().getWorldInfo().areCommandsAllowed())
			{
				int value = parseInt(args[1]);

				if (value != 0)
				{
					MinerStats.get(player).addPoint(value);
				}
			}
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
		return args.length == 1 ? CommandBase.getListOfStringsMatchingLastWord(args, getCommands()) : Collections.<String>emptyList();
	}
}