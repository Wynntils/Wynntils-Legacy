package com.wynndevs;

import com.wynndevs.core.Reference;
import com.wynndevs.core.enums.ModuleResult;
import com.wynndevs.core.events.ClientEvents;
import com.wynndevs.core.input.KeyBindings;
import com.wynndevs.expansion.WynnExpansion;
import com.wynndevs.map.WynnMap;
import com.wynndevs.market.WynnMarket;
import com.wynndevs.richpresence.WynnRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;


@Mod(name = Reference.NAME, modid = Reference.MOD_ID, version = Reference.VERSION, acceptedMinecraftVersions = "[1.11,1.12.2]", clientSideOnly = true)
public class ModCore {

    public static ArrayList<String> invalidModules = new ArrayList<>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());

        KeyBindings.init();

        if(WynnRichPresence.initModule(e) == ModuleResult.ERROR) {
            invalidModules.add("RichPresence");
        }
        if(WynnExpansion.initModule(e) == ModuleResult.ERROR) {
            invalidModules.add("Expansion");
        }
        if(WynnMap.initModule(e) == ModuleResult.ERROR) {
            invalidModules.add("Map");
        }
        if(WynnMarket.initModule(e) == ModuleResult.ERROR) {
            invalidModules.add("Market");
        }
    }

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

}
