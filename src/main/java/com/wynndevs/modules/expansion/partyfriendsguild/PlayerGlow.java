package com.wynndevs.modules.expansion.partyfriendsguild;

import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.WynnExpansion;
import com.wynndevs.modules.expansion.misc.Delay;
import com.wynndevs.modules.expansion.webapi.WebAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerGlow {

	public static boolean GlowParty = false;
	public static boolean NameParty = false;
	public static boolean GlowFriends = false;
	public static boolean NameFriends = false;
	public static boolean GlowGuild = false;
	public static boolean NameGuild = false;
	public static boolean GlowHelpers = false;
	private static boolean NameHelpers = true;
	public static boolean HighlightDisguises = false;
	public static boolean NameDisguises = false;
	public static boolean BumpHelperGlow = false;
	public static boolean BumpHelperName = false;
	
	public static boolean PlayerCollision = false;
	
	public static boolean WarHealth = false;
	public static boolean GlowVeryLowHealth = false;
	public static boolean GlowLowHealth = false;
	public static boolean GlowMidHealth = false;
	public static boolean GlowHighHealth = false;
	public static boolean GlowVeryHighHealth = false;
	
	public static List<String> PartyList = new CopyOnWriteArrayList<String>();
	public static List<String> FriendsList = new CopyOnWriteArrayList<String>();
	public static List<String> GuildList = new CopyOnWriteArrayList<String>();
	public static List<String> HelpersList = new CopyOnWriteArrayList<String>();
	
	private static Delay refresh = new Delay(2.0f, true);
	private static Delay refreshHealth = new Delay(0.5f, true);
	private static boolean ScheduleUpdateTeams = false;
	
	public static void PlayerGlows(Minecraft mc) {
		Scoreboard Scoreboard = mc.player.getWorldScoreboard();
		if (!Scoreboard.getTeamNames().contains("Party")) Scoreboard.createTeam("Party").setPrefix(String.valueOf('\u00a7') + 'e');
		if (!Scoreboard.getTeamNames().contains("Friends")) Scoreboard.createTeam("Friends").setPrefix(String.valueOf('\u00a7') + 'a');
		if (!Scoreboard.getTeamNames().contains("Guild")) Scoreboard.createTeam("Guild").setPrefix(String.valueOf('\u00a7') + 'b');
		if (!Scoreboard.getTeamNames().contains("Helpers")) Scoreboard.createTeam("Helpers").setPrefix(String.valueOf('\u00a7') + '5');
		if (!Scoreboard.getTeamNames().contains("Player")) Scoreboard.createTeam("Player").setPrefix(String.valueOf('\u00a7') + 'f');
		
		ScorePlayerTeam Party = Scoreboard.getTeam("Party");
		ScorePlayerTeam Friends = Scoreboard.getTeam("Friends");
		ScorePlayerTeam Guild = Scoreboard.getTeam("Guild");
		ScorePlayerTeam Helpers = Scoreboard.getTeam("Helpers");
		ScorePlayerTeam Player = Scoreboard.getTeam("Player");
		
		if (!PlayerCollision && !Player.getCollisionRule().equals(Team.CollisionRule.NEVER)) {
			Scoreboard.getTeam("Player").setCollisionRule(Team.CollisionRule.NEVER);
		}else if (PlayerCollision && !Player.getCollisionRule().equals(Team.CollisionRule.ALWAYS)) {
			Scoreboard.getTeam("Player").setCollisionRule(Team.CollisionRule.ALWAYS);
		}
		if (!mc.player.isOnScoreboardTeam(Player)){
			Scoreboard.addPlayerToTeam(mc.player.getName(), Player.getName());
		}
		
		if (!WarHealth || !Reference.onWars()) {
			if (refresh.Passed() && mc.world != null && (NameDisguises || HighlightDisguises ? mc.world.loadedEntityList : mc.world.playerEntities) != null ) {
				
				if (ScheduleUpdateTeams) {
					updateTeams(Scoreboard, Party, Friends, Guild, Helpers);
					ScheduleUpdateTeams = false;
				}
				
				for (Entity entity : (NameDisguises || HighlightDisguises ? mc.world.loadedEntityList : mc.world.playerEntities)) {
					if ((NameDisguises || HighlightDisguises ? mc.world.loadedEntityList : mc.world.playerEntities) == null)
						break;
                    if (!(entity instanceof EntityArmorStand) && ((entity.getName() != null && entity.getName().matches("[0-9a-zA-Z_]+") && entity instanceof EntityPlayer && !((EntityPlayer) entity).isUser()) || (entity.getName().endsWith("[Disguised]") && !entity.getName().substring(0, entity.getName().length() - 14).equals(mc.player.getName()))) && !entity.getTags().contains("WynnExpGlowSkip")) {
						boolean Disguise = entity.getName().contains("[Disguised]");
						StringBuilder Name = new StringBuilder();
						boolean flag = false;
						for (Character chr : entity.getName().replace(" [Disguised]", "").toCharArray()) {
							if (!flag) {
								if (chr.equals('\u00a7')) {
									flag = true;
								}else{
									Name.append(chr);
								}
							} else {
								flag = false;
							}
						}
						
						
						//Name = Name.toLowerCase();
						
						if (!Name.toString().equals(mc.player.getName())) {
							if (!Disguise || NameDisguises) {
								if (NameParty && (PartyList.contains(Name.toString()) || (Name.length() > 14 && PartyList.contains(Name.substring(0,14))))) {
									if (!entity.getName().startsWith(String.valueOf('\u00a7') + 'e')) {
										if (entity instanceof EntityPlayer) {
											((EntityPlayer) entity).refreshDisplayName();
										}else{
											entity.setCustomNameTag(String.valueOf('\u00a7') + 'e' + (entity.getName().startsWith(String.valueOf('\u00a7')) ? entity.getName().substring(2) : entity.getName()));
										}
									}
								}else if (BumpHelperName && NameHelpers && HelpersList.contains(Name.toString())) {
									if (!entity.getName().startsWith(String.valueOf('\u00a7') + '5')) {
										if (entity instanceof EntityPlayer) {
											((EntityPlayer) entity).refreshDisplayName();
										}else{
											entity.setCustomNameTag(String.valueOf('\u00a7') + '5' + (entity.getName().startsWith(String.valueOf('\u00a7')) ? entity.getName().substring(2) : entity.getName()));
										}
									}
								}else if (NameFriends && FriendsList.contains(Name.toString())) {
									if (!entity.getName().startsWith(String.valueOf('\u00a7') + 'a')) {
										if (entity instanceof EntityPlayer) {
											((EntityPlayer) entity).refreshDisplayName();
										}else{
											entity.setCustomNameTag(String.valueOf('\u00a7') + 'a' + (entity.getName().startsWith(String.valueOf('\u00a7')) ? entity.getName().substring(2) : entity.getName()));
										}
									}
								}else if (NameGuild && GuildList.contains(Name.toString())) {
									if (!entity.getName().startsWith(String.valueOf('\u00a7') + 'b')) {
										if (entity instanceof EntityPlayer) {
											((EntityPlayer) entity).refreshDisplayName();
										}else{
											entity.setCustomNameTag(String.valueOf('\u00a7') + 'b' + (entity.getName().startsWith(String.valueOf('\u00a7')) ? entity.getName().substring(2) : entity.getName()));
										}
									}
								}else if (!BumpHelperName && NameHelpers && HelpersList.contains(Name.toString())) {
									if (!entity.getName().startsWith(String.valueOf('\u00a7') + '5')) {
										if (entity instanceof EntityPlayer) {
											((EntityPlayer) entity).refreshDisplayName();
										}else{
											entity.setCustomNameTag(String.valueOf('\u00a7') + '5' + (entity.getName().startsWith(String.valueOf('\u00a7')) ? entity.getName().substring(2) : entity.getName()));
										}
									}
								}else if (!entity.getName().startsWith(String.valueOf('\u00a7') + 'r')) {
									if (entity instanceof EntityPlayer) {
										((EntityPlayer) entity).refreshDisplayName();
									}else{
										entity.setCustomNameTag(String.valueOf('\u00a7') + 'r' + (entity.getName().startsWith(String.valueOf('\u00a7')) ? entity.getName().substring(2) : entity.getName()));
									}
								}
							}else{
								if (!entity.getName().startsWith(String.valueOf('\u00a7') + 'r')) {
									if (entity instanceof EntityPlayer) {
										((EntityPlayer) entity).refreshDisplayName();
									}else{
										entity.setCustomNameTag(String.valueOf('\u00a7') + 'r' + (entity.getName().startsWith(String.valueOf('\u00a7')) ? entity.getName().substring(2) : entity.getName()));
									}
								}
							}
							
							if (entity.getName().contains("[Disguised]")) {
								if (HighlightDisguises && Scoreboard.getPlayersTeam(Name.toString()) != null) {
									if (entity.getTeam() == null || !entity.getTeam().equals(Scoreboard.getPlayersTeam(Name.toString()))) {
										Scoreboard.addPlayerToTeam(entity.getCachedUniqueIdString(), Scoreboard.getPlayersTeam(Name.toString()).getName());
										//System.out.println(entity.getCachedUniqueIdString() + " Is Joining " + Name + " On Team " + Scoreboard.getPlayersTeam(Name).getName());
									}
								}else if (entity.getTeam() != null) {
									Scoreboard.removePlayerFromTeams(entity.getCachedUniqueIdString());
								}
							}
							
							if (GlowParty && entity.isOnScoreboardTeam(Party)) {
								if (!entity.isGlowing()) {
									entity.setGlowing(true);
								}
							}else if (GlowFriends && entity.isOnScoreboardTeam(Friends)) {
								if (!entity.isGlowing()) {
									entity.setGlowing(true);
								}
							}else if (GlowGuild && entity.isOnScoreboardTeam(Guild)) {
								if (!entity.isGlowing()) {
									entity.setGlowing(true);
								}
							}else if (GlowHelpers && entity.isOnScoreboardTeam(Helpers)) {
								if (!entity.isGlowing()) {
									entity.setGlowing(true);
								}
							}else{
								if (entity.isGlowing() && entity.getTeam() == null) {
									entity.setGlowing(false);
								}
							}
						}
					}
				}
			}
		}else{
			if (!Scoreboard.getTeamNames().contains("VeryLowHealth")) Scoreboard.createTeam("VeryLowHealth").setPrefix(String.valueOf('\u00a7') + '4');
			if (!Scoreboard.getTeamNames().contains("LowHealth")) Scoreboard.createTeam("LowHealth").setPrefix(String.valueOf('\u00a7') + 'c');
			if (!Scoreboard.getTeamNames().contains("MidHealth")) Scoreboard.createTeam("MidHealth").setPrefix(String.valueOf('\u00a7') + 'e');
			if (!Scoreboard.getTeamNames().contains("HighHealth")) Scoreboard.createTeam("HighHealth").setPrefix(String.valueOf('\u00a7') + 'a');
			if (!Scoreboard.getTeamNames().contains("VeryHighHealth")) Scoreboard.createTeam("VeryHighHealth").setPrefix(String.valueOf('\u00a7') + '2');
			
			if (refreshHealth.Passed() && mc.world != null && mc.world.playerEntities != null ) {
				for (EntityPlayer entity : mc.world.playerEntities) {
					if (mc.world.playerEntities == null)
						break;
                    if (entity instanceof EntityOtherPlayerMP && entity.getName() != null && entity.getName().matches("[0-9a-zA-Z_]+") && !entity.isUser()) {
						if (!entity.getName().equals(mc.player.getName())) {
							switch ((int) Math.ceil((Math.ceil(entity.getHealth()) / Math.ceil(entity.getMaxHealth())) * 5)) {
							case 1:
								if (entity.getTeam() != Scoreboard.getTeam("VeryLowHealth"))
									Scoreboard.addPlayerToTeam(entity.getName(), "VeryLowHealth"); 
								if (GlowVeryLowHealth) {
									if (!entity.isGlowing()) entity.setGlowing(true);
								}else{
									if (entity.isGlowing()) entity.setGlowing(false);
								}
								break;
							case 2:
								if (entity.getTeam() != Scoreboard.getTeam("LowHealth"))
									Scoreboard.addPlayerToTeam(entity.getName(), "LowHealth"); 
								if (GlowLowHealth) {
									if (!entity.isGlowing()) entity.setGlowing(true);
								}else{
									if (entity.isGlowing()) entity.setGlowing(false);
								}
								break;
							case 3:
								if (entity.getTeam() != Scoreboard.getTeam("MidHealth"))
									Scoreboard.addPlayerToTeam(entity.getName(), "MidHealth"); 
								if (GlowMidHealth) {
									if (!entity.isGlowing()) entity.setGlowing(true);
								}else{
									if (entity.isGlowing()) entity.setGlowing(false);
								}
								break;
							case 4:
								if (entity.getTeam() != Scoreboard.getTeam("HighHealth"))
									Scoreboard.addPlayerToTeam(entity.getName(), "HighHealth"); 
								if (GlowHighHealth) {
									if (!entity.isGlowing()) entity.setGlowing(true);
								}else{
									if (entity.isGlowing()) entity.setGlowing(false);
								}
								break;
							case 5:
								if (entity.getTeam() != Scoreboard.getTeam("VeryHighHealth"))
									Scoreboard.addPlayerToTeam(entity.getName(), "VeryHighHealth"); 
								if (GlowVeryHighHealth) {
									if (!entity.isGlowing()) entity.setGlowing(true);
								}else{
									if (entity.isGlowing()) entity.setGlowing(false);
								}
								break;
							}
						}
					}
				}
			}
		}
	}
	
	public static void updateUsername(NameFormat event) {
		String Name = event.getUsername();
		if (NameParty && (PartyList.contains(Name) || (Name.length() > 14 && PartyList.contains(Name.substring(0,14))))) {
			event.setDisplayname(String.valueOf('\u00a7') + 'e' + Name);
		}else if (BumpHelperName && NameHelpers && HelpersList.contains(Name)) {
			event.setDisplayname(String.valueOf('\u00a7') + '5' + Name);
		}else if (NameFriends && FriendsList.contains(Name)) {
			event.setDisplayname(String.valueOf('\u00a7') + 'a' + Name);
		}else if (NameGuild && GuildList.contains(Name)) {
			event.setDisplayname(String.valueOf('\u00a7') + 'b' + Name);
		}else if (!BumpHelperName && NameHelpers && HelpersList.contains(Name)) {
			event.setDisplayname(String.valueOf('\u00a7') + '5' + Name);
		}else{
			event.setDisplayname(String.valueOf('\u00a7') + 'r' + Name);
		}
	}
	
	public static void updateTeams(Scoreboard Scoreboard, ScorePlayerTeam Party, ScorePlayerTeam Friends, ScorePlayerTeam Guild, ScorePlayerTeam Helpers) {
		// Remove People if on wrong team
		List<String> Players = new ArrayList<String>();
		if (!Party.getMembershipCollection().isEmpty()) {
			Players.addAll(Party.getMembershipCollection());
			for (int i=0;i<Players.size();i++) {
				if (!PartyList.contains(Players.get(i)) || !GlowParty) {
					Scoreboard.removePlayerFromTeams(Players.get(i));
					Players.remove(i);
					i--;
				}
			}
			Players.clear();
		}
		if (!Friends.getMembershipCollection().isEmpty()) {
			Players.addAll(Friends.getMembershipCollection());
			for (int i=0;i<Players.size();i++) {
				if (!FriendsList.contains(Players.get(i)) || !GlowFriends) {
					Scoreboard.removePlayerFromTeams(Players.get(i));
					Players.remove(i);
					i--;
				}
			}
			Players.clear();
		}
		if (!Guild.getMembershipCollection().isEmpty()) {
			Players.addAll(Guild.getMembershipCollection());
			for (int i=0;i<Players.size();i++) {
				if (!GuildList.contains(Players.get(i)) || !GlowGuild) {
					Scoreboard.removePlayerFromTeams(Players.get(i));
					Players.remove(i);
					i--;
				}
			}
			Players.clear();
		}
		if (!Helpers.getMembershipCollection().isEmpty()) {
			Players.addAll(Helpers.getMembershipCollection());
			for (int i=0;i<Players.size();i++) {
				if (!HelpersList.contains(Players.get(i)) || !GlowHelpers) {
					Scoreboard.removePlayerFromTeams(Players.get(i));
					Players.remove(i);
					i--;
				}
			}
			Players.clear();
		}
		// Add Missing People
		List<Team> BlackListedTeams = new ArrayList<Team>();
		String User = ModCore.mc().player.getName();
		if (GlowParty) {
			BlackListedTeams.add(Party);
			for (String Player : PartyList) {
				if (!BlackListedTeams.contains(Scoreboard.getPlayersTeam(Player)) && !Player.equals(User)) {
					Scoreboard.addPlayerToTeam(Player, Party.getName());
				}
			}
		}
		if (BumpHelperGlow && GlowHelpers) {
			BlackListedTeams.add(Helpers);
			for (String Player : HelpersList) {
				if (!BlackListedTeams.contains(Scoreboard.getPlayersTeam(Player)) && !Player.equals(User)) {
					Scoreboard.addPlayerToTeam(Player, Helpers.getName());
				}
			}
		}
		if (GlowFriends) {
			BlackListedTeams.add(Friends);
			for (String Player : FriendsList) {
				if (!BlackListedTeams.contains(Scoreboard.getPlayersTeam(Player)) && !Player.equals(User)) {
					Scoreboard.addPlayerToTeam(Player, Friends.getName());
				}
			}
		}
		if (GlowGuild) {
			BlackListedTeams.add(Guild);
			for (String Player : GuildList) {
				if (!BlackListedTeams.contains(Scoreboard.getPlayersTeam(Player)) && !Player.equals(User)) {
					Scoreboard.addPlayerToTeam(Player, Guild.getName());
				}
			}
		}
		if (!BumpHelperGlow && GlowHelpers) {
			BlackListedTeams.add(Helpers);
			for (String Player : HelpersList) {
				if (!BlackListedTeams.contains(Scoreboard.getPlayersTeam(Player)) && !Player.equals(User)) {
					Scoreboard.addPlayerToTeam(Player, Helpers.getName());
				}
			}
		}
	}
	
	public static void updateParty() {
		try {
			Boolean[] PartyClearence = new Boolean[16];
			for (int i = 0; i < PartyClearence.length; i++) {
				PartyClearence[i] = false;
			}
			
			Collection<NetworkPlayerInfo> InfoMap = Minecraft.getMinecraft().getConnection().getPlayerInfoMap();
			for (NetworkPlayerInfo networkplayerinfo : InfoMap) {
				if ((ModCore.mc().ingameGUI.getTabList().getPlayerName(networkplayerinfo).contains(String.valueOf('\u00a7') + 'e') || ModCore.mc().ingameGUI.getTabList().getPlayerName(networkplayerinfo)	.contains(String.valueOf('\u00a7') + 'c'))) {
					String PartyTest = ModCore.mc().ingameGUI.getTabList().getPlayerName(networkplayerinfo).substring(2, ModCore.mc().ingameGUI.getTabList().getPlayerName(networkplayerinfo).length() - 2);
					if (!PartyTest.contains(String.valueOf('\u00a7') + 'l') && !PartyTest.contains("[")) {
						if (!PartyList.contains(PartyTest)) {
							PartyList.add(PartyTest);
						}
						PartyClearence[PartyList.indexOf(PartyTest)] = true;
					}
				}
			}
			for (int i = 0; i < PartyList.size(); i++) {
				// System.out.println(PartyList.get(i));
				if (!PartyClearence[i]) {
					PartyList.remove(i);
					i--;
				}
			}
			ScheduleUpdateTeams = true;
		} catch (Exception ignore) {
		}
	}

	private static boolean UpdateFriends = false;
	private static boolean RetrieveFriends = false;
	
	public static boolean chatHandler(String msg, String rawmsg) {
		// Joined Game
		if (!Reference.onWorld() && !RetrieveFriends) {
			RetrieveFriends = true;
		} else if (Reference.onWorld() && RetrieveFriends && !UpdateFriends) {
			UpdateFriends = true;
			WynnExpansion.ChatQue.add("/friend list");
			WebAPI.TaskSchedule.add("UpdateGuild");
			RetrieveFriends = false;
		}
		
		// Friend update Detect and Get
		if (msg.endsWith("has been added to your friends!") || msg.endsWith("has been removed from your friends!") && !UpdateFriends) {
			UpdateFriends = true;
			WynnExpansion.ChatQue.add("/friend list");
			//Minecraft.getMinecraft().player.sendChatMessage("/friend list");
		}
		if (UpdateFriends && msg.startsWith(ModCore.mc().player.getName() + "' friends (")) {
			UpdateFriends = false;
			FriendsList.clear();
			for (int i = msg.indexOf(':') + 2; i < msg.length(); i++) {
				i = msg.indexOf(',', i);
				if (i < 0) {
					i = msg.length();
				}
				String Friend = msg.substring(msg.lastIndexOf(" ", i) + 1, i);
				if (Friend.matches("[0-9a-zA-Z_]+") && Friend.length() <= 16) {
					FriendsList.add(Friend);
				}else{
					TextComponentString Message = new TextComponentString("Invalid Friend: "); // + Friend + "\"");
					Message.getStyle().setColor(TextFormatting.YELLOW);
					
					TextComponentString MessageFriend = new TextComponentString(Friend);
					MessageFriend.getStyle().setColor(TextFormatting.RED);
					MessageFriend.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend remove " + Friend));
					
					TextComponentString Hover = new TextComponentString("Click to remove");
					Hover.getStyle().setColor(TextFormatting.RED);
					MessageFriend.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Hover)); 
					
					Message.appendSibling(MessageFriend);
					ExpReference.postToChat(Message);
				}
			}
			WebAPI.TaskSchedule.add("Correct Friends Case");
			return true;
		} else if (UpdateFriends && msg.equals("We couldn't find any friends.")) {
			FriendsList.clear();
			return true;
		} else if (UpdateFriends && msg.equals("Try typing /friend add Username!")) {
			UpdateFriends = false;
			return true;
		}

		// Guild update Detect
		if (msg.endsWith("has joined the guild! Say hello!") || msg.endsWith("has left the guild.")) {
			WebAPI.TaskSchedule.add("UpdateGuild");
		} else if (rawmsg.startsWith(String.valueOf('\u00a7') + '3' + "You have joined ")) {
			Guild = "[-]" + rawmsg.substring(rawmsg.indexOf(String.valueOf('\u00a7') + 'b') + 2, rawmsg.indexOf(String.valueOf('\u00a7') + '3', rawmsg.indexOf(String.valueOf('\u00a7') + 'b') + 2));
			WebAPI.TaskSchedule.add("UpdateGuild");
		}
		return false;
	}

	public static String Guild = "";

	public static void updateGuild() {
		try {
			if (Guild.startsWith("[-]")) {
				Guild = Guild.substring(3);
			} else {
				BufferedReader GuildGetRawURL = new BufferedReader(new InputStreamReader(new URL(WebAPI.PlayerInfoAPIURL.replace("CMDARG", Minecraft.getMinecraft().player.getName())).openConnection().getInputStream()));
				String GuildGetRaw = GuildGetRawURL.readLine();
				GuildGetRawURL.close();
				
				Guild = GuildGetRaw.substring(GuildGetRaw.indexOf("},\"guild\":{\"name\":") + 18,GuildGetRaw.indexOf(",", GuildGetRaw.indexOf("},\"guild\":{\"name\":") + 18)).replace("\"", "");
				if (GuildGetRaw.substring(GuildGetRaw.indexOf("},\"guild\":{\"name\":\"" + Guild + "\",\"rank\":") + 28 + Guild.length(), GuildGetRaw.indexOf("},\"guild\":{\"name\":\"" + Guild + "\",\"rank\":") + 28 + Guild.length()).equals("\"\"")) {
					Guild = "";
				}
				// System.out.println("Player Raw: " + GuildGetRaw);
				// System.out.println("Guild Name: " + Guild);
			}

			if (GlowGuild && !Guild.equals("")) {
				String GuildListRaw = new BufferedReader(new InputStreamReader(new URL(WebAPI.GuildInfoAPIURL.replace("CMDARG", Guild.replace(" ", "%20"))).openConnection().getInputStream())).readLine();
				GuildList.clear();
				for (int i = 2; i < GuildListRaw.length(); i++) {
					i = GuildListRaw.indexOf("\"name\":", i);
					if (i < 0) {
						break;
					}
					String GuildMember = GuildListRaw.substring(i + 7, GuildListRaw.indexOf(",", i)).replace("\"", "");
					if (!GuildList.contains(GuildMember)) {
						GuildList.add(GuildMember);
						// System.out.println(GuildMember);
					}
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public static void updateHelpers(){
		try {
			ExpReference.consoleOut("Retrieving Helpers");
			HelpersList.clear();
			// Retrieve Item Lists from file
			BufferedReader HelperList = new BufferedReader(new InputStreamReader(new URL(WebAPI.HelpersURL).openConnection().getInputStream()));
			String HelperLine;
			HelperLine = HelperList.readLine();
			while (HelperLine != null) {
				Date HelperNameDate = new SimpleDateFormat("yyyy/MM/dd' 'HH:mm:ss").parse(HelperLine.substring(HelperLine.indexOf(" - ") +3));
				
				String UUID = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + HelperLine.substring(0, HelperLine.indexOf(" - ")) + "?at=" + Math.round(HelperNameDate.getTime() / 1000)).openConnection().getInputStream())).readLine();
				UUID = UUID.substring(UUID.indexOf("\"id\":\"") +6, UUID.indexOf("\",\"", UUID.indexOf("\"id\":\"") +6));
				
				String Username = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/user/profiles/" + UUID + "/names").openConnection().getInputStream())).readLine();
				Username = Username.substring(Username.lastIndexOf("{\"name\":\"") +9, Username.indexOf("\"", Username.lastIndexOf("{\"name\":\"") +9));
				
				HelpersList.add(Username);
				HelperLine = HelperList.readLine();
			}
			HelperList.close();
			ExpReference.consoleOut("Successfully retrieved " + HelpersList.size() + " Helpers");
		} catch (Exception ignore) {}
	}

	public static void cleanScoreboards() {
		Scoreboard Scoreboard = ModCore.mc().player.getWorldScoreboard();
		if (Scoreboard.getTeamNames().contains("Party")) Scoreboard.removeTeam(Scoreboard.getTeam("Party"));
		if (Scoreboard.getTeamNames().contains("Friends")) Scoreboard.removeTeam(Scoreboard.getTeam("Friends"));
		if (Scoreboard.getTeamNames().contains("Guild")) Scoreboard.removeTeam(Scoreboard.getTeam("Guild"));
		if (Scoreboard.getTeamNames().contains("Helpers")) Scoreboard.removeTeam(Scoreboard.getTeam("Helpers"));
		if (Scoreboard.getTeamNames().contains("Player")) Scoreboard.removeTeam(Scoreboard.getTeam("Player"));

		if (Scoreboard.getTeamNames().contains("VeryLowHealth")) Scoreboard.removeTeam(Scoreboard.getTeam("VeryLowHealth"));
		if (Scoreboard.getTeamNames().contains("LowHealth")) Scoreboard.removeTeam(Scoreboard.getTeam("LowHealth"));
		if (Scoreboard.getTeamNames().contains("MidHealth")) Scoreboard.removeTeam(Scoreboard.getTeam("MidHealth"));
		if (Scoreboard.getTeamNames().contains("HighHealth")) Scoreboard.removeTeam(Scoreboard.getTeam("HighHealth"));
		if (Scoreboard.getTeamNames().contains("VeryHighHealth")) Scoreboard.removeTeam(Scoreboard.getTeam("VeryHighHealth"));

		if (Scoreboard.getTeamNames().contains("Mythic")) Scoreboard.removeTeam(Scoreboard.getTeam("Mythic"));
		if (Scoreboard.getTeamNames().contains("Legendary")) Scoreboard.removeTeam(Scoreboard.getTeam("Legendary"));
		if (Scoreboard.getTeamNames().contains("Rare")) Scoreboard.removeTeam(Scoreboard.getTeam("Rare"));
		if (Scoreboard.getTeamNames().contains("Unique")) Scoreboard.removeTeam(Scoreboard.getTeam("Unique"));
		if (Scoreboard.getTeamNames().contains("Set")) Scoreboard.removeTeam(Scoreboard.getTeam("Set"));
	}
}
