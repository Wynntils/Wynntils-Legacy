package com.wynndevs.modules.expansion.webapi;

import com.wynndevs.ModCore;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.partyfriendsguild.PlayerHomeMenu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerCollection {
	
	public static List<List<String>> Worlds = new ArrayList<List<String>>();
	
	public static void CollectPlayers(){
		try {
			ExpReference.consoleOut("Making Player database");
			List<String> PlayerListTmp = new ArrayList<String>();
			BufferedReader PlayerListRawURL = new BufferedReader(new InputStreamReader(new URL(WebAPI.PlayerListAPIURL).openConnection().getInputStream()));
			String PlayerListRaw = PlayerListRawURL.readLine();
			PlayerListRawURL.close();
			
			for (int LastLocation = 1; PlayerListRaw.indexOf("\":[", LastLocation) != -1; LastLocation++) {
				PlayerListTmp.add(PlayerListRaw.substring(PlayerListRaw.lastIndexOf(",\"", PlayerListRaw.indexOf("[", LastLocation)) +1, PlayerListRaw.indexOf(']', PlayerListRaw.indexOf("\":[", LastLocation)) +1).replace("]", ","));
				LastLocation = PlayerListRaw.indexOf("]", LastLocation);
			}
			
			Collections.sort(PlayerListTmp, String.CASE_INSENSITIVE_ORDER);
			
			int TotalPlayers = 0;
			
			for (int i = 0; i < PlayerListTmp.size(); i++) {
				
				List<String> Tmp = new ArrayList<String>();
				Tmp.add(PlayerListTmp.get(i).substring(PlayerListTmp.get(i).indexOf("\"") +1, PlayerListTmp.get(i).indexOf("\":")));
				Worlds.add(Tmp);
				
				for (int LastLocation = Worlds.get(i).get(0).length()+3; PlayerListTmp.get(i).indexOf("\",", LastLocation +1) != -1; LastLocation++) {
					Worlds.get(i).add(PlayerListTmp.get(i).substring(PlayerListTmp.get(i).indexOf("\"", LastLocation +1) +1, PlayerListTmp.get(i).indexOf("\",", PlayerListTmp.get(i).indexOf("\"", LastLocation +1))));
					LastLocation = PlayerListTmp.get(i).indexOf("\",", LastLocation);
					
					TotalPlayers++;
				}
			}
			PlayerListTmp.clear();
			ExpReference.consoleOut("Player database made: " + TotalPlayers + " players in " + Worlds.size() + " worlds");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static List<String[]> GetGuildList() {
		List<String[]> GuildList = new ArrayList<String[]>();
		try {
			BufferedReader GuildGetRawURL = new BufferedReader(new InputStreamReader(new URL(WebAPI.PlayerInfoAPIURL.replace("CMDARG", ModCore.mc().player.getName())).openConnection().getInputStream()));
			String GuildGetRaw = GuildGetRawURL.readLine();
			GuildGetRawURL.close(); 
			
			String Guild = GuildGetRaw.substring(GuildGetRaw.indexOf("},\"guild\":{\"name\":") + 18,GuildGetRaw.indexOf(",", GuildGetRaw.indexOf("},\"guild\":{\"name\":") + 18)).replace("\"", "");
			if (GuildGetRaw.substring(GuildGetRaw.indexOf("},\"guild\":{\"name\":\"" + Guild + "\",\"rank\":") + 28 + Guild.length(), GuildGetRaw.indexOf("},\"guild\":{\"name\":\"" + Guild + "\",\"rank\":") + 28 + Guild.length()).equals("\"\"")) {
				Guild = "";
			}
			
			
			if (!Guild.equals("")) {
				BufferedReader GuildListRawURL = new BufferedReader(new InputStreamReader(new URL(WebAPI.GuildInfoAPIURL.replace("CMDARG", Guild.replace(" ", "%20"))).openConnection().getInputStream()));
				String GuildListRaw = GuildListRawURL.readLine();
				GuildListRawURL.close();
				
				for (int i = 2; i < GuildListRaw.length(); i++) {
					i = GuildListRaw.indexOf("\"name\":", i);
					if (i < 0) {
						break;
					}
					String Name = GuildListRaw.substring(i + 7, GuildListRaw.indexOf(",", i)).replace("\"", "");
					String RankRaw = GuildListRaw.substring(GuildListRaw.indexOf("\"rank\":", i+7) +7, GuildListRaw.indexOf(",", GuildListRaw.indexOf("\"rank\":", i+7) +7)).replace("\"", "");
					String Rank = "0";
					if (RankRaw.equalsIgnoreCase("OWNER")) {Rank = "5";}else
					if (RankRaw.equalsIgnoreCase("CHIEF")) {Rank = "4";}else
					if (RankRaw.equalsIgnoreCase("CAPTAIN")) {Rank = "3";}else
					if (RankRaw.equalsIgnoreCase("RECRUITER")) {Rank = "2";}else
					if (RankRaw.equalsIgnoreCase("RECRUIT")) {Rank = "1";}
					
					String Exp = GuildListRaw.substring(GuildListRaw.indexOf("\"contributed\":", i) +14, GuildListRaw.indexOf(",", GuildListRaw.indexOf("\"contributed\":", i) +14)).replace("\"", "");
					
					String Joined = GuildListRaw.substring(GuildListRaw.indexOf("\"joinedFriendly\"", i) +16, GuildListRaw.indexOf("}", GuildListRaw.indexOf("\"contributed\":", i) +16)).replace("\"", "");
					
					String[] GuildMember = {"0", Name, Rank, Exp, Joined};
					if (!GuildList.contains(GuildMember)) {
						GuildList.add(GuildMember);
						if (Name.equals(ModCore.mc().player.getName())) {PlayerHomeMenu.GuildName = Guild; PlayerHomeMenu.GuildRank = Integer.parseInt(Rank);}
						// System.out.println(GuildMember);
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return GuildList;
	}
}
