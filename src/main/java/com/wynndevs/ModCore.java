package com.wynndevs;

import com.wynndevs.core.Reference;
import com.wynndevs.core.events.ClientEvents;
import com.wynndevs.core.input.KeyBindings;
import com.wynndevs.wynnrp.WynnRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;


@Mod(name = Reference.NAME, modid = Reference.MOD_ID, version = Reference.VERSION, acceptedMinecraftVersions = "[1.11,1.12.2]")
public class ModCore {

    public static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(new ClientEvents());

        KeyBindings.init();
        WynnRichPresence.startRichPresence(e);
    }

}
