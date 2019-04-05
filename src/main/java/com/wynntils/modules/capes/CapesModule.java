/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.capes;

import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.capes.events.EntityEvents;
import com.wynntils.modules.capes.events.ServerEvents;
import com.wynntils.modules.capes.layers.LayerCape;
import com.wynntils.modules.capes.layers.LayerElytra;
import com.wynntils.modules.capes.layers.LayerFoxEars;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;

@ModuleInfo(name = "capes", displayName = "wynntils.modules.capes.display_name")
public class CapesModule extends Module {

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
