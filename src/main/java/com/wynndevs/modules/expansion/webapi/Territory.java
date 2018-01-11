package com.wynndevs.modules.expansion.webapi;

import com.wynndevs.modules.expansion.ExpReference;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Territory {
	
	public static List<WynnTerritory> TerritoryList = new CopyOnWriteArrayList<WynnTerritory>();
	private static List<WynnTerritory> TerritoryListTmp = new ArrayList<WynnTerritory>();
	
	public static boolean CompactNews = false;
	public static boolean GroupNewsNames = false;
	public static boolean ShowAttackMessages = false;
	public static List<String> CGNname = new ArrayList<String>();
	public static List<String> CGNprefix = new ArrayList<String>();
	
	public static void GetTerritoryList() {
		try {
			BufferedReader TerritoryListRawURL = new BufferedReader(new InputStreamReader(new URL(WebAPI.TerritoryAPIURL).openConnection().getInputStream()));
			String TerritoryListRaw = TerritoryListRawURL.readLine().replace("’", "'"); //String.valueOf('\u2019')
			TerritoryListRawURL.close();
			
			// Split Territories
			for (int LastLocation = 0; TerritoryListRaw.indexOf("},", LastLocation) != -1; LastLocation++) {
				String TerritoryRaw = TerritoryListRaw.substring(TerritoryListRaw.indexOf(":{", LastLocation), TerritoryListRaw.indexOf("},", LastLocation)).replace("}", "") + ",";
				LastLocation = TerritoryListRaw.indexOf("},", LastLocation);
				
				if (TerritoryRaw.contains("\"location\":{")) {
					WynnTerritory Territory = new WynnTerritory();
					Territory.Name = TerritoryRaw.substring(TerritoryRaw.indexOf("\"territory\":") +12, TerritoryRaw.indexOf(",",TerritoryRaw.indexOf("\"territory\":") +12)).replace("\"", "");
					Territory.Guild = TerritoryRaw.substring(TerritoryRaw.indexOf("\"guild\":") +8, TerritoryRaw.indexOf(",",TerritoryRaw.indexOf("\"guild\":") +8)).replace("\"", "");
					Territory.Attacker = TerritoryRaw.substring(TerritoryRaw.indexOf("\"attacker\":") +11, TerritoryRaw.indexOf(",",TerritoryRaw.indexOf("\"attacker\":") +11));
					Territory.CaptureData = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(TerritoryRaw.substring(TerritoryRaw.indexOf("\"acquired\":") +11, TerritoryRaw.indexOf(",",TerritoryRaw.indexOf("\"acquired\":") +11)).replace("\"", ""));
					
					if (Territory.Attacker.equals("null")) {
						Territory.Attacker = "";
					}else{
						Territory.Attacker = Territory.Attacker.replace("\"", "");
					}
					
					Territory.Coords[0][0] = Integer.parseInt(TerritoryRaw.substring(TerritoryRaw.indexOf("\"startX\":") +9, TerritoryRaw.indexOf(",",TerritoryRaw.indexOf("\"startX\":") +9)).replace("\"", ""));
					Territory.Coords[0][1] = Integer.parseInt(TerritoryRaw.substring(TerritoryRaw.indexOf("\"startY\":") +9, TerritoryRaw.indexOf(",",TerritoryRaw.indexOf("\"startY\":") +9)).replace("\"", ""));
					Territory.Coords[1][0] = Integer.parseInt(TerritoryRaw.substring(TerritoryRaw.indexOf("\"endX\":") +7, TerritoryRaw.indexOf(",",TerritoryRaw.indexOf("\"endX\":") +7)).replace("\"", ""));
					Territory.Coords[1][1] = Integer.parseInt(TerritoryRaw.substring(TerritoryRaw.indexOf("\"endY\":") +7, TerritoryRaw.indexOf(",",TerritoryRaw.indexOf("\"endY\":") +7)).replace("\"", ""));
					Territory.FixCoords();
					
					TerritoryListTmp.add(Territory);
				}
			}
		} catch (Exception ignore) {}
	}
	
	public static void SetupTerritoryList() {
		ExpReference.consoleOut("Retrieving Territory data");
		GetTerritoryList();
		TerritoryList.clear();
		TerritoryList.addAll(TerritoryListTmp);
		TerritoryListTmp.clear();
		ExpReference.consoleOut("Successfully retrieved data for " + TerritoryList.size() + " Territories");
	}
	
	public static void Update() {
		GetTerritoryList();
		if (!TerritoryListTmp.isEmpty()) {
			if (TerritoryList.isEmpty()) {
				TerritoryList.addAll(TerritoryListTmp);
				TerritoryListTmp.clear();
			}else{
				for (WynnTerritory Territory : TerritoryListTmp) {
					boolean TerritoryExists = false;
					for (int i=0;i<TerritoryList.size();i++) {
						if (Territory.Name.equals(TerritoryList.get(i).Name)) {
							TerritoryExists = true;
							if (!Territory.Guild.equals(TerritoryList.get(i).Guild) || !Territory.Attacker.equals(TerritoryList.get(i).Attacker)) {
								if (Territory.CaptureData.after(TerritoryList.get(i).CaptureData)) {
									String TerritoryNews = "Well somthing happened at " + Territory.Name + ", dunno what though. You should maybe screenshot this and send it to EHTYCSCYTHE so it can be looked into and maybe fixed";
									
									if (!Territory.Guild.equals(TerritoryList.get(i).Guild)) {
										TerritoryNews = "[" + String.valueOf('\u2749') + Territory.Guild + "] captured [" + Territory.Name + "] from [" + String.valueOf('\u2749') + TerritoryList.get(i).Guild + "]";
										if (CompactNews) {
											if (CGNname.contains(Territory.Guild)) {
												TerritoryNews = TerritoryNews.replace(String.valueOf('\u2749') + Territory.Guild, CGNprefix.get(CGNname.indexOf(Territory.Guild)));
											}else{
												WebAPI.TaskSchedule.add("CGN: " + Territory.Guild);
											}
											if (CGNname.contains(TerritoryList.get(i).Guild)) {
												TerritoryNews = TerritoryNews.replace(String.valueOf('\u2749') + TerritoryList.get(i).Guild, CGNprefix.get(CGNname.indexOf(TerritoryList.get(i).Guild)));
											}else{
												WebAPI.TaskSchedule.add("CGN: " + TerritoryList.get(i).Guild);
											}
										}
									}else if (ShowAttackMessages && Territory.Attacker.equals(TerritoryList.get(i).Attacker)) {
										if (Territory.Attacker.equals("")) {
											TerritoryNews = "[" + String.valueOf('\u2749') + Territory.Guild + "] besieged at [" + Territory.Name + "] by [" + String.valueOf('\u2749') + Territory.Attacker + "]";
											if (CompactNews) {
												if (CGNname.contains(Territory.Guild)) {
													TerritoryNews = TerritoryNews.replace(String.valueOf('\u2749') + Territory.Guild, CGNprefix.get(CGNname.indexOf(Territory.Guild)));
												}else{
													WebAPI.TaskSchedule.add("CGN: " + Territory.Guild);
												}
												if (CGNname.contains(Territory.Attacker)) {
													TerritoryNews = TerritoryNews.replace(String.valueOf('\u2749') + Territory.Attacker, CGNprefix.get(CGNname.indexOf(Territory.Attacker)));
												}else{
													WebAPI.TaskSchedule.add("CGN: " + Territory.Attacker);
												}
											}
										}else{
											TerritoryNews = "[" + String.valueOf('\u2749') + Territory.Guild + "] defended [" + Territory.Name + "] from [" + String.valueOf('\u2749') + Territory.Attacker + "]";
											if (CompactNews) {
												if (CGNname.contains(Territory.Guild)) {
													TerritoryNews = TerritoryNews.replace(String.valueOf('\u2749') + Territory.Guild, CGNprefix.get(CGNname.indexOf(Territory.Guild)));
												}else{
													WebAPI.TaskSchedule.add("CGN: " + Territory.Guild);
												}
												if (CGNname.contains(Territory.Attacker)) {
													TerritoryNews = TerritoryNews.replace(String.valueOf('\u2749') + Territory.Attacker, CGNprefix.get(CGNname.indexOf(Territory.Attacker)));
												}else{
													WebAPI.TaskSchedule.add("CGN: " + Territory.Attacker);
												}
											}
										}
									}
									
									TerritoryUI.TerritoryListUpdates.add((GroupNewsNames ? TerritoryNews.replace("[", "").replace("]", "") : TerritoryNews));
									TerritoryList.set(i, Territory);
								}else{
									ExpReference.consoleOut("Inconsistant Capture time for " + Territory.Name + ", Ignoring");
								}
							}
							break;
						}
					}
					if (!TerritoryExists) {
						TerritoryList.add(Territory);
					}
				}
				TerritoryListTmp.clear();
			}
		}
	}
	
	public static void GetCompactGuildName(String GuildName) {
		String GuildGetRaw;
		try {
			if (!CGNname.contains(GuildName)) {
				BufferedReader GuildGetRawURL = new BufferedReader(new InputStreamReader(new URL(WebAPI.GuildInfoAPIURL.replace("CMDARG", GuildName.replace(" ", "%20"))).openConnection().getInputStream()));
				GuildGetRaw = GuildGetRawURL.readLine();
				GuildGetRawURL.close();
				
				if (GuildGetRaw.contains(",\"prefix\":")) {
					CGNprefix.add(GuildGetRaw.substring(GuildGetRaw.indexOf(",\"prefix\":") + 10, GuildGetRaw.indexOf(",", GuildGetRaw.indexOf(",\"prefix\":") + 10)).replace("\"", ""));
					CGNname.add(GuildName);
					AddGuildPrefix(CGNname.get(CGNname.size()-1), CGNprefix.get(CGNprefix.size()-1));
				}
			}
			if (CompactNews && !CGNname.isEmpty()) {
				for (int i = 0; i < TerritoryUI.TerritoryListUpdates.size(); i++) {
					if (TerritoryUI.TerritoryListUpdates.get(i).contains(String.valueOf('\u2749' + GuildName))) {
						TerritoryUI.TerritoryListUpdates.set(i, TerritoryUI.TerritoryListUpdates.get(i).replace(String.valueOf('\u2749') + GuildName, CGNprefix.get(CGNname.size() - 1)));
					}
				}
			}
		} catch (Exception ignore) {
		}

	}
	
	public static void LoadGuildPrefixes() {
		CGNname.clear();
		CGNprefix.clear();
		try{
			if (new File("config/Wynn Expansion/Guild List.txt").exists()){
				ExpReference.consoleOut("Loading Guild Prefixes from file");
				boolean ReWrite = false;
				BufferedReader GuildList = new BufferedReader(new FileReader("config/Wynn Expansion/Guild List.txt"));
				String Guild = GuildList.readLine();
				while(Guild != null) {
					if (CGNname.contains(Guild.substring(Guild.indexOf(" : ") +3))) {
						CGNname.set(CGNname.indexOf(Guild.substring(Guild.indexOf(" : ") +3)), Guild.substring(Guild.indexOf(" : ") +3));
						CGNprefix.set(CGNname.indexOf(Guild.substring(Guild.indexOf(" : ") +3)), Guild.substring(0, Guild.indexOf(" : ")));
						ReWrite = true;
					}else{
						CGNname.add(Guild.substring(Guild.indexOf(" : ") +3));
						CGNprefix.add(Guild.substring(0, Guild.indexOf(" : ")));
					}
					Guild = GuildList.readLine();
				}
				GuildList.close();
				ExpReference.consoleOut("Loaded " + CGNname.size() + " Guild Prefixes");
				
				if (ReWrite) {
					ExpReference.consoleOut("ReWriting Guild Prefix list");
					if (!new File("config/Wynn Expansion/Guild List.txt").delete())
						new File("config/Wynn Expansion/Guild List.txt").deleteOnExit();
					for (int i=0;i<CGNname.size();i++) {
						AddGuildPrefix(CGNname.get(i), CGNprefix.get(i));
					}
					ExpReference.consoleOut("ReWrite of Guild Prefix list Complete");
				}
			}
		}catch(Exception ignore){}
	}
	
	public static void AddGuildPrefix(String Guild, String Prefix) {
		try{
			FileWriter File = new FileWriter("config/Wynn Expansion/Guild List.txt", true);
			File.write(Prefix + " : " + Guild + "\n");
			File.close();
		}catch(Exception ignore){}
	}
	
	public static void ReWriteGuildPrefix() {
		try{
			if (!new File("config/Wynn Expansion/Guild List.txt").delete()) {
				new File("config/Wynn Expansion/Guild List.txt").deleteOnExit();
			}
			
			FileWriter File = new FileWriter("config/Wynn Expansion/Guild List.txt", true);
			for (int i=0;i<CGNname.size();i++) {
				File.write(CGNprefix.get(i) + " : " + CGNname.get(i) + "\n");
			}
			File.close();
		}catch(Exception ignore){}
	}
	
	public static void FindAllGuildPrefixes() {
		try {
			ExpReference.consoleOut("Searching for new guilds");
			
			BufferedReader GuildListRawURL = new BufferedReader(new InputStreamReader(new URL(WebAPI.GuildListAPIURL).openConnection().getInputStream()));
			String GuildListRaw = GuildListRawURL.readLine();
			GuildListRawURL.close();
			
			if (GuildListRaw.contains("\"guilds\":[\"")) {
				
				GuildListRaw = GuildListRaw.substring(GuildListRaw.indexOf("\"guilds\":[")+10, GuildListRaw.indexOf("]", GuildListRaw.indexOf("\"guilds\":[")+10));
				String[] GuildList = GuildListRaw.split("\",\"");
				if (GuildList.length != 0) {
					if (GuildList[0] != null) GuildList[0] = GuildList[0].replace("\"", "");
					if (GuildList[GuildList.length-1] != null) GuildList[GuildList.length-1] = GuildList[GuildList.length-1].replace("\"", "");
				}
				
				int NewGuilds = 0;
				for (String Guild : GuildList) {
					if (!CGNname.contains(Guild) && !WebAPI.TaskSchedule.contains("CGN: " + Guild)) {
						//AddGuildPrefix(Guild, "BLEH");
						WebAPI.TaskSchedule.add("CGN: " + Guild);
						NewGuilds++;
					}
				}
				
				int OldGuilds = 0;
				for (int i=0;i<CGNname.size();i++) {
					boolean OldGuild = true;
					for (String Guild : GuildList) {
						if (CGNname.get(i).equals(Guild)) {
							OldGuild = false;
							break;
						}
					}
					if (OldGuild) {
						CGNname.remove(i);
						CGNprefix.remove(i);
						i--;
						OldGuilds++;
					}
				}
				if (OldGuilds > 0) {
					ReWriteGuildPrefix();
				}
				
				ExpReference.consoleOut("Found " + GuildList.length + " Guilds, With " + NewGuilds + " New Guilds and " + OldGuilds + " Dead guilds");
			}
		} catch (Exception ignore) {}
	}
}

