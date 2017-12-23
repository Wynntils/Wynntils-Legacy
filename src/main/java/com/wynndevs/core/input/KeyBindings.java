package com.wynndevs.core.input;

import com.wynndevs.core.Reference;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBindings {

    public static KeyBinding MARKET_GUI;

    public static void init() {
        MARKET_GUI = new KeyBinding("Open Market", Keyboard.KEY_M, "key.categories." + Reference.MOD_ID);

        ClientRegistry.registerKeyBinding(MARKET_GUI);
    }

}
