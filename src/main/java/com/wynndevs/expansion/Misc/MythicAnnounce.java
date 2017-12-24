package com.wynndevs.expansion.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

public class MythicAnnounce extends ModGui{
	
	public static Delay MythicDisplayTime = new Delay(5.0f, false);
	public static boolean MythicAnnounce = false;
	public static Delay LegendaryDisplayTime = new Delay(5.0f, false);
	public static boolean LegendaryAnnounce = false;

	public MythicAnnounce(Minecraft mc) {
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
		FontRenderer font = mc.fontRenderer;
		
		if (!MythicDisplayTime.Passed()){
			this.drawCenteredString(font, "Mythic Item Appeared", width/2, height/5, 3.0f, Integer.parseInt("AA00AA",16));
		}else if (MythicAnnounce){
			MythicAnnounce = false;
		}else if (!LegendaryDisplayTime.Passed()){
			this.drawCenteredString(font, "Legendary Item Appeared", width/2, height/5, 2.0f, Integer.parseInt("55FFFF",16));
		}else if (LegendaryAnnounce){
			LegendaryAnnounce = false;
		}
	}
}
