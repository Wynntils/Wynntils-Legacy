package com.wynndevs.modules.expansion.customhud;

import com.wynndevs.modules.richpresence.guis.WRPGui;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HudOverlay extends WRPGui {

    public HudOverlay(Minecraft mc) {
        super(mc);
    }

    @SubscribeEvent(priority= EventPriority.NORMAL)
    public void onRenderExperienceBar(RenderGameOverlayEvent.Pre e) {
        if(e.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            e.setCanceled(true);
        }
    }


}
