/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.managers;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.enums.wynntils.WynntilsConflictContext;
import com.wynntils.core.framework.instances.KeyHolder;
import com.wynntils.core.framework.settings.ui.SettingsUI;
import com.wynntils.modules.core.CoreModule;
import com.wynntils.modules.core.managers.PartyManager;
import com.wynntils.modules.map.overlays.MiniMapOverlay;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.events.ClientEvents;
import com.wynntils.modules.utilities.overlays.hud.StopWatchOverlay;
import com.wynntils.modules.utilities.overlays.ui.GearViewerUI;
import com.wynntils.modules.utilities.overlays.ui.PartyManagementUI;
import com.wynntils.webapi.WebManager;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.lwjgl.input.Keyboard;

import java.util.Map;

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

        CoreModule.getModule().registerKeyBinding("Cast R-L-R Spell", Keyboard.KEY_Z, "Wynntils", KeyConflictContext.IN_GAME, true, QuickCastManager::castFirstSpell);
        CoreModule.getModule().registerKeyBinding("Cast R-R-R Spell", Keyboard.KEY_X, "Wynntils", KeyConflictContext.IN_GAME, true, QuickCastManager::castSecondSpell);
        CoreModule.getModule().registerKeyBinding("Cast R-L-L Spell", Keyboard.KEY_C, "Wynntils", KeyConflictContext.IN_GAME, true, QuickCastManager::castThirdSpell);
        CoreModule.getModule().registerKeyBinding("Cast R-R-L Spell", Keyboard.KEY_V, "Wynntils", KeyConflictContext.IN_GAME, true, QuickCastManager::castFourthSpell);

        CoreModule.getModule().registerKeyBinding("Mount Horse", Keyboard.KEY_Y, "Wynntils", KeyConflictContext.IN_GAME, true, MountHorseManager::mountHorseAndShowMessage);

        CoreModule.getModule().registerKeyBinding("Open Ingredient Pouch", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, PouchHotkeyManager::onIngredientHotkeyPress);
        CoreModule.getModule().registerKeyBinding("Open Emerald Pouch", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, PouchHotkeyManager::onEmeraldHotkeyPress);

        stopwatchKey = CoreModule.getModule().registerKeyBinding("Start/Stop Stopwatch", Keyboard.KEY_NUMPAD5, "Wynntils", KeyConflictContext.IN_GAME, true, StopWatchOverlay::start);

        itemScreenshotKey = CoreModule.getModule().registerKeyBinding("Screenshot Current Item", Keyboard.KEY_F4, "Wynntils", KeyConflictContext.GUI, true, () -> {});

        // -98 for middle click
        CoreModule.getModule().registerKeyBinding("View Player's Gear", -98, "Wynntils", KeyConflictContext.IN_GAME, true, GearViewerUI::openGearViewer);

        showLevelOverlayKey = UtilitiesModule.getModule().registerKeyBinding("Show Item Level Overlay", Keyboard.KEY_LCONTROL, "Wynntils", WynntilsConflictContext.AMBIENT, true, () -> {});

        CoreModule.getModule().registerKeyBinding("Open Party Management UI", Keyboard.KEY_RBRACKET, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            PartyManager.handlePartyList(); // Refresh list just before opening
            McIf.mc().displayGuiScreen(new PartyManagementUI());
        });

        RegisterCustomCommandKeybinds();
    }

    private static void RegisterCustomCommandKeybinds() {
        CoreModule.getModule().registerKeyBinding("Command Keybind 1", Keyboard.KEY_J, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            String cKeyBind = UtilitiesConfig.CommandKeybinds.INSTANCE.cKeyBind1;
            if (cKeyBind.isEmpty())
                return;
            if (McIf.mc().currentScreen != null || !Reference.onServer)
                return;

            if (handleIfClientCommand(cKeyBind)) return;

            //run server command
            McIf.player().sendChatMessage("/" + cKeyBind);
        });

        CoreModule.getModule().registerKeyBinding("Command Keybind 2", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            String cKeyBind = UtilitiesConfig.CommandKeybinds.INSTANCE.cKeyBind2;
            if (cKeyBind.isEmpty())
                return;
            if (McIf.mc().currentScreen != null || !Reference.onServer)
                return;

            if (handleIfClientCommand(cKeyBind)) return;

            McIf.player().sendChatMessage("/" + cKeyBind);
        });

        CoreModule.getModule().registerKeyBinding("Command Keybind 3", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            String cKeyBind = UtilitiesConfig.CommandKeybinds.INSTANCE.cKeyBind3;
            if (cKeyBind.isEmpty())
                return;
            if (McIf.mc().currentScreen != null || !Reference.onServer)
                return;

            if (handleIfClientCommand(cKeyBind)) return;

            McIf.player().sendChatMessage("/" + cKeyBind);
        });

        CoreModule.getModule().registerKeyBinding("Command Keybind 4", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            String cKeyBind = UtilitiesConfig.CommandKeybinds.INSTANCE.cKeyBind4;
            if (cKeyBind.isEmpty())
                return;
            if (McIf.mc().currentScreen != null || !Reference.onServer)
                return;

            if (handleIfClientCommand(cKeyBind)) return;

            McIf.player().sendChatMessage("/" + cKeyBind);
        });

        CoreModule.getModule().registerKeyBinding("Command Keybind 5", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            String cKeyBind = UtilitiesConfig.CommandKeybinds.INSTANCE.cKeyBind5;
            if (cKeyBind.isEmpty())
                return;
            if (McIf.mc().currentScreen != null || !Reference.onServer)
                return;

            if (handleIfClientCommand(cKeyBind)) return;

            McIf.player().sendChatMessage("/" + cKeyBind);
        });

        CoreModule.getModule().registerKeyBinding("Command Keybind 6", Keyboard.KEY_NONE, "Wynntils", KeyConflictContext.IN_GAME, true, () -> {
            String cKeyBind = UtilitiesConfig.CommandKeybinds.INSTANCE.cKeyBind6;
            if (cKeyBind.isEmpty())
                return;
            if (McIf.mc().currentScreen != null || !Reference.onServer)
                return;

            if (handleIfClientCommand(cKeyBind)) return;

            McIf.player().sendChatMessage("/" + cKeyBind);
        });
    }

    private static boolean handleIfClientCommand(String cKeyBind) {
        String[] parts = cKeyBind.split(" ");
        String command = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, parts.length - 1);

        // This map contains aliases aswell as full command names
        Map<String, ICommand> clientCommands = getClientCommands();

        // Run as client command if possible
        if (clientCommands.containsKey(command))
        {
            try {
                clientCommands.get(command).execute(FMLCommonHandler.instance().getMinecraftServerInstance(), McIf.player(), args);
            } catch (CommandException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    public static Map<String, ICommand> getClientCommands() {
        return ClientCommandHandler.instance.getCommands();
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
