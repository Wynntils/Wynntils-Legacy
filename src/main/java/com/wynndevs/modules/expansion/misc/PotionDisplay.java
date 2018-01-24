package com.wynndevs.modules.expansion.misc;

import com.wynndevs.ModCore;
import com.wynndevs.modules.expansion.ExpReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PotionDisplay extends ModGui{
	
	public static boolean PotionAllignRight = false;
	public static boolean PotionShadow = false;
	public static boolean PotionHideTimer = false;
	public static boolean PotionCenterVerticaly = false;
	
	public static List<String[]> Effects = new ArrayList<String[]>();
	
	private static boolean UseGameTime = false;
	
	public PotionDisplay(Minecraft mc){
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
		FontRenderer font = mc.fontRenderer;
		
		for (int i=0;i<Effects.size();i++){
            String TimeFormat;
            Long Time;
			if (UseGameTime){
				Time = ((Long.parseLong(Effects.get(i)[1]) - ModCore.mc().world.getWorldTime()) / 20);
			}else{
				Time = (Long.parseLong(Effects.get(i)[1]) - System.currentTimeMillis()) / 1000;
			}
			
			Float Size = 1.0f;
			if (Time >= 30){
				TimeFormat = String.valueOf('\u00a7') + "a" + new DecimalFormat("00").format(Math.round(Time / 60)) + ':' + new DecimalFormat("00").format(Time % 60);
			}else if (Time >= 10){
				TimeFormat = String.valueOf('\u00a7') + "e" + new DecimalFormat("00").format(Math.round(Time / 60)) + ':' + new DecimalFormat("00").format(Time % 60);
				Size = 1.10f;
			}else{
				if (UseGameTime){
					TimeFormat = String.valueOf('\u00a7') + "c" + new DecimalFormat("00").format(Math.round(Time / 60)) + ':' + new DecimalFormat("00").format(Time % 60) + ':' + new DecimalFormat("00").format(((Long.parseLong(Effects.get(i)[1]) - ModCore.mc().world.getWorldTime()) % 20) * 5);
				}else{
					TimeFormat = String.valueOf('\u00a7') + "c" + new DecimalFormat("00").format(Math.round(Time / 60)) + ':' + new DecimalFormat("00").format(Time % 60) + ':' + new DecimalFormat("00").format(((Long.parseLong(Effects.get(i)[1]) - System.currentTimeMillis()) % 1000) / 10);
				}
				Size = 1.25f;
			}
			
			if (PotionHideTimer) TimeFormat = "";
			
			if (PotionShadow){
				this.drawString(font, (PotionAllignRight ? TimeFormat + " " + Effects.get(i)[0] : Effects.get(i)[0] + " " + TimeFormat), (PotionAllignRight ? width - ExpReference.getMsgLength(TimeFormat + " " + Effects.get(i)[0], Size) - 4 : 4), (PotionCenterVerticaly ? (height/2) + Math.round((i*15)-((Size/2)*15)-(Effects.size()*7.5f)) : (height/5) + Math.round((i*15)-((Size/2)*15))), Size, Integer.parseInt("55FF55",16));
			}else{
				this.drawStringPlain(font, (PotionAllignRight ? TimeFormat + " " + Effects.get(i)[0] : Effects.get(i)[0] + " " + TimeFormat), (PotionAllignRight ? width - ExpReference.getMsgLength(TimeFormat + " " + Effects.get(i)[0], Size) - 4 : 4), (PotionCenterVerticaly ? (height/2) + Math.round((i*15)-((Size/2)*15)-(Effects.size()*7.5f)) : (height/5) + Math.round((i*15)-((Size/2)*15))), Size, Integer.parseInt("55FF55",16));
			}
			if ((UseGameTime ? Time < 0 : (Long.parseLong(Effects.get(i)[1]) - System.currentTimeMillis()) < 0)){
				Effects.remove(i);
				i--;
			}
		}
	}
	
	public static void UsePotion(String msg){
		if (msg.startsWith("[+") && msg.endsWith(" seconds]")){
			String[] Potion ={msg.substring(msg.indexOf("[+") +2, msg.indexOf(" ", msg.indexOf(" ", msg.indexOf("[+") +2) +1)), msg.substring(msg.indexOf("for ") +4, msg.indexOf(" seconds"))};
			if (Potion[0].contains(String.valueOf('\u273A'))){
				Potion[0] = String.valueOf('\u00a7') + "3+" + Potion[0];
			}else if (Potion[0].contains(String.valueOf('\u2739'))){
				Potion[0] = String.valueOf('\u00a7') + "c+" + Potion[0];
			}else if (Potion[0].contains(String.valueOf('\u2749'))){
				Potion[0] = String.valueOf('\u00a7') + "b+" + Potion[0];
			}else if (Potion[0].contains(String.valueOf('\u274B'))){
				Potion[0] = String.valueOf('\u00a7') + "f+" + Potion[0];
			}else if (Potion[0].contains(String.valueOf('\u2726'))){
				Potion[0] = String.valueOf('\u00a7') + "e+" + Potion[0];
			}else if (Potion[0].contains(String.valueOf('\u2724'))){
				Potion[0] = String.valueOf('\u00a7') + "2+" + Potion[0];
			}else if (Potion[0].contains("XP")){
				Potion[0] = String.valueOf('\u00a7') + "6+" + Potion[0];
			}else{
				Potion[0] = "+" + Potion[0];
			}
			
			if (UseGameTime){
				Potion[1] = String.valueOf((Long.parseLong(Potion[1]) * 20) + ModCore.mc().world.getWorldTime());
			}else{
				Potion[1] = String.valueOf(System.currentTimeMillis() + (Long.parseLong(Potion[1]) * 1000));
			}
			boolean Added = false;
			for (int i=0;i<Effects.size();i++){
				if (Long.parseLong(Potion[1]) < Long.parseLong(Effects.get(i)[1])){
					Effects.add(i, Potion);
					Added = true;
					break;
				}
			}
			if (!Added) Effects.add(Potion);
		}
	}
}
