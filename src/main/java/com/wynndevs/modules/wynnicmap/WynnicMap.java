package com.wynndevs.modules.wynnicmap;

import com.wynndevs.core.Reference;
import com.wynndevs.core.enums.ModuleResult;
import com.wynndevs.core.input.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.io.IOException;

public class WynnicMap {
    public static final File WYNNICMAP_STORAGE_ROOT = new File(Reference.MOD_STORAGE_ROOT.getAbsolutePath() + "/wynnicmap"); /* Root folder for all WynnicMap related things */
    private static boolean moduleWorking = false; public static boolean getModuleWorking() {return moduleWorking;}                     /* Indicates if the module is working */

    public static int updatingState = -1;

    public static GuiMap minimap = new GuiMap(sr().getScaledWidth()-110,10,100,100,0,0,0);

    public static ScaledResolution sr(){return new ScaledResolution(Minecraft.getMinecraft());}



    /**
     * Tries to load the WynnicMap module into the game or reloads if loaded
     * @return Succession of loading
     */
    public static void loadModule() {
        if(moduleWorking) unloadModule();
        try {
            MapHandler.LoadMap();
        } catch (IOException error) {
            updatingState = 0;
            return;
        }
        finally {
            if(MapHandler.getMapVersion() < MapUpdater.LatestVersion())
                updatingState = 0;
        }
        moduleWorking = true;
    }

    /**
     * Unloads the WynnicMap module from the game freeing up resources
     */
    public static void unloadModule() {
        MapHandler.UnloadMap();
        moduleWorking = false;
    }

    /**
     * Overlay Event for minimap, Dont call this method please
     *
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void renderminimap(RenderGameOverlayEvent.Post event) {
        if(!moduleWorking || !Reference.onServer()) return;
        if (event.isCanceled() || event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        try{
            minimap.x = sr().getScaledWidth()-110;
            minimap.centerX = (float)Minecraft.getMinecraft().player.posX;
            minimap.centerY = (float)Minecraft.getMinecraft().player.posZ;

            minimap.drawMap();
        }catch(Exception ignored) {
            //ignored.getStackTrace();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void keyPress(TickEvent.ClientTickEvent event) {
        if(updatingState == 2) {
            loadModule();
            updatingState = -1;
            return;
        }
        if(KeyBindings.WYNNICMAP_ZOOM_IN.isKeyDown()) {
            minimap.zoom++;
        }
        else if(KeyBindings.WYNNICMAP_ZOOM_OUT.isKeyDown()) {
            minimap.zoom--;
        }
        else if(KeyBindings.WYNNICMAP_MENU.isPressed()) { //currently will toggle instead of opening menu
            minimap.visible = !minimap.visible;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void joinUpdate(EntityJoinWorldEvent e) {
        if (updatingState != 0  || !Reference.onServer() || e.getEntity() != Minecraft.getMinecraft().player || Reference.onWorld()) {return;}
        updatingState = 1;
        MapUpdater.TryUpdate();
    }
}
