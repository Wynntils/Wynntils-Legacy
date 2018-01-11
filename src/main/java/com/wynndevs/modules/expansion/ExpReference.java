package com.wynndevs.modules.expansion;

import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.misc.ChatTimeStamp;
import net.minecraft.util.text.ITextComponent;

public class ExpReference {
	
	public static byte Class = 0;

	public static void postToChat(ITextComponent Message) {ModCore.mc().ingameGUI.getChatGUI().printChatMessage((Message.getUnformattedText().equals("") ? Message : ChatTimeStamp.AddTimeStamp(Message)));}
	
	public static int getMsgLength(String msg, float size){return (int) Math.floor(ModCore.mc().fontRenderer.getStringWidth(msg) * size);}

	public static void consoleOut(String output) {
		Reference.LOGGER.info(output);
	}

}
