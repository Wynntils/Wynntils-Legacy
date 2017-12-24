package com.wynndevs.modules.expansion.Experience;

import com.wynndevs.ModCore;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.text.DecimalFormat;

public class SoulpointTime {
	
	public static boolean SoulPointTime = false;
	
	public static void SoulpointPrintTime(){
		//System.out.println(Minecraft.getMinecraft().world.getWorldTime() % 24000);
		//System.out.println(Minecraft.getMinecraft().world.getTotalWorldTime() % 24000);
		//long time = world.getWorldTime();
		//long days = (time / 24000L);
		//long current = (time + 6000L) % 24000L; // 0 is 6:00 am, 18000 is midnight, so add 6000
		//int h = (int)(current / 1000L);
		//int m = (int)((current % 1000L) * 3 / 50); // 1000 ticks divided by 60 minutes = 16 and 2/3
		//System.out.println("Time: " + days + "d " + h + ":" + m + "   TIMES " + time + " - " + current);
		
		if (SoulPointTime){
			long TimeLeft = (24000L - (ModCore.mc().world.getWorldTime() % 24000L)) / 20;
			ItemStack Soulpoints = Minecraft.getMinecraft().player.inventory.getStackInSlot(8);
			Soulpoints.setStackDisplayName(String.valueOf('\u00a7') + 'l' + String.valueOf('\u00a7') + 'o' + Soulpoints.getCount() + String.valueOf('\u00a7') + 'r' + String.valueOf('\u00a7') + "b Soul Points " + String.valueOf('\u00a7') + "3[" + new DecimalFormat("00").format(Math.round(TimeLeft / 60)) + ':' + new DecimalFormat("00").format(TimeLeft % 60) + ']');
		}else{
			ItemStack Soulpoints = Minecraft.getMinecraft().player.inventory.getStackInSlot(8);
			Soulpoints.setStackDisplayName(String.valueOf('\u00a7') + 'l' + String.valueOf('\u00a7') + 'o' + Soulpoints.getCount() + String.valueOf('\u00a7') + 'r' + String.valueOf('\u00a7') + "b Soul Points");
		}
	}
}
