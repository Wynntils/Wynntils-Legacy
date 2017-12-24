package com.wynndevs.modules.expansion.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

public class Announcments extends ModGui{
	
	public static Delay AnnounceTime = new Delay(5.0f, false);
	public static String AnnounceMessage = "";
	
	public Announcments(Minecraft mc) {
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		int height = scaled.getScaledHeight();
		FontRenderer font = mc.fontRenderer;
		
		if (!AnnounceTime.Passed()){
			this.drawCenteredString(font, AnnounceMessage, width/2, height/5, 1.25f, Integer.parseInt("AA00AA",16));
		}
	}
}
