/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.chat.overlays.gui;

import com.wynntils.modules.chat.overlays.ChatOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.init.SoundEvents;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class ChatGUI extends GuiChat {
    
    public ChatGUI() {
        
    }
    
    public ChatGUI(String defaultInputText) {
        super(defaultInputText);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(ChatOverlay.getChat().getOverTabId() > -1) {
            if(mouseButton == 1) mc.displayGuiScreen(new TabGUI(ChatOverlay.getChat().getOverTabId()));
            else ChatOverlay.getChat().setCurrentTab(ChatOverlay.getChat().getOverTabId());
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
        }
        else if(ChatOverlay.getChat().getOverTabId() == -2) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            mc.displayGuiScreen(new TabGUI(-2)) ;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(inputField.getText().isEmpty() && keyCode == Keyboard.KEY_TAB) ChatOverlay.getChat().switchTabs();
        super.keyTyped(typedChar, keyCode);
    }

}
