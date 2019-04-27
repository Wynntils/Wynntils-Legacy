/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.cosmetics;

import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.cosmetics.events.EntityEvents;
import com.wynntils.modules.cosmetics.events.ServerEvents;
import com.wynntils.modules.cosmetics.layers.LayerCape;
import com.wynntils.modules.cosmetics.layers.LayerElytra;
import com.wynntils.modules.cosmetics.layers.LayerFoxEars;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;

@ModuleInfo(name = "capes", displayName = "Capes")
public class CosmeticsModule extends Module {

    public void onEnable() {
        registerEvents(new EntityEvents());
        registerEvents(new ServerEvents());
    }

    public void postEnable() {
        Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, true);
        for (RenderPlayer render : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            render.addLayer(new LayerCape(render));
            render.addLayer(new LayerElytra(render));
            render.addLayer(new LayerFoxEars(render));
        }
    }

}
