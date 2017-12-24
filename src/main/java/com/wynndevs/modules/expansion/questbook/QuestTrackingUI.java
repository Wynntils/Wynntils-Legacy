package com.wynndevs.modules.expansion.questbook;

import com.wynndevs.modules.expansion.misc.ModGui;
import net.minecraft.client.Minecraft;

public class QuestTrackingUI extends ModGui {

	public QuestTrackingUI(Minecraft mc) {
		if (QuestBook.refreshDelay.PassedOnce()) {
			QuestBook.ReloadBook();
		}

		this.drawSplitString(mc.fontRenderer, QuestBook.selectedQuestDescription, 5, 5, 240, 0.9f, 1.4f, Integer.parseInt("FFFFFF", 16));
	}
}
