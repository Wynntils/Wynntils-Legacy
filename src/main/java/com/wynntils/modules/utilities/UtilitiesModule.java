/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities;

import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.events.ClientEvents;
import com.wynntils.modules.utilities.events.ServerEvents;
import com.wynntils.modules.utilities.managers.KeyManager;
import com.wynntils.modules.utilities.overlays.OverlayEvents;
import com.wynntils.modules.utilities.overlays.hud.*;
import com.wynntils.modules.utilities.overlays.inventories.*;

@ModuleInfo(name = "utilities", displayName = "Utilities")
public class UtilitiesModule extends Module {

    private static UtilitiesModule module;
    private GameUpdateOverlay gameUpdateOverlay;

    public void onEnable() {
        module = this;

        KeyManager.registerKeys();

        registerEvents(new ServerEvents());
        registerEvents(new OverlayEvents());
        registerEvents(new ClientEvents());

        // Inventory Overlays
        registerEvents(new ItemIdentificationOverlay());
        registerEvents(new RarityColorOverlay());
        registerEvents(new SkillPointOverlay());
        registerEvents(new ItemLockOverlay());
        registerEvents(new MenuButtonsOverlay());
        registerEvents(new IngredientFilterOverlay());
        registerEvents(new EmeraldCountOverlay());
        registerEvents(new LoreChangerOverlay());

        // Real overlays
        registerOverlay(new WarTimerOverlay(), Priority.LOWEST);
        registerOverlay(new ActionBarOverlay(), Priority.LOWEST);
        registerOverlay(new HealthBarOverlay(), Priority.NORMAL);
        registerOverlay(new HotbarOverlay(), Priority.NORMAL);
        registerOverlay(new ManaBarOverlay(), Priority.NORMAL);
        registerOverlay(new ExpBarOverlay(), Priority.NORMAL);
        registerOverlay(new LevelingOverlay(), Priority.LOW);
        registerOverlay(new BubblesOverlay(), Priority.HIGHEST);
        registerOverlay(new DrowningVignetteOverlay(), Priority.HIGHEST);
        registerOverlay(gameUpdateOverlay = new GameUpdateOverlay(), Priority.NORMAL);
        registerOverlay(new TerritoryFeedOverlay(), Priority.LOW);
        registerOverlay(new ToastOverlay(), Priority.LOW);
        registerOverlay(new LowHealthVignetteOverlay(), Priority.LOW);
        registerOverlay(new InfoOverlay._1(), Priority.NORMAL);
        registerOverlay(new InfoOverlay._2(), Priority.NORMAL);
        registerOverlay(new InfoOverlay._3(), Priority.NORMAL);
        registerOverlay(new InfoOverlay._4(), Priority.NORMAL);
        registerOverlay(new PlayerInfoOverlay(), Priority.HIGHEST);


        registerOverlay(new GammaOverlay(), Priority.NORMAL);
        registerOverlay(new LobbyCleanerOverlay(), Priority.LOW);
        registerOverlay(new StopWatchOverlay(), Priority.LOW);

        registerSettings(UtilitiesConfig.class);
        registerSettings(UtilitiesConfig.Data.class);
        registerSettings(UtilitiesConfig.Items.class);
        registerSettings(UtilitiesConfig.Wars.class);
        registerSettings(UtilitiesConfig.Market.class);

        registerSettings(OverlayConfig.class);
        registerSettings(OverlayConfig.Health.class);
        registerSettings(OverlayConfig.Leveling.class);
        registerSettings(OverlayConfig.Exp.class);
        registerSettings(OverlayConfig.Mana.class);
        registerSettings(OverlayConfig.Hotbar.class);
        registerSettings(OverlayConfig.ToastsSettings.class);
        registerSettings(OverlayConfig.WarTimer.class);
        registerSettings(OverlayConfig.Bubbles.class);
        registerSettings(OverlayConfig.GameUpdate.class);
        registerSettings(OverlayConfig.TerritoryFeed.class);
        registerSettings(OverlayConfig.InfoOverlays.class);
        registerSettings(OverlayConfig.PlayerInfo.class);
        registerSettings(OverlayConfig.GameUpdate.GameUpdateEXPMessages.class);
        registerSettings(OverlayConfig.GameUpdate.GameUpdateInventoryMessages.class);
        registerSettings(OverlayConfig.GameUpdate.RedirectSystemMessages.class);
        registerSettings(OverlayConfig.GameUpdate.TerritoryChangeMessages.class);
    }

    public static UtilitiesModule getModule() {
        return module;
    }

    public GameUpdateOverlay getGameUpdateOverlay() {
        return gameUpdateOverlay;
    }

}
