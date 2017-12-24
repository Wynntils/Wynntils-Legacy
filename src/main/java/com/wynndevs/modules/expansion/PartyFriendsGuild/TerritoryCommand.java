package com.wynndevs.modules.expansion.PartyFriendsGuild;

import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.WebAPI.Territory;
import com.wynndevs.modules.expansion.WebAPI.WynnTerritory;
import com.wynndevs.modules.expansion.WynnExpansion;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.List;

public class TerritoryCommand implements ICommand{

	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "territory";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/territory <territory>";
	}

	@Override
	public List<String> getAliases() {
		List<String> aliases = new ArrayList<String>();
		aliases.add("territory");
		return aliases;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length >= 1){
			String ArgTmp = args[0];
			WynnTerritory SelectedTerritory = new WynnTerritory();
			for (int i=1;i<args.length;i++){
				ArgTmp = ArgTmp + " " + args[i];
			}
			for (WynnTerritory TerritoryTest : Territory.TerritoryList) {
				if (ArgTmp.replace(" ", "_").equalsIgnoreCase(TerritoryTest.Name.replace(" ", "_"))) {
					SelectedTerritory = TerritoryTest;
					break;
				}
			}
			if (SelectedTerritory.Name.equals("")){
				ExpReference.PostToChat(new TextComponentString(String.valueOf('\u00a7') + "cTerritory Not Found: " + ArgTmp));
			}else{
				WynnExpansion.ChatQue.add("/g " + SelectedTerritory.Name + ": " + SelectedTerritory.GetFormatedCoords());
			}
		}else{
			ExpReference.PostToChat(new TextComponentString(String.valueOf('\u00a7') + "eUsage: /territory [territory]"));
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		List<String> Output = new ArrayList<String>();
		String TerritorySearch = args[0];
		for (int i=1;i<args.length;i++){
			TerritorySearch = TerritorySearch + " " + args[i];
		}
		for (WynnTerritory Territory : Territory.TerritoryList){
			if (Territory.Name.replace(" ", "_").toUpperCase().startsWith(TerritorySearch.replace(" ", "_").toUpperCase())) {
				if (TerritorySearch.contains(" ")) {
					String Tmp = Territory.Name.toUpperCase();
					Tmp = Tmp.replace(TerritorySearch.substring(0, TerritorySearch.lastIndexOf(" ") +1).toUpperCase(), "");
					Output.add(Territory.Name.substring(Territory.Name.length() - Tmp.length()).replace(" ", "_"));
				}else{
					Output.add(Territory.Name.replace(" ", "_"));
				}
			}
		}
		return Output;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}
}
