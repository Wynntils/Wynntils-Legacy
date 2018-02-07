package cf.wynntils.modules.capes;

import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.capes.events.EntityEvents;
import cf.wynntils.modules.capes.layers.LayerCape;
import cf.wynntils.modules.capes.managers.CapeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;

/**
 * Created by HeyZeer0 on 07/02/2018.
 * Copyright © HeyZeer0 - 2016
 */

@ModuleInfo(name = "Capes")
public class CapesModule extends Module {

    public void onEnable() {
        CapeManager.updateCapes();
        registerEvents(new EntityEvents());
    }

    public void postInit() {
        Minecraft.getMinecraft().gameSettings.setModelPartEnabled(EnumPlayerModelParts.CAPE, true);
        for (RenderPlayer render : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            render.addLayer(new LayerCape(render));
        }
    }

}
