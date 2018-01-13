package com.wynndevs.modules.wynnicmap;

import com.wynndevs.core.Reference;
import com.wynndevs.core.enums.ModuleResult;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.IOException;

public class WynnicMap {
    public static final File WYNNICMAP_STORAGE_ROOT = new File(Reference.MOD_STORAGE_ROOT.getAbsolutePath() + "/wynnicmap");
    public static boolean moduleWorking = false;


    public static ModuleResult initModule(FMLPreInitializationEvent e) {
        try {
            MapHandler.LoadMap();
        } catch (IOException error) {
            error.printStackTrace();
            return ModuleResult.ERROR;
        }
        moduleWorking = true;
        return ModuleResult.SUCCESS;
    }
}
