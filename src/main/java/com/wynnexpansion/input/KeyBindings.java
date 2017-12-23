package com.wynnexpansion.input;

import com.wynnexpansion.Reference;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBindings {

    public static KeyBinding test;


    /**
     * Initializes the list of key bindings used by the mod
     */
    public static void init() {
        test = new KeyBinding("key.test", Keyboard.KEY_SEMICOLON, "key.categories." + Reference.MOD_ID);


        ClientRegistry.registerKeyBinding(test);
    }

}
