package com.wynntils.core.framework.enums;

import com.wynntils.core.framework.instances.GuiMovementScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;

public enum WynntilsConflictContext implements IKeyConflictContext {

    ALLOW_MOVEMENTS {
        @Override
        public boolean isActive() {
            return Minecraft.getMinecraft().currentScreen == null || Minecraft.getMinecraft().currentScreen instanceof GuiMovementScreen;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other) {
            return this == other || other == KeyConflictContext.IN_GAME;
        }
    }


}
