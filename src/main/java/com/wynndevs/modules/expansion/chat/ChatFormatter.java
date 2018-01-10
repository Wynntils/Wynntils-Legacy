package com.wynndevs.modules.expansion.chat;

import com.wynndevs.ConfigValues;
import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.misc.ChatReformater;
import com.wynndevs.modules.expansion.misc.ChatTimeStamp;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ChatFormatter {

    public static String lastMessage = "";
    public static int lastAmount = 2;
    public static final HashMap<String, String> wynnic = new HashMap<>();

    @SubscribeEvent
    public void formatChat(ClientChatReceivedEvent e) {
        if(e.isCanceled() || e.getType() != 1) {
            return;
        }

        if(wynnic.size() <= 0) {
            wynnic.put("⒜", "a");wynnic.put("⒝", "b");wynnic.put("⒞", "c");wynnic.put("⒟", "d");wynnic.put("⒠", "e");wynnic.put("⒡", "f");wynnic.put("⒢", "g");wynnic.put("⒣", "h");
            wynnic.put("⒤", "i");wynnic.put("⒥", "j");wynnic.put("⒦", "k");wynnic.put("⒧", "l");wynnic.put("⒨", "m");wynnic.put("⒩", "n");wynnic.put("⒪", "o");wynnic.put("⒫", "p");
            wynnic.put("⒬", "q");wynnic.put("⒭", "r");wynnic.put("⒮", "s");wynnic.put("⒯", "t");wynnic.put("⒰", "u");wynnic.put("⒱", "v");wynnic.put("⒲", "w");wynnic.put("⒳", "x");
            wynnic.put("⒴", "y");wynnic.put("⒵", "z");
        }

        String msgRaw = e.getMessage().getFormattedText();

        if (ConfigValues.Expansion.Chats.MainChat.mentionNotification && msgRaw.contains("/") && msgRaw.contains(":")) {
            String[] mm = msgRaw.split(":");
            if(mm.length < 2) {
                return;
            }
            if(mm[1].contains(ModCore.mc().player.getName())) {
                String playerName = ModCore.mc().player.getName();
                String mainMm = StringUtils.join(Arrays.copyOfRange(mm, 1, mm.length), ":");
                e.setMessage(new TextComponentString(mm[0] + ":" + mainMm.replace(playerName, "§e" + playerName + "§" + mm[1].charAt(1))));
                ModCore.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_PLING, 1.0F));
            }
        }

        ChatReformater.reformat(e);
        ChatTimeStamp.timeStamp(e);

        String translated = "";

        msgRaw = e.getMessage().getFormattedText();

        boolean acceptSpace = false;

        for(String x : msgRaw.split(" ")) {
            for(char c : x.toCharArray()) {
                if(wynnic.containsKey(String.valueOf(c))) {
                    translated+= wynnic.get(String.valueOf(c));
                    acceptSpace = true;
                }
            }

            if(acceptSpace) {
                translated+=" ";
                acceptSpace = false;
            }
        }

        if(!translated.equals("")) {
            e.setMessage(new TextComponentString(msgRaw).setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("§bWynnic Translation:§e " + translated)))));
        }

        msgRaw = e.getMessage().getFormattedText();
        Reference.LOGGER.warn(ConfigValues.wynnExpansion.chat.main.filter);
        if (ConfigValues.wynnExpansion.chat.main.filter && e.getMessage().getFormattedText().equals(lastMessage)) {
            GuiNewChat ch = ModCore.mc().ingameGUI.getChatGUI();

            if(ch == null) {
                return;
            }

            try{
                //getting lines
                Field lField = ch.getClass().getDeclaredFields()[3];
                lField.setAccessible(true);
                List<ChatLine> oldLines = (List<ChatLine>)lField.get(ch);

                if(oldLines == null || oldLines.size() <= 0) {
                    return;
                }

                if (ConfigValues.wynnExpansion.chat.main.filter) {

                    //updating first message
                    ChatLine line = oldLines.get(0);
                    Field txt = line.getClass().getDeclaredFields()[1];
                    txt.setAccessible(true);
                    txt.set(line, new TextComponentString(lastMessage + " §7[" + lastAmount++ + "x]" + ConfigValues.wynnExpansion.chat.main.filter));

                    //refreshing
                    ch.refreshChat();
                    e.setCanceled(true);
                }
            }catch (Exception  ex) { ex.printStackTrace(); }
            return;
        }
        lastAmount = 2;
        lastMessage = msgRaw;
    }

}
