package com.wynndevs.core.gui;

import com.wynndevs.core.Reference;
import com.wynndevs.modules.richpresence.guis.WRPGui;
import com.wynndevs.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UpdateOverlay extends WRPGui {

    int size = 50;
    long timeout = System.currentTimeMillis();
    boolean loaded = false;

    public UpdateOverlay(Minecraft mc) {
        super(mc);
    }

    @SubscribeEvent(priority= EventPriority.NORMAL)
    public void onRenderExperienceBar(RenderGameOverlayEvent.Post e) {
        if(e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if(!WebManager.getUpdate().hasUpdate()) {
            return;
        }

        drawRect(0, 0 - size, 203, 43 - size, -2500134);
        drawRect(0, 0 - size, 200, 40 - size, -10066329);
        drawString("§a§lWynutils", 5, 3 - size, -1);
        drawString("Update §av" + WebManager.getUpdate().getLatestUpdate() + "§f is available!", 8, 17 - size, -1);
        drawString("§7Currently using: v" + Reference.VERSION, 8, 27 - size, -1);
        if(size > 0 && !loaded) {
            size-=1;
            timeout = System.currentTimeMillis();
        }else{
            if(System.currentTimeMillis() - timeout >= 5000) {
                loaded = true;
            }
            if(loaded) {
                if(size < 50) {
                    size+=1;
                    timeout = System.currentTimeMillis();
                }
            }
        }
    }

}
