package com.wynndevs.modules.wynnicmap;

import com.wynndevs.core.Reference;
import com.wynndevs.core.enums.ModuleResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.io.IOException;

public class WynnicMap {
    public static final File WYNNICMAP_STORAGE_ROOT = new File(Reference.MOD_STORAGE_ROOT.getAbsolutePath() + "/wynnicmap"); /* Root folder for all WynnicMap related things */
    private static boolean moduleWorking = false; public static boolean getModuleWorking() {return moduleWorking;}                     /* Indicates if the module is working */

    public static ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

    /**
     * Tries to load the WynnicMap module into the game or reloads if loaded
     * @return Succession of loading
     */
    public static ModuleResult loadModule() {
        if(moduleWorking) unloadModule();
        try {
            MapHandler.LoadMap();
        } catch (IOException error) {
            error.printStackTrace();
            return ModuleResult.ERROR;
        }
        moduleWorking = true;
        return ModuleResult.SUCCESS;
    }

    /**
     * Unloads the WynnicMap module from the game freeing up resources
     */
    public static void unloadModule() {
        MapHandler.UnloadMap();
        moduleWorking = false;
    }

    static GuiMap minimap = new GuiMap(sr.getScaledWidth()-200,sr.getScaledHeight()-10,190,190,0,0,0);

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderminimap(RenderGameOverlayEvent.Post e) {
        if(!moduleWorking || !Reference.onWorld()) return;
        if (e.isCanceled() || e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        try{
            sr = new ScaledResolution(Minecraft.getMinecraft());
            new GuiMap(sr.getScaledWidth()-110,10,100,100,Minecraft.getMinecraft().player.getPosition().getX(),Minecraft.getMinecraft().player.getPosition().getZ(),0).drawMap();
        }catch(Exception ignored) {
            ignored.getStackTrace();
        }
    }
}
