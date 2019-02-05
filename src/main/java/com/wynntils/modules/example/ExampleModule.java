/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.example;

import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import org.lwjgl.input.Keyboard;

/** EXAMPLE CLASS
 * ExampleModule shows some of the things that are needed to make
 * a Module Class.
 * Modules are the parts that make up the whole Wynntils mod,
 * They should be able to be turned off and on dynamically,
 * register their event listeners, overlays and keybindings,
 * and (TODO) house the module's user prefrences
 */
@ModuleInfo(name = "example_snake-cased_name", displayName = "Display Name For The Module")
public class ExampleModule extends Module {

    /**
     * When the module gets enabled, This method will be called
     *
     * From here, call Module#registerEvents to register events
     * From here, call Module#registerOverlay to register a overlays
     * From here, call Module#registerKeyBinding to register a key
     */
    public void onEnable() {
        getLogger().warn("MODULE STARTED");///You can use Module#getLogger to spit things to the console

        registerEvents(new ExampleListener());//Registering ExampleListener as an event handler

        registerOverlay(new ExampleOverlay(), Priority.LOW);//Registering ExampleOverlay on the LOW priority Overlays collection

        registerKeyBinding("Test", Keyboard.KEY_G, "Wynntils", true, () -> {
            getLogger().warn("KEY PRESSED");//Registering the 'G' key to a test example key and make it spit "KEY PRESSED" to console
        });
    }

    /**
     * When the module gets disabled, This method will be called
     *
     * TODO, WIP there is no call for this method
     */
    public void onDisable() {

    }

    /**
     * Here you can toggle your module, you can use options to check if this is active
     * Something like MyOptions#exampleModule.isActive() <- if that gets toggled the module will be disabled
     *
     * @return if the current module should be handled
     */
    public boolean isActive() {
        return true;
    }

}
