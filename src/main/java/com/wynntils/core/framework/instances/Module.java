/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.core.framework.instances;

import com.wynntils.McIf;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.entities.interfaces.EntitySpawnCodition;
import com.wynntils.core.framework.enums.Priority;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.settings.instances.SettingsHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

public abstract class Module {

    private Logger logger;

    /**
     * Called when the module is enabled
     * this occurs at the FMLPreInitializationEvent startup sequence.
     * @see FMLPreInitializationEvent
     */
    public abstract void onEnable();

    /**
     * Called after all modules were enabled
     * this occurs at the FMLPostInitializationEvent startup sequence
     * @see FMLPostInitializationEvent
     */
    public void postEnable() {

    }

    /**
     * Called when the module is disabled
     * NOT CALLED ANYWHERE AND NOT IMPLEMENTED YET
     */
    public void onDisable() {

    }

    /**
     * @return if the module is active or not, if not, some things will not be triggered.
     */
    public boolean isActive() {
        return true;
    }

    /**
     * Registers a new listener class to the Wynntils Event Bus linked to the module.
     * All event methods should have the SubscribeEvent annotation in order to receive events
     * @see net.minecraftforge.fml.common.eventhandler.SubscribeEvent
     *
     * @param listenerClass the class instance
     */
    public void registerEvents(Listener listenerClass) {
        FrameworkManager.registerEvents(this, listenerClass);
    }

    /**
     * Registers a new FakeEntity natural spawn condition linked to the module.
     * @see com.wynntils.core.framework.entities.instances.FakeEntity
     * @see EntitySpawnCodition
     *
     * @param spawnCondition the spawn condition instance
     */
    public void registerSpawnCondition(EntitySpawnCodition spawnCondition) {
        FrameworkManager.registerSpawnCondition(this, spawnCondition);
    }

    /**
     * Registers a new Settings class linked to the module
     * without registration the class will not be loaded.
     * @see SettingsHolder
     * @see com.wynntils.core.framework.settings.annotations.SettingsInfo
     * @see com.wynntils.core.framework.settings.annotations.Setting
     *
     * @param settingsClass the class
     */
    public void registerSettings(Class<? extends SettingsHolder> settingsClass) {
        FrameworkManager.registerSettings(this, settingsClass);
    }

    /**
     * Registers a new HUD Overlay linked to the module
     * without registration it will not be displayed
     * @see Overlay
     *
     * @param overlay The overlay instance
     * @param priority The render priority
     */
    public void registerOverlay(Overlay overlay, Priority priority) {
        FrameworkManager.registerOverlay(this, overlay, priority);
    }

    /**
     * Registers a new Keybind linked to the module
     *
     * @param name The key name, will be displayed at configurations
     * @param key The LWJGL key value {@see Keyboard}
     * @param tab The configuration tab that will be used (usually Wynntils)
     * @param conflictCtx The conflict context that the key belongs to
     * @param press If true pressing the key will only trigger onAction once, while false will continuously call it
     *              for the time the key is still down
     * @param onPress Will be executed when the key press is detected
     */
    public KeyHolder registerKeyBinding(String name, int key, String tab, IKeyConflictContext conflictCtx, boolean press, Runnable onPress) {
        return FrameworkManager.registerKeyBinding(this, new KeyHolder(name, key, tab, conflictCtx, press, onPress));
    }

    /**
     * Registers a new Keybind linked to the module, with the default universal conflict context
     *
     * @param name The key name, will be displayed at configurations
     * @param key The LWJGL key value {@see Keyboard}
     * @param tab The configuration tab that will be used (usually Wynntils)
     * @param press If true pressing the key will only trigger onAction once, while false will continuously call it
     *              for the time the key is still down
     * @param onPress Will be executed when the key press is detected
     */
    public KeyHolder registerKeyBinding(String name, int key, String tab, boolean press, Runnable onPress) {
        return registerKeyBinding(name, key, tab, null, press, onPress);
    }

    /**
     * Registers a new Client Command NOT linked to the module
     * @see ICommand
     *
     * @param command the ClientCommand instance
     */
    public void registerCommand(ICommand command) {
        ClientCommandHandler.instance.registerCommand(command);
    }

    public Minecraft getMinecraft() {
        return McIf.mc();
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

}
