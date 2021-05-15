/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.framework.enums.wynntils;

import com.wynntils.McIf;
import com.wynntils.core.framework.instances.GuiMovementScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;

public enum WynntilsConflictContext implements IKeyConflictContext {

    /**
     * Context for keybindings related to player movement. Allows for movement inputs to register while in certain
     * non-movement-obstructing GUIs.
     */
    ALLOW_MOVEMENTS {
        @Override
        public boolean isActive() {
            return McIf.mc().currentScreen == null || McIf.mc().currentScreen instanceof GuiMovementScreen;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return this == other || other == KeyConflictContext.IN_GAME;
        }
    },

    /**
     * Context for keybindings that are always active, in some sense, but that are allowed to conflict with other
     * keybindings. For example, the "item level overlay" keybinding is ambient, since it functions independently of
     * any other context, and therefore is okay to bind to CTRL, which would normally conflict with sprint.
     */
    AMBIENT {
        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return this == other;
        }
    }

}
