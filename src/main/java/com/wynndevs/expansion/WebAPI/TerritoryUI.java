package com.wynndevs.expansion.WebAPI;

import com.wynndevs.expansion.ExpReference;
import com.wynndevs.expansion.Misc.Delay;
import com.wynndevs.expansion.Misc.ModGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TerritoryUI extends ModGui {
	
	public static List<String> TerritoryListUpdates = new CopyOnWriteArrayList<String>();
	private static List<Integer> NewsPosition = new ArrayList<Integer>();
	private static Delay NewsDelay = new Delay(0.05f, true);

	public TerritoryUI(Minecraft mc) {
		ScaledResolution scaled = new ScaledResolution(mc);
		int width = scaled.getScaledWidth();
		// int height = scaled.getScaledHeight();
		FontRenderer font = mc.fontRenderer;
		
		if (!TerritoryListUpdates.isEmpty() && TerritoryListUpdates.size() > NewsPosition.size()) {
			for (int i = NewsPosition.size(); i < TerritoryListUpdates.size(); i++) {
				try{
					if (TerritoryListUpdates.subList(0, i).contains(TerritoryListUpdates.get(i))){
						TerritoryListUpdates.remove(i);
						i--;
					}else{
						NewsPosition.add(-1);
					}
				}catch(Exception ignore){}
			}
		}
		if (!NewsPosition.isEmpty()) {
			boolean ShowAnother = true;
			boolean Iterate = false;
			if (NewsDelay.Passed()) {
				Iterate = true;
			}
			for (int i = 0; i < NewsPosition.size(); i++) {
				// Get News length
				int NewsLength = ExpReference.GetMsgLength(TerritoryListUpdates.get(i), 1.0f);

				// Iterate position
				if (Iterate && NewsPosition.get(i) > -1) {
					NewsPosition.set(i, NewsPosition.get(i) + 1);
				}

				// Disable showing another
				if (Iterate && NewsPosition.get(i) > -1 && NewsPosition.get(i) < NewsLength + 25) {
					ShowAnother = false;
				}

				// Show another if allowed
				if (Iterate && ShowAnother && NewsPosition.get(i) == -1) {
					NewsPosition.set(i, 0);
					ShowAnother = false;
				}

				// Display News
				if (NewsPosition.get(i) > -1) {
					this.drawString(font, TerritoryListUpdates.get(i).replace(String.valueOf('\u2749'), ""), width - NewsPosition.get(i), 20, 1.0f, 0xff55FFFF);
				}

				// Remove old News
				if (NewsPosition.get(i) > width + NewsLength) {
					NewsPosition.remove(i);
					TerritoryListUpdates.remove(i);
					i--;
				}
			}
		}
	}
}
