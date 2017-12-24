package com.wynndevs.expansion.Misc;

import com.wynndevs.ModCore;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;


public class CordsCommand implements ICommand {

	@Override
	public String getName() {
		return "CordsFormat";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/crd x ### y ### z ###";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = new ArrayList<String>();
		aliases.add("crd");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		String builder = "";
		for (String str: args) {
			builder += str;
		}
		if (!builder.isEmpty()) {
			builder = builder.toUpperCase().replace("X", "[x:").replace("Y", ",y:").replace("Z", ",z:").replace(" ", "") + "]";
			ModCore.mc().player.sendChatMessage(builder);
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		List<String> Output = new ArrayList<String>();
		return Output;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

	@Override
	public int compareTo(ICommand o) {
		return 0;
	}
}
