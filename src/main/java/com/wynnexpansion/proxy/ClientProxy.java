package com.wynnexpansion.proxy;

import com.wynnexpansion.input.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ClientProxy implements CommonProxy {


    @Override
    public void registerEvents() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void init() {
        KeyBindings.init();
    }

    @Override
    public void preInit() {

    }

    @Override
    public void postInit() {

    }
}
