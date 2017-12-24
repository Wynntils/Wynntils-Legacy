package com.wynndevs.core;

import com.wynndevs.ModCore;

public class Reference {

    public static final String MOD_ID = "wynnexp";
    public static final String NAME = "WynncraftExpansion";
    public static final String VERSION = "1.0";
    public static boolean onServer() { return !ModCore.mc().isSingleplayer() && ModCore.mc().getCurrentServerData().serverIP.contains("wynncraft"); }

}