package com.wynndevs.modules.expansion;


import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.misc.ChatTimeStamp;
import com.wynndevs.modules.expansion.misc.Delay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.ITextComponent;

import java.util.Collection;

public class ExpReference {
	
	public static final String MOD_ID = Reference.MOD_ID;
	public static final String NAME = Reference.NAME;
	public static String VERSION = "";
	//public static final String ACCEPTED_MC_VERS = "[1.11]";
	
	private static boolean InGame = false;
	private static boolean InWar = false;
	private static boolean InNether = false;
	
	
	public static byte Class = 0;
	public static boolean Loaded = false;
	private static Delay LoadedTimer = new Delay(2.5f, false);
	
	public static GuiNewChat Chat() {return ModCore.mc().ingameGUI.getChatGUI();}
	public static void PostToChat(ITextComponent Message) {ModCore.mc().ingameGUI.getChatGUI().printChatMessage((Message.getUnformattedText().equals("") ? Message : ChatTimeStamp.AddTimeStamp(Message)));}
	public static boolean inServer(){
		try{
			if (Minecraft.getMinecraft().isSingleplayer()) {
				return false;
			}
			return Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("wynncraft");
		}catch (NullPointerException e){
			return false;
		}
	}
	public static void UpdateStatus() {
		InGame = false;
		Loaded = false;
		if (inServer()) {
			try {
				ItemStack book = ModCore.mc().player.inventory.getStackInSlot(7);
				if (book.hasDisplayName() && book.getDisplayName().endsWith("Quest Book")) {
					InGame = true;
					if (LoadedTimer.Passed()) {
						Loaded = true;
					}
				}
			} catch (Exception ignored){}
			
			try {
				if (InGame) {
					Collection<NetworkPlayerInfo> Tablist = Minecraft.getMinecraft().getConnection().getPlayerInfoMap();
					for (NetworkPlayerInfo TabSlot : Tablist) {
						if (ModCore.mc().ingameGUI.getTabList().getPlayerName(TabSlot).contains("Global [")) {
							String ServerCheck = ModCore.mc().ingameGUI.getTabList().getPlayerName(TabSlot);
							InWar = ServerCheck.contains("WAR");
							InNether = ServerCheck.contains("N");
							return;
						}
					}
					
				}else{
					InWar = false;
					InNether = false;
					LoadedTimer.Reset();
				}
			} catch (Exception ignored){
				InWar = false;
				InNether = false;
				LoadedTimer.Reset();
			}
		}
	}
	public static boolean inGame(){
		return InGame;
	}
	public static boolean inWar(){
		return InWar;
	}
	public static boolean inNether(){
		return InNether;
	}
	public static void Disconnect(GuiScreen guiScreen) {
		// Method recreated from GuiIngameMenu.ActionPerformed.case1
		
		InGame = false;
		InWar = false;
		InNether = false;
		
		Loaded = false;
		
		ModCore.mc().world.sendQuittingDisconnectingPacket();
		ModCore.mc().loadWorld((WorldClient)null);
		ModCore.mc().displayGuiScreen(guiScreen);
	}
	
	public static int GetMsgLength(String Msg, float Size){return (int) Math.floor(ModCore.mc().fontRenderer.getStringWidth(Msg) * Size);}
	
	public static void CleanScoreboards() {
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
	public static void ConsoleOut(String Output) {
		System.out.println("[WynnExp] " + Output);
	}
}
