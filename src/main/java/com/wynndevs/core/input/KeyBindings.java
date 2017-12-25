package com.wynndevs.core.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBindings {

    public static KeyBinding MARKET_GUI;
	public static KeyBinding OPEN_QUEST_BOOK;
	public static KeyBinding OPEN_WYNN_SOUND;
	public static KeyBinding SPELL_1;
	public static KeyBinding SPELL_2;
	public static KeyBinding SPELL_3;
	public static KeyBinding SPELL_4;
	public static KeyBinding TOGGLE_LOCK;
	public static KeyBinding OPEN_PLAYER_MENU;
	public static KeyBinding OPEN_ITEM_GUIDE;
	public static KeyBinding OPEN_CONFIG_MENU;

    public static void init() {
        MARKET_GUI = new KeyBinding("Open Market", Keyboard.KEY_M, "Wynncraft Expansion");
		OPEN_CONFIG_MENU = new KeyBinding("Open Config", Keyboard.KEY_G, "Wynncraft Expansion");

        ClientRegistry.registerKeyBinding(MARKET_GUI);
		ClientRegistry.registerKeyBinding(OPEN_QUEST_BOOK = new KeyBinding("Open Quest Book", Keyboard.KEY_R, "Wynncraft Expansion"));
		ClientRegistry.registerKeyBinding(OPEN_WYNN_SOUND = new KeyBinding("Open Wynn Sound GUI", Keyboard.KEY_H, "Wynncraft Expansion"));
		ClientRegistry.registerKeyBinding(SPELL_1 = new KeyBinding("Cast Spell 1", Keyboard.KEY_Z, "Wynncraft Expansion"));
		ClientRegistry.registerKeyBinding(SPELL_2 = new KeyBinding("Cast Spell 2", Keyboard.KEY_X, "Wynncraft Expansion"));
		ClientRegistry.registerKeyBinding(SPELL_3 = new KeyBinding("Cast Spell 3", Keyboard.KEY_C, "Wynncraft Expansion"));
		ClientRegistry.registerKeyBinding(SPELL_4 = new KeyBinding("Cast Spell 4", Keyboard.KEY_V, "Wynncraft Expansion"));
		ClientRegistry.registerKeyBinding(TOGGLE_LOCK = new KeyBinding("Toggle Item Lock", Keyboard.KEY_F, "Wynncraft Expansion"));
		ClientRegistry.registerKeyBinding(OPEN_PLAYER_MENU = new KeyBinding("Open Player Menu", Keyboard.KEY_G, "Wynncraft Expansion"));
		ClientRegistry.registerKeyBinding(OPEN_ITEM_GUIDE = new KeyBinding("Open Item Guide", Keyboard.KEY_P, "Wynncraft Expansion"));
    }

}
