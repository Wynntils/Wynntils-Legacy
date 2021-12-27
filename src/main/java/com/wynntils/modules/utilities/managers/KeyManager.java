/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.enums.wynntils.WynntilsConflictContext;
import com.wynntils.core.framework.instances.KeyHolder;
import com.wynntils.core.framework.settings.ui.SettingsUI;
import com.wynntils.modules.core.CoreModule;
import com.wynntils.modules.map.overlays.MiniMapOverlay;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.events.ClientEvents;
import com.wynntils.modules.utilities.overlays.hud.GameUpdateOverlay;
import com.wynntils.modules.utilities.overlays.hud.StopWatchOverlay;
import com.wynntils.modules.utilities.overlays.ui.GearViewerUI;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;
import java.util.ArrayList;
import java.util.List;

public class KeyManager {

    private static float lastGamma = 1f;

    private static KeyHolder lockInventoryKey;
    private static KeyHolder favoriteTradeKey;
    private static KeyHolder checkForUpdatesKey;
    private static KeyHolder zoomInKey;
    private static KeyHolder zoomOutKey;
    private static KeyHolder stopwatchKey;
    private static KeyHolder itemScreenshotKey;
    private static KeyHolder showLevelOverlayKey;
    public static KeyBinding GuildMenuKeyBind;
    
    public static void registerKeys() {
        UtilitiesModule.getModule().registerKeyBinding("Gammabright", Keyboard.KEY_G, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            if (McIf.mc().gameSettings.gammaSetting < 1000) {
                lastGamma = McIf.mc().gameSettings.gammaSetting;
                McIf.mc().gameSettings.gammaSetting = 1000;
                return;
            }

            McIf.mc().gameSettings.gammaSetting = lastGamma;
        });

        checkForUpdatesKey = CoreModule.getModule().registerKeyBinding("Check for Updates", Keyboard.KEY_NONE, "Wynntils", true, WebManager::checkForUpdates);

        CoreModule.getModule().registerKeyBinding("Open Settings", Keyboard.KEY_P, "Wynntils", KeyConflictContext.IN_GAME, true, () -> McIf.mc().displayGuiScreen(SettingsUI.getInstance(McIf.mc().currentScreen)));

        lockInventoryKey = UtilitiesModule.getModule().registerKeyBinding("Lock Slot", Keyboard.KEY_H, "Wynntils", KeyConflictContext.GUI, true, () -> {});
        favoriteTradeKey = UtilitiesModule.getModule().registerKeyBinding("Favorite Trade", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.GUI, true, () -> {});

        UtilitiesModule.getModule().registerKeyBinding("Toggle AFK Protection", Keyboard.KEY_N, "Wynntils", KeyConflictContext.IN_GAME, true, ClientEvents::toggleAfkProtection);

        zoomInKey = CoreModule.getModule().registerKeyBinding("Zoom In", Keyboard.KEY_EQUALS, "Wynntils", KeyConflictContext.IN_GAME, false, () -> MiniMapOverlay.zoomBy(+1));

        zoomOutKey = CoreModule.getModule().registerKeyBinding("Zoom Out", Keyboard.KEY_MINUS, "Wynntils", KeyConflictContext.IN_GAME, false, () -> MiniMapOverlay.zoomBy(-1));

        CoreModule.getModule().registerKeyBinding("Cast First Spell", Keyboard.KEY_Z, "Wynntils", KeyConflictContext.IN_GAME, true, QuickCastManager::castFirstSpell);
        CoreModule.getModule().registerKeyBinding("Cast Second Spell", Keyboard.KEY_X, "Wynntils", KeyConflictContext.IN_GAME, true, QuickCastManager::castSecondSpell);
        CoreModule.getModule().registerKeyBinding("Cast Third Spell", Keyboard.KEY_C, "Wynntils", KeyConflictContext.IN_GAME, true, QuickCastManager::castThirdSpell);
        CoreModule.getModule().registerKeyBinding("Cast Fourth Spell", Keyboard.KEY_V, "Wynntils", KeyConflictContext.IN_GAME, true, QuickCastManager::castFourthSpell);

        CoreModule.getModule().registerKeyBinding("Mount Horse", Keyboard.KEY_Y, "Wynntils", KeyConflictContext.IN_GAME, true, MountHorseManager::mountHorseAndShowMessage);

        CoreModule.getModule().registerKeyBinding("Mob Totem Menu", Keyboard.KEY_J, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            if (!Reference.onWorld) return;

            McIf.player().sendChatMessage("/totem");
        });
        
        GuildMenuKeyBind = new KeyBinding("Open Guild Menu", Keyboard.KEY_R, "Wynntils");
        ClientRegistry.registerKeyBinding(GuildMenuKeyBind);

        CoreModule.getModule().registerKeyBinding("Attack Territory", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            if (!Reference.onWorld) return;

            McIf.player().sendChatMessage("/guild attack");
        });

        CoreModule.getModule().registerKeyBinding("Territory Menu", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            if (!Reference.onWorld) return;

            McIf.player().sendChatMessage("/guild territory");
        });

        CoreModule.getModule().registerKeyBinding("Pet Menu", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            if (!Reference.onWorld) return;

            McIf.player().sendChatMessage("/pets");
        });

        CoreModule.getModule().registerKeyBinding("Housing Edit Toggle", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            if (!Reference.onWorld) return;

            McIf.player().sendChatMessage("/housing edit");
        });

        CoreModule.getModule().registerKeyBinding("Open Ingredient Pouch", Keyboard.KEY_O, "Wynntils", KeyConflictContext.IN_GAME, true, PouchHotkeyManager::onIngredientHotkeyPress);

        CoreModule.getModule().registerKeyBinding("Open Emerald Pouch", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, PouchHotkeyManager::onEmeraldHotkeyPress);

        stopwatchKey = CoreModule.getModule().registerKeyBinding("Start/Stop Stopwatch", Keyboard.KEY_NUMPAD5, "Wynntils", KeyConflictContext.IN_GAME, true, StopWatchOverlay::start);

        itemScreenshotKey = CoreModule.getModule().registerKeyBinding("Screenshot Current Item", Keyboard.KEY_F4, "Wynntils", KeyConflictContext.GUI, true, () -> {});

        // -98 for middle click
        CoreModule.getModule().registerKeyBinding("View Player's Gear", -98, "Wynntils", KeyConflictContext.IN_GAME, true, GearViewerUI::openGearViewer);

        showLevelOverlayKey = UtilitiesModule.getModule().registerKeyBinding("Show Item Level Overlay", Keyboard.KEY_LCONTROL, "Wynntils", WynntilsConflictContext.AMBIENT, true, () -> {});
    }

    public static KeyHolder getFavoriteTradeKey() {
        return favoriteTradeKey;
    }

    public static KeyHolder getLockInventoryKey() {
        return lockInventoryKey;
    }

    public static KeyHolder getCheckForUpdatesKey() {
        return checkForUpdatesKey;
    }

    public static KeyHolder getZoomInKey() {
        return zoomInKey;
    }

    public static KeyHolder getZoomOutKey() {
        return zoomOutKey;
    }

    public static KeyHolder getStopwatchKey() {
        return stopwatchKey;
    }

    public static KeyHolder getItemScreenshotKey() {
        return itemScreenshotKey;
    }

    public static KeyHolder getShowLevelOverlayKey() {
        return showLevelOverlayKey;
    }

}
