package com.wynndevs.modules.wynnicmap;

import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.core.input.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class WynnicMap {
    public static ScaledResolution sr(){return new ScaledResolution(Minecraft.getMinecraft());}
    public static final File WYNNICMAP_STORAGE_ROOT = new File(Reference.MOD_STORAGE_ROOT.getAbsolutePath() + "/wynnicmap"); /* Root folder for all WynnicMap related things */
    private static boolean moduleWorking = false; public static boolean getModuleWorking() {return moduleWorking;}                     /* Indicates if the module is working */
    public static Logger logger = LogManager.getLogger(Reference.MOD_ID + "-wynnicmap");

    public static GuiMap minimap = new GuiMap(sr().getScaledWidth()-110,10,100,100,0,0,0,false,true);

    public static final boolean DONT_UPDATE = false; // THIS IS SUPPOSED TO BE FALSE BEFORE EACH COMMIT! IF ITS TRUE, PLEASE TELL ME ABOUT IT!!! -SHCM
    private static boolean checkedForUpdates = false;

    /**
     * Tries to load the WynnicMap module into the game or reloads if loaded
     * @return Succession of loading
     */
    public static void loadModule() {
        if (moduleWorking) unloadModule();
        try {
            MapHandler.LoadMap();
        } catch (Exception error) {
        } finally {
            moduleWorking = true;
        }
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
     * @param event eventArgs
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void renderMinimap(RenderGameOverlayEvent.Post event) {
        if(event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if(!moduleWorking || !Reference.onServer() || event.isCanceled()) return;

        try{
            minimap.x = sr().getScaledWidth()-110;
            minimap.RecreateDrawingCut();

            minimap.centerX = Minecraft.getMinecraft().player.posX;
            minimap.centerY = Minecraft.getMinecraft().player.posZ;

            minimap.drawMap();
        }catch(Exception ignored) {
            ignored.printStackTrace();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void tick(TickEvent.ClientTickEvent event) {
        if(MapUpdater.updatingState == 2) {
            loadModule();
            MapUpdater.updatingState = 0;
            return;
        }
        if(KeyBindings.WYNNICMAP_ZOOM_IN.isKeyDown()) {
            minimap.zoom++;
        }
        else if(KeyBindings.WYNNICMAP_ZOOM_OUT.isKeyDown()) {
            minimap.zoom--;
        }
        else if(KeyBindings.WYNNICMAP_MENU.isPressed()) {
            if(!minimap.visible) {
                minimap.visible = true;
                minimap.circular = false;
            }
            else if(minimap.circular) {
                minimap.visible = false;
            }
            else {
                minimap.circular = true;
            }
        }


        if(ModCore.mc().world != null && ModCore.mc().player != null && Reference.onServer())
            if((MapHandler.getMap() == null) || !MapHandler.getMap().isInside(ModCore.mc().player.posX,ModCore.mc().player.posZ))
                MapHandler.ChangeMap(ModCore.mc().player.posX,ModCore.mc().player.posZ);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void changeWorld(EntityJoinWorldEvent e) {
        if(!Reference.onServer() || e.getEntity() != Minecraft.getMinecraft().player) return;
        if (!DONT_UPDATE && !checkedForUpdates) {
            MapUpdater.TryUpdate();
            checkedForUpdates = true;
        }
    }
}
