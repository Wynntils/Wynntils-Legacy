/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.framework.instances;

import com.wynntils.core.framework.enums.wynntils.WynntilsConflictContext;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiMovementScreen extends GuiScreen {
    protected boolean allowMovement = true;

    @Override
    public void handleInput() throws IOException {
        if (!allowMovement) {
            super.handleInput();
            return;
        }
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                this.handleMouseInput();
            }
        }

        if (Keyboard.isCreated()) {
            while (Keyboard.next()) {

                for (KeyBinding key : mc.gameSettings.keyBindings) {
                    if (key.getKeyCode() != Keyboard.getEventKey() || key.getKeyConflictContext() != WynntilsConflictContext.ALLOW_MOVEMENTS) continue;

                    KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());
                    KeyBinding.onTick(Keyboard.getEventKey());
                    return;
                }

                this.handleKeyboardInput();
            }
        }
    }

}
