package com.wynndevs.modules.expansion.partyfriendsguild;

import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.WynnExpansion;
import com.wynndevs.modules.expansion.webapi.WebAPI;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.List;

public class InfoCommand implements ICommand{

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "info";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/info <playername>";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = new ArrayList<String>();
		aliases.add("info");
		return aliases;
	}

	@Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args){
		if (args.length == 1){
			PlayerInfoMenu.LastPageHome = true;
			PlayerInfoMenu.PlayerLoaded = false;
			if (!PlayerHomeMenu.PlayersLoaded) WebAPI.TaskSchedule.add("RefreshPlayerLists");
			PlayerHomeMenu.RefreshTimer.Reset();
			PlayerInfoMenu.CurrentPlayer = args[0];
			PlayerInfoMenu.PlayerInfomation.clear();
			PlayerInfoMenu.GuildID = -1;
			WynnExpansion.ChatQue.add("--OPENGUI--PlayerInfoMenu");
		}else{
			ExpReference.postToChat(new TextComponentString(String.valueOf('\u00a7') + "eUsage: /info [player]"));
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return new ArrayList <String>();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return true;
	}

}
