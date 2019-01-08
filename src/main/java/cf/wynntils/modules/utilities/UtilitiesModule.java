package cf.wynntils.modules.utilities;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.utilities.configs.OverlayConfig;
import cf.wynntils.modules.utilities.configs.UtilitiesConfig;
import cf.wynntils.modules.utilities.events.ClientEvents;
import cf.wynntils.modules.utilities.events.ServerEvents;
import cf.wynntils.modules.utilities.managers.KeyManager;
import cf.wynntils.modules.utilities.overlays.OverlayEvents;
import cf.wynntils.modules.utilities.overlays.hud.*;
import cf.wynntils.modules.utilities.overlays.inventories.ItemIdentificationOverlay;
import cf.wynntils.modules.utilities.overlays.inventories.ItemLockOverlay;
import cf.wynntils.modules.utilities.overlays.inventories.RarityColorOverlay;
import cf.wynntils.modules.utilities.overlays.inventories.SkillPointOverlay;

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

        //Inventory Overlays
        registerEvents(new ItemIdentificationOverlay());
        registerEvents(new RarityColorOverlay());
        registerEvents(new SkillPointOverlay());
        registerEvents(new ItemLockOverlay());

        registerOverlay(new WarTimerOverlay(), Priority.LOWEST);
        registerOverlay(new ActionBarOverlay(), Priority.LOWEST);
        registerOverlay(new HealthBarOverlay(), Priority.NORMAL);
        registerOverlay(new ManaBarOverlay(), Priority.NORMAL);
        registerOverlay(new ExpBarOverlay(), Priority.NORMAL);
        registerOverlay(new LevelingOverlay(), Priority.LOW);
        registerOverlay(new BubblesOverlay(), Priority.HIGHEST);
        registerOverlay(new DrowningVignetteOverlay(), Priority.HIGHEST);
        registerOverlay(new GameUpdateOverlay(), Priority.LOW);

        registerOverlay(new GammaOverlay(), Priority.NORMAL);
        registerOverlay(new LobbyCleanerOverlay(), Priority.LOW);

        registerSettings(UtilitiesConfig.class);
        registerSettings(UtilitiesConfig.Data.class);
        registerSettings(UtilitiesConfig.Items.class);
        registerSettings(UtilitiesConfig.Debug.class);
        registerSettings(UtilitiesConfig.Wars.class);

        registerSettings(OverlayConfig.class);
        registerSettings(OverlayConfig.Health.class);
        registerSettings(OverlayConfig.Leveling.class);
        registerSettings(OverlayConfig.Exp.class);
        registerSettings(OverlayConfig.Mana.class);
        registerSettings(OverlayConfig.WarTimer.class);
        registerSettings(OverlayConfig.Bubbles.class);
        registerSettings(OverlayConfig.GameUpdate.class);
        registerSettings(OverlayConfig.GameUpdate.GameUpdateEXPMessages.class);
        registerSettings(OverlayConfig.GameUpdate.RedirectSystemMessages.class);
        registerSettings(OverlayConfig.GameUpdate.TerritoryChangeMessages.class);
    }

    public static UtilitiesModule getModule() {
        return module;
    }
}
