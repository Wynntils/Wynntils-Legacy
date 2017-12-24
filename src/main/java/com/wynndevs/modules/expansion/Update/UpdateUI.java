package com.wynndevs.modules.expansion.Update;

import com.wynndevs.modules.expansion.ExpReference;
import com.wynndevs.modules.expansion.Misc.Delay;
import com.wynndevs.modules.expansion.Misc.ModGui;
import net.minecraft.client.Minecraft;

public class UpdateUI extends ModGui {
	
	private static Delay UpdateDisplayTimer = new Delay(30.0f, false);
	private static Delay SlideUpDelay = new Delay(0.03f, true);
	private static int SlideUp = 50;
	private static boolean Showing = false;
	public static boolean Show = false;
	
	public UpdateUI(Minecraft mc) {
		if (Show){
			drawRect(0, 0 -SlideUp, 203, 43 -SlideUp, 0xff000000);
			drawRect(0, 0 -SlideUp, 200, 40 -SlideUp, 0xff515151);
			drawString(mc.fontRenderer, "Wynn Expansion", 5, 3 -SlideUp, 0xffff9000);
			drawString(mc.fontRenderer, "Update v" + Update.latest + " is Available!", 8, 17 -SlideUp, 0xffffffff);
			drawString(mc.fontRenderer, "Currently Using: " + ExpReference.VERSION, 8, 27 -SlideUp, 0xffbbbbbb);
			if (!Showing && SlideUp > 0 && SlideUpDelay.Passed()){
				SlideUp--;
				if (SlideUp == 0){
					Showing = true;
					UpdateDisplayTimer.Reset();
				}
			}else if (Showing && UpdateDisplayTimer.Passed() && SlideUpDelay.Passed()){
				SlideUp++;
				if (SlideUp == 50){
					Showing = false;
					Show = false;
				}
			}
		}
	}
}
