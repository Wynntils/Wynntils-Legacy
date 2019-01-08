package cf.wynntils.modules.chat.overlays;

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
        if(ChatOverlay.getChat().getOverTabId() > -1) ChatOverlay.getChat().setCurrentTab(ChatOverlay.getChat().getOverTabId());
        else if(ChatOverlay.getChat().getOverTabId() == -2) //todo open the gui for creating a new chat

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_TAB) ChatOverlay.getChat().switchTabs();

        super.keyTyped(typedChar, keyCode);
    }

}
