package com.wynnexpansion;

import com.wynnexpansion.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION)
public class WynnExpansion {


    /**
     * The proxy for client and server.
     */
    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // some example code
        System.out.println("DIRT BLOCK >> " + Blocks.DIRT.getUnlocalizedName());
    }
}
