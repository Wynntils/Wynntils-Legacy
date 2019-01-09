package cf.wynntils.modules.chat.overlays.gui;

import cf.wynntils.modules.chat.overlays.ChatOverlay;
import net.minecraft.client.gui.GuiChat;
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
        }
        else if(ChatOverlay.getChat().getOverTabId() == -2) mc.displayGuiScreen(new TabGUI(-2));

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_TAB) ChatOverlay.getChat().switchTabs();

        super.keyTyped(typedChar, keyCode);
    }

}
