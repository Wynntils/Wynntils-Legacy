/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.chat.events;

import java.util.List;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.WynncraftServerEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.modules.chat.configs.ChatConfig;
import com.wynntils.modules.chat.managers.ChatManager;
import com.wynntils.modules.chat.managers.HeldItemChatManager;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.modules.chat.overlays.gui.ChatGUI;
import com.wynntils.webapi.services.TranslationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import scala.actors.threadpool.Arrays;

public class ClientEvents implements Listener {
    
    @SuppressWarnings("unchecked")
    private static final List<String> POWDER_MANUAL_LINES = Arrays.asList(new String[] {
            "                     Chapter 1 - The Basics",
            "    There exist five varieties of magic powder, each corresponding to one of five different elements; ✹ Fire, ❉ Water, ✦ Thunder, ✤ Earth, and ❋ Air.",
            "    It is possible to augment items with these powders, if one is versed in the art of enchantment. Many offer this service for a price, as Powder Masters.",
            "    Certain items have a higher magical capacity, and thusly are able to hold more powders within them. Some have many, some have few, others still have none.",
            "    Should a weapon be enchanted with a powder, the powder will imbue the item with a slight amount of elemental damage, but also transfer basic neutral damage into its element.",
            "    Should a piece of armour be enchanted with a powder, the garment will gain a resistance towards that powder's element, at the cost of an elemental weakness.",
            "                  Chapter 2 - Elemental Abilities",
            "    If a powder is more concentrated, they can unlock powerful elemental magicks within an item. Powders appraised to be Tier IV or higher are capable of forming these special abilities.",
            "    Two or more like-elemented powders are necessary for this. While items can be augmented with powders even after unlocking an ability, only one amplified magic power can be kept within an item at one time.",
            "    Weapons and armour channel this magical energy in radically different ways, just as powders change their more mundane abilities. The method of use for each piece can be referred to in Chapter 3.",
            "    Using different tiers of powder to try to form an ability on an item is somewhat unpredictable in the end potency of the magic, but using higher tiers of powder will generally lead to a stronger effect.",
            "    To unleash the unique ability imbued in your weapon, one must first charge the power by attacking enemies. Then, heavily focus on the latent energies within the item, and release them with a swing. (Shift+Left-Click)",
            "                  Chapter 3 - Elemental Abilities",
            "    ✤ Earth:  Earth-imbued weapons will unleash a powerful quake, rupturing the nearby ground and disorienting enemies. Earth-augmented armors produce a rage-state in the user as they near death, turning Earth-based attacks more potent, and more violent.",
            "    ✦ Thunder:  Thunder-imbued weapons can release a streak of lightning that seeks enemies, bouncing from one foe to the next nearby. Thunder-augmented armors will steal the life force of the slain, and in kind increase the power of Thunder-based attacks temporarily.",
            "    ❉ Water:  Water-imbued weapons will curse one's nearby enemies. This curse weakens armor, and turns them vulnerable to further attack. Water-augmented armors recycle used mana, transferring it into a short boost to Water-affiliated techniques, depending on how much mana was consumed.",
            "    ✹ Fire:  Fire-imbued weapons will generate a fan of flames that burn one's enemies and empower one's allies. Fire-augmented armors react to being struck, turning the impact of the blow, no matter how weak, into a temporary power boost to Fire-based attacks.",
            "    ❋ Air:  Air-imbued weapons trap nearby enemies in a vortex of wind, and blow the victims away when struck. Air-augmented armors will, so long as they are kept close, leach energy from nearby foes to improve the strength of Air-based attacks."
    });
    
    private boolean ignoreNextBlank = false;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent e) {
        if (e.getGui() instanceof GuiChat) {
            if (e.getGui() instanceof ChatGUI) return;
            String defaultText = (String) ReflectionFields.GuiChat_defaultInputFieldText.getValue(e.getGui());

            e.setGui(new ChatGUI(defaultText));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onChatReceived(ClientChatReceivedEvent e) {
        if (e.getMessage().getUnformattedText().startsWith("[Info] ") && ChatConfig.INSTANCE.filterWynncraftInfo) {
            e.setCanceled(true);
        } else if (e.getMessage().getFormattedText().startsWith(TextFormatting.GRAY + "[You are now entering") && ChatConfig.INSTANCE.filterTerritoryEnter) {
            e.setCanceled(true);
        } else if (POWDER_MANUAL_LINES.contains(e.getMessage().getUnformattedText()) && ChatConfig.INSTANCE.customPowderManual) {
            e.setCanceled(true);
        } else if (e.getMessage().getUnformattedText().equals("                         Powder Manual") && ChatConfig.INSTANCE.customPowderManual) {
            ignoreNextBlank = true;
        } else if (e.getMessage().getUnformattedText().isEmpty() && ignoreNextBlank) {
            e.setCanceled(true);
            ignoreNextBlank = false;
        }
    }

    /**
     * Used for replacing commands by others, also knows as, creating aliases
     *
     * Replacements:
     * /tell -> /msg
     * /xp -> /guild xp
     */
    @SubscribeEvent
    public void commandReplacements(ClientChatEvent e) {
        if (e.getMessage().startsWith("/tell")) e.setMessage(e.getMessage().replaceFirst("/tell", "/msg"));
        else if (e.getMessage().startsWith("/xp")) e.setMessage(e.getMessage().replaceFirst("/xp", "/guild xp"));
    }


    @SubscribeEvent
    public void onWynnLogin(WynncraftServerEvent.Login e) {
        ReflectionFields.GuiIngame_persistantChatGUI.setValue(Minecraft.getMinecraft().ingameGUI, new ChatOverlay());
        TranslationManager.init();
    }

    @SubscribeEvent
    public void onWynnLogout(WynncraftServerEvent.Leave e) {
        TranslationManager.shutdown();
    }

    @SubscribeEvent
    public void onSendMessage(ClientChatEvent e) {
        if (e.getMessage().startsWith("/")) return;

        Pair<String, Boolean> message = ChatManager.applyUpdatesToServer(e.getMessage());
        e.setMessage(message.a);
        if (message.b || message.a.isEmpty() || message.a.trim().isEmpty()) {
            e.setCanceled(true);
            return;
        }

        if (!ChatOverlay.getChat().getCurrentTab().getAutoCommand().isEmpty())
            e.setMessage(ChatOverlay.getChat().getCurrentTab().getAutoCommand() + " " + e.getMessage());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {
        if (!Reference.onWorld) return;

        HeldItemChatManager.onTick();
    }

}
