package com.wynndevs.modules.expansion.webapi;

import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.experience.SpellCastingUI;
import com.wynndevs.modules.expansion.misc.Delay;
import com.wynndevs.modules.expansion.misc.WorldItemName;
import com.wynndevs.modules.expansion.partyfriendsguild.PlayerGlow;
import com.wynndevs.modules.expansion.partyfriendsguild.PlayerHomeMenu;
import com.wynndevs.modules.expansion.partyfriendsguild.PlayerInfoMenu;
import com.wynndevs.modules.expansion.questbook.QuestBook;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebAPI{
	
	public static List<String> TaskSchedule = new CopyOnWriteArrayList<String>();
	private static Delay TerritoryDelay = new Delay(120.0f,true);
	private static Delay PartyUpdateDelay = new Delay(10.0f,true);
	private static Delay HelpersUpdateDelay = new Delay(3600.0f,true);
	private static List<Long> RateLimiter = new ArrayList<Long>();
	public static boolean Running = false;
	public static boolean Done = true;
	
	public static String ItemDBURL = "";
	public static String ItemDB2URL = "";
	public static String TerritoryAPIURL = "";
	public static String TerritoryCoordsURL = "";
	public static String HelpersURL = "";
	public static String PlayerListAPIURL = "";
	public static String PlayerInfoAPIURL = "";
	public static String GuildInfoAPIURL = "";
	public static String GuildListAPIURL = "";
	public static String QuestCorrectionsURL = "";
	public static String SpellKeyRenaimingURL = "";
	
	public static void StartAPI() {
		
		Running = true;
		Done = false;
		
		new Thread(() -> {
            if (!Reference.onServer()) {
                Running = false;
            }
            Controller();// code goes here.
        }).start();
		//this line will execute immediately, not waiting for your task to complete
	}
	
	public static void Controller(){
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		
		GetURLSettings();
		if (Running) {
			
			// GET DATA LISTS \\
			ItemDB.GenerateItemDB();
			WorldItemName.SetupItemTables();
			Territory.SetupTerritoryList();
			PlayerGlow.updateHelpers();
			QuestBook.SetupQuestCorrections();
			SpellCastingUI.setupButtonRenaming();
			////////	\\\\\\\\
			
			ExpReference.consoleOut("Web API collection thread started");
			TerritoryDelay.Reset();
			while (Running){
				
				if (Reference.onServer()){
					//Automated Tasks
					if (RateLimiter.size() < 225) {
						if (TerritoryDelay.Passed()){
							RateLimiter.add(System.currentTimeMillis() + 600000);
							Territory.Update();
						}
					}
					
					//Scheduled Tasks
					if (RateLimiter.size() < 200 && TaskSchedule.size() > 0) {
						if (TaskSchedule.get(0).equals("UpdateGuild")){
							RateLimiter.add(System.currentTimeMillis() + 600000);
							RateLimiter.add(System.currentTimeMillis() + 600000);
							PlayerGlow.updateGuild();
							ExpReference.consoleOut("Updated your Guild's infomation");
						}else if(TaskSchedule.get(0).startsWith("CGN: ")){
							RateLimiter.add(System.currentTimeMillis() + 600000);
							Territory.GetCompactGuildName(TaskSchedule.get(0).substring(5));
						}else if(TaskSchedule.get(0).equals("RefreshPlayerLists")){
							RateLimiter.add(System.currentTimeMillis() + 600000);
							RateLimiter.add(System.currentTimeMillis() + 600000);
							RateLimiter.add(System.currentTimeMillis() + 600000);
							PlayerHomeMenu.GatherPlayers();
						}else if(TaskSchedule.get(0).equals("GetPlayerInfo")){
							RateLimiter.add(System.currentTimeMillis() + 600000);
							PlayerInfoMenu.GetPlayerInfo();
						}
						TaskSchedule.remove(0);
					}
					
					//Extra Tasks no API required
					if(PartyUpdateDelay.Passed() && Reference.onWorld()){PlayerGlow.updateParty();}
					if(HelpersUpdateDelay.Passed() && Reference.onWorld()){PlayerGlow.updateHelpers();}
					
					
					//RateLimiter Cleaner
					if (!RateLimiter.isEmpty() && RateLimiter.get(0) < System.currentTimeMillis()){RateLimiter.remove(0);}	
				}
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException ignore) {}
			}
			ExpReference.consoleOut("Web API collection thread stopped");
		}else{
			ExpReference.consoleOut("Web API collection disabled");
		}
		Done = true;
	}
	
	public static void GetURLSettings() {
		try {
			ExpReference.consoleOut("Gathering URL settings");
			BufferedReader URLSettingFile = new BufferedReader(new InputStreamReader(new URL("https://pastebin.com/raw/NQmFK8rA").openConnection().getInputStream()));
			
			String URLSettingLine = URLSettingFile.readLine();
			while (URLSettingLine != null) {
				String[] URLSettings = URLSettingLine.split(" : ");
				
				switch (URLSettings[0]) {
					case "webapi":
						if (Running) {
							Running = Boolean.valueOf(URLSettings[1]);
						}
						break;
					case "ItemDB":
						ItemDBURL = GetLink(URLSettings); break;
					case "ItemDB2":
						ItemDB2URL = GetLink(URLSettings); break;
					case "TerritoryAPI":
						TerritoryAPIURL = GetLink(URLSettings); break;
					case "TerritoryCoords":
						TerritoryCoordsURL = GetLink(URLSettings); break;
					case "Helpers":
						HelpersURL = GetLink(URLSettings); break;
					case "PlayerListAPI":
						PlayerListAPIURL = GetLink(URLSettings); break;
					case "PlayerInfoAPI":
						PlayerInfoAPIURL = GetLink(URLSettings); break;
					case "GuildInfoAPI":
						GuildInfoAPIURL = GetLink(URLSettings); break;
					case "GuildListAPI":
						GuildListAPIURL = GetLink(URLSettings); break;
					case "QuestCorrections":
						QuestCorrectionsURL = GetLink(URLSettings); break;
					case "SpellKeyRenaiming":
						SpellKeyRenaimingURL = GetLink(URLSettings); break;
					default: break;
				}
				URLSettingLine = URLSettingFile.readLine();
			}
			URLSettingFile.close();
			
			ExpReference.consoleOut("URL Settings successfully retrieved");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String GetLink(String[] Settings) {
		if (Boolean.valueOf(Settings[1])) {
			return Settings[2];
		}else{
			return "";
		}
	}
}
