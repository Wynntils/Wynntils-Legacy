/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.core.framework.instances;

import com.wynntils.McIf;
import com.wynntils.core.framework.enums.wynntils.WynntilsConflictContext;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public abstract class GuiMovementScreen extends GuiTickableScreen {

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

                for (KeyBinding key : McIf.mc().gameSettings.keyBindings) {
                    if (key.getKeyCode() != Keyboard.getEventKey() || key.getKeyConflictContext() != WynntilsConflictContext.ALLOW_MOVEMENTS) continue;

                    KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());
                    KeyBinding.onTick(Keyboard.getEventKey());
                    return;
                }

                this.handleKeyboardInput();
            }
        }
    }

    @Override
    public void onTick() {

    }

}
