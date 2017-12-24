package com.wynndevs.modules.expansion.misc;

import com.wynndevs.ModCore;
import com.wynndevs.modules.expansion.WynnExpansion;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class SilentMode {
	
	private static String SilentMessage = "Your message wont be seen, I'm running in silent mode";
	
	public static void ChatChecker(ClientChatReceivedEvent event) {
		String msg = event.getMessage().getUnformattedText();
		if (msg.contains(String.valueOf('\u27A4') + " " + ModCore.mc().player.getName() + "] ") && !msg.startsWith("[" + ModCore.mc().player.getName() + " " + String.valueOf('\u27A4'))) {
			String User = msg.substring(msg.indexOf("[") +1, msg.indexOf(" "));
			WynnExpansion.ChatQue.add("/msg " + User + " " + SilentMessage);
		}
		if (msg.contains(String.valueOf('\u27A4'))) {
			if (!msg.endsWith(SilentMessage)) System.out.println(msg);
			event.setCanceled(true);
		}
	}
}
