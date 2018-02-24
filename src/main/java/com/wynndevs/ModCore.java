package com.wynndevs;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;


public class ModCore {
    public static Logger logger;
	
    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

}
