package cf.wynntils.core.framework.instances;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
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

    public Runnable getOnAction() {
        return onAction;
    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

}
