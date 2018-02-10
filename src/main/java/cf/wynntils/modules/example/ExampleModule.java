package cf.wynntils.modules.example;


import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import org.lwjgl.input.Keyboard;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@ModuleInfo(name = "ExampleModule")
public class ExampleModule extends Module {

    /**
     * When modules gets enabled
     *
     * Here you can call Module#registerEvents to register events (¿okay?)
     * Here you can call Module#registerHudOverlay to register a HudOverlay
     *  -> HudOverlay will request getMinecraft and the default x, y
     * Here you can call Module#registerKeyBinding to register a key
     */
    public void onEnable() {
        getLogger().warn("MODULE STARTED");
        getLogger().warn("MODULE STARTED");
        getLogger().warn("MODULE STARTED");
        getLogger().warn("MODULE STARTED");
        getLogger().warn("MODULE STARTED");

        registerEvents(new ExampleListener());
        registerHudOverlay(new ExampleHudOverlay("Example", 100, 50));
        registerKeyBinding("Test", Keyboard.KEY_G, "Wynntils", true, () -> {
            getLogger().warn("KEY PRESSED");
        });
    }

    /**
     * When module gets disabled
     *
     * WIP there is no call for this method
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
