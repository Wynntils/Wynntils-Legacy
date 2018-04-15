package cf.wynntils.modules.utilities;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.utilities.configs.UtilitiesConfig;
import cf.wynntils.modules.utilities.events.ClientEvents;
import cf.wynntils.modules.utilities.events.ServerEvents;
import cf.wynntils.modules.utilities.managers.KeyManager;
import cf.wynntils.modules.utilities.overlays.OverlayEvents;
import cf.wynntils.modules.utilities.overlays.hud.*;
import cf.wynntils.modules.utilities.overlays.uis.DebugUI;
import org.lwjgl.input.Keyboard;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@ModuleInfo(name = "utilities", displayName = "Utils")
public class UtilitiesModule extends Module {

    private static UtilitiesModule module;


    public void onEnable() {
        module = this;

        KeyManager.registerKeys();

        registerEvents(new ServerEvents());
        registerEvents(new OverlayEvents());
        registerEvents(new ClientEvents());

        registerOverlay(new HealthBarOverlay(), Priority.NORMAL);
        registerOverlay(new ManaBarOverlay(), Priority.NORMAL);
        registerOverlay(new ExpBarOverlay(), Priority.NORMAL);
        registerOverlay(new LevelingOverlay(), Priority.LOW);

        registerOverlay(new GammaOverlay(), Priority.NORMAL);
        registerOverlay(new LobbyCleanerOverlay(), Priority.LOW);

        registerOverlay(new DebugOverlay(),Priority.NORMAL);

        registerSettings(UtilitiesConfig.class);
        registerSettings(UtilitiesConfig.Data.class);
        registerSettings(UtilitiesConfig.Items.class);
        registerSettings(UtilitiesConfig.Chat.class);
        registerSettings(UtilitiesConfig.Debug.class);

        registerKeyBinding("The holy key of debugging", Keyboard.KEY_K, "DEBUG", true, () -> {
            new DebugUI().show();
        });
    }

    public static UtilitiesModule getModule() {
        return module;
    }
}
