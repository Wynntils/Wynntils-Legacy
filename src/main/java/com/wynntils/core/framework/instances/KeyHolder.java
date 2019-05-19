/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.instances;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class KeyHolder {

    String name, tab;
    int key;
    Runnable onAction;

    boolean press;

    KeyBinding keyBinding;

    public KeyHolder(String name, int key, String tab, boolean press, Runnable onAction) {
        this.name = name;
        this.key = key;
        this.tab = tab;
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

}
