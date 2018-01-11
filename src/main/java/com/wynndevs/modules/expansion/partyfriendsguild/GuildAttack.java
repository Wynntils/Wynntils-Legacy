package com.wynndevs.modules.expansion.partyfriendsguild;

import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.webapi.Territory;
import com.wynndevs.modules.expansion.webapi.WynnTerritory;
import com.wynndevs.modules.expansion.sound.MovingSoundMusic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;

public class GuildAttack {
	
	private static SoundEvent Horn = new SoundEvent(new ResourceLocation(Reference.MOD_ID, "horn"));
	private static SoundEvent Drum = new SoundEvent(new ResourceLocation(Reference.MOD_ID, "drum"));
	
	public static boolean GuildWarWaypoints = false;
	public static boolean AttackTimer = false;
	public static boolean RemoveExcessWar = false;
	public static boolean WarHornWarmup = false;
	public static boolean WarHornAttack = false;
	
	public static WynnTerritory CurrentTerritory = new WynnTerritory();
	private static int TimeToChat = -1;
	
	public static boolean ChatHandler(String msg){
		
		if (GuildWarWaypoints && msg.contains(CurrentTerritory.Name + ": " + CurrentTerritory.GetFormatedCoords()) && TimeToChat > -1){
			TimeToChat = -1;
		}
		
		if (msg.startsWith("[WAR] A war is about to start! Your guild is attacking ")){
			String TerritoryName = msg.substring(msg.indexOf("attacking ") +10, msg.indexOf("!", msg.indexOf("attacking ") +10));
			if (!TerritoryName.equals(CurrentTerritory.Name)) {
				CurrentTerritory = new WynnTerritory();
				for (int i=0;i<Territory.TerritoryList.size();i++){
					if (Territory.TerritoryList.get(i).Name.equals(TerritoryName)){
						CurrentTerritory = Territory.TerritoryList.get(i);
						break;
					}
				}
				if (GuildWarWaypoints) {
					if (!CurrentTerritory.HasCoords()) {
							ExpReference.postToChat(new TextComponentString(String.valueOf('\u00a7') + "c[Coords not found for " + TerritoryName + "]"));
					}else{
						TimeToChat = (int) Math.ceil(Math.random()*300);
					}
				}
				if (WarHornWarmup) {
					ModCore.mc().getSoundHandler().playSound(new MovingSoundMusic(Horn));
				}
			}
		}
		if (msg.startsWith("[WAR] The battle will begin in 0 seconds")){
			CurrentTerritory = new WynnTerritory();
			GuildAttackTimer.Timer = -1;
			if (RemoveExcessWar){return true;}
		}else if (WarHornAttack && msg.startsWith("[WAR] The battle will begin in 5 seconds")){
				ModCore.mc().getSoundHandler().playSound(new MovingSoundMusic(Drum));
				if (RemoveExcessWar){return true;}
		}else if ((msg.startsWith("[WAR] The battle will begin in") || msg.startsWith("[WAR] A war is about to start!") || msg.startsWith("[WAR] You must be in the region")) && AttackTimer){
			if (msg.startsWith("[WAR] The battle will begin in")){
				if (msg.contains("minute")){
					GuildAttackTimer.Timer = Integer.parseInt(msg.substring(msg.indexOf("begin in ") +9, msg.indexOf(" minute"))) *60 + Integer.parseInt(msg.substring(msg.indexOf(", ") +2, msg.indexOf(" second")));
					GuildAttackTimer.TimerDelay.Reset();
				}else{
					GuildAttackTimer.Timer = Integer.parseInt(msg.substring(msg.indexOf(" in ") +4, msg.indexOf(" second")));
					GuildAttackTimer.TimerDelay.Reset();
				}
			}
			if (RemoveExcessWar){return true;}
		}
		
		return false;
	}
	
	public static void GuildChatWarCoords(){
		if (TimeToChat == 0){
			if (CurrentTerritory.HasCoords()) {
				ModCore.mc().player.sendChatMessage("/g " + CurrentTerritory.Name + ": " + CurrentTerritory.GetFormatedCoords());
			}
			TimeToChat = -1;
		}else if (TimeToChat > 0){
			TimeToChat--;
		}
	}
}
