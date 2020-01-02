/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.framework.instances;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyHolder {

    private Runnable onAction;
    private boolean press;
    private KeyBinding keyBinding;

    public KeyHolder(String name, int key, String tab, boolean press, Runnable onAction) {
        this.onAction = onAction;
        this.press = press;

        keyBinding = new KeyBinding(name, key, tab);
        ClientRegistry.registerKeyBinding(keyBinding);
    }

    public boolean isPress() {
        return press;
    }

    public Runnable getOnAction() {
        return onAction;
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

}
