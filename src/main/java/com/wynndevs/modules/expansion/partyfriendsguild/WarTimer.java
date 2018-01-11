package com.wynndevs.modules.expansion.partyfriendsguild;

import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.misc.ModGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.text.DecimalFormat;

public class WarTimer extends ModGui{
	
	public static boolean EnableWarTimer = false;
	public static boolean WarTimerLeft = false;
	
	public static long TimeStamp = 0L;

	public static int mobCount = 0;
	
	public WarTimer(Minecraft mc) {
		if (TimeStamp == 0L || TimeStamp > System.currentTimeMillis()) {
			TimeStamp = System.currentTimeMillis();
		}
		long Time = ((System.currentTimeMillis() - TimeStamp) / 1000L);
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		FontRenderer font = mc.fontRenderer;
		if (EnableWarTimer) {
			String Message = "WAR: " + String.valueOf('\u00a7') + "b" + new DecimalFormat("00").format(Math.floor(Time / 60L)) + ':' + new DecimalFormat("00").format(Time % 60L);
			this.drawString(font, Message, (WarTimerLeft ? 4 : width - ExpReference.getMsgLength(Message, 2.0f) - 2), 4, 2.0f, Integer.parseInt("00AAAA",16));
		}

		if (false) { //IF SETTING IS ON
			String mobs = mobCount + " Mobs Remaining";
			this.drawString(font, mobs, (WarTimerLeft ? 4 : width - ExpReference.getMsgLength(mobs, 1.0f) - 2), 14, 1.0f, Integer.parseInt("00AAAA", 16));
		}

		if (Math.floor(Time / 60L) >= 30L) {
			mc.player.sendChatMessage("/servers");
		}
	}
}
