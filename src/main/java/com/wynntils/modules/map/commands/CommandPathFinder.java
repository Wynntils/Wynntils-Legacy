package com.wynntils.modules.map.commands;

import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GREEN;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

public class CommandPathFinder extends CommandBase implements IClientCommand {
	
	@Override
	public boolean allowUsageWithoutPrefix (final ICommandSender sender, final String message) {
		return false;
	}
	
	@Override
	public String getName () {
		return "pathfinder";
	}
	
	@Override
	public List<String> getAliases () {
		return Arrays.asList("pf", "path");
	}
	
	@Override
	public String getUsage (final ICommandSender sender) {
		return "pathfinder <to/find/help>";
	}
	
	@Override
	public int getRequiredPermissionLevel () {
		return 0;
	}
	
	@Override
	public void execute (final MinecraftServer server, final ICommandSender sender, final String[] args)
			throws CommandException {
		if (args.length == 0) {
			throw new WrongUsageException("/" + this.getUsage(sender));
		}
		
		switch (args[0].toLowerCase(Locale.ROOT)) {
			case "to":
				// TODO: command "to"
				break;
			case "find":
				// TODO: command "find"
				break;
			case "help":
			default:
				sender.sendMessage(new TextComponentString(String.join("\n",
						CommandPathFinder.formatCommandHelp("to", Arrays.asList("town", "island", "dungeon", "raid"),
								""),
						CommandPathFinder.formatCommandHelp("to <x> <y> <z>", null,
								"Get directions to the given coordinates."),
						CommandPathFinder.formatCommandHelp("to <x> <z>", null,
								"Get directions to the given coordinates - height is filled in by the nearest path."),
						CommandPathFinder.formatCommandHelp("find",
								Arrays.asList("bank", "emerald", "liquid", "identifier", "blacksmith", "armouring",
										"tailoring", "weaponsmithing", "jeweling", "alchemism", "scribing", "cooking"),
								"Finds the nearest entity of the given type."))));
				break;
		}
	}
	
	@Override
	public List<String> getTabCompletions (final MinecraftServer server, final ICommandSender sender,
			final String[] args, final BlockPos targetPos) {
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "to", "find", "help");
		} else if (args.length == 2 && args[0].equals("find")) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "bank", "emerald", "liquid", "identifier",
					"blacksmith", "armouring", "tailoring", "weaponsmithing", "jeweling", "alchemism", "scribing",
					"cooking");
		}
		return Collections.emptyList();
	}
	
	private static String formatCommandHelp (final String command, final Iterable<String> args, final String help) {
		final String argsString = args == null ? "" : String.join(",", args);
		return GOLD + command + (argsString.isEmpty() ? " " : (" <" + argsString + "> ")) + GREEN + help;
	}
}
