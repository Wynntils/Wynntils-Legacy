package com.wynndevs.modules.expansion.misc;

import com.wynndevs.ModCore;
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
    public void execute(MinecraftServer server, ICommandSender sender, String[] args){
		StringBuilder builder = new StringBuilder();
		for (String str: args) {
			builder.append(str);
		}
		if (builder.length() > 0) {
			builder = new StringBuilder(builder.toString().toUpperCase().replace("X", "[x:").replace("Y", ",y:").replace("Z", ",z:").replace(" ", "") + "]");
			ModCore.mc().player.sendChatMessage(builder.toString());
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return new ArrayList <String>();
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
