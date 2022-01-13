/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.instances;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyHolder {

    private final Runnable onPress;
    private final KeyBinding keyBinding;
    private final boolean press;

    /**
     * @param name The key name, will be displayed at configurations
     * @param key The LWJGL key value {@see Keyboard}
     * @param tab The configuration tab that will be used (usually Wynntils)
     * @param conflictCtx The conflict context that the key belongs to
     * @param press If true pressing the key will only trigger onAction once, while false will continuously call it
     *              for the time the key is still down
     * @param onPress Will be executed when the key press is detected
     */
    public KeyHolder(String name, int key, String tab, IKeyConflictContext conflictCtx, boolean press, Runnable onPress) {
        this.onPress = onPress;
        this.press = press;

        keyBinding = conflictCtx != null ? new KeyBinding(name, conflictCtx, key, tab) : new KeyBinding(name, key, tab);
        ClientRegistry.registerKeyBinding(keyBinding);
    }

    public boolean isPress() {
        return press;
    }

    public Runnable getOnPress() {
        return onPress;
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    public String getName() {
        return keyBinding.getKeyDescription();
    }

    public String getTab() {
        return keyBinding.getKeyCategory();
    }

    public int getKey() {
        return keyBinding.getKeyCode();
    }

    public boolean isKeyDown() {
        return Keyboard.isKeyDown(getKey());
    }

}
