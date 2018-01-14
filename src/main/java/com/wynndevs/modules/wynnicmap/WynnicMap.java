package com.wynndevs.modules.wynnicmap;

import com.wynndevs.core.Reference;
import com.wynndevs.core.enums.ModuleResult;

import java.io.File;
import java.io.IOException;

public class WynnicMap {
    public static final File WYNNICMAP_STORAGE_ROOT = new File(Reference.MOD_STORAGE_ROOT.getAbsolutePath() + "/wynnicmap"); /* Root folder for all WynnicMap related things */
    private static boolean moduleWorking = false; public static boolean getModuleWorking() {return moduleWorking;}                     /* Indicates if the module is working */

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
}
