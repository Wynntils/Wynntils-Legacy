package cf.wynntils.modules.chat.events;

import cf.wynntils.core.events.custom.WynncraftServerEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.utils.Pair;
import cf.wynntils.core.utils.ReflectionFields;
import cf.wynntils.modules.chat.configs.ChatConfig;
import cf.wynntils.modules.chat.managers.ChatManager;
import cf.wynntils.modules.chat.overlays.ChatGUI;
import cf.wynntils.modules.chat.overlays.ChatOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents implements Listener {

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent e) {
        if(e.getGui() instanceof GuiChat) {
            if(e.getGui() instanceof ChatGUI) return;
            String defaultText = (String) ReflectionFields.GuiChat_defaultInputFieldText.getValue(e.getGui());

            e.setGui(new ChatGUI(defaultText));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatRecieved(ClientChatReceivedEvent e) {
        if (e.getMessage().getFormattedText().startsWith("§4[Info] ") && ChatConfig.INSTANCE.filterWynncraftInfo)
            e.setCanceled(true);
    }

    @SubscribeEvent
    public void onWynnLogin(WynncraftServerEvent.Login e) {
        ReflectionFields.GuiIngame_persistantChatGUI.setValue(Minecraft.getMinecraft().ingameGUI, new ChatOverlay());
    }

    @SubscribeEvent
    public void onSendMessage(ClientChatEvent e) {
        if(e.getMessage().startsWith("/")) return;

        Pair<String, Boolean> message = ChatManager.applyUpdatesToServer(e.getMessage());
        e.setMessage(message.a);
        if (message.b) {
            e.setCanceled(true);
        }

        if(!ChatOverlay.getChat().getCurrentTab().getAutoCommand().isEmpty()) e.setMessage(ChatOverlay.getChat().getCurrentTab().getAutoCommand() + " " + e.getMessage());
    }

}
