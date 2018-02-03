package cf.wynntils.modules.core.overlays;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.HudOverlay;
import cf.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class UpdateOverlay extends HudOverlay {

    int size = 50;
    long timeout = System.currentTimeMillis();
    boolean loaded = false;

    public UpdateOverlay(Minecraft mc, int x, int y) {
        super(mc, x, y);

        setConfigValue("manageable", false);
    }

    @Override
    public void postRender(RenderGameOverlayEvent.Post e) {
        if(e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if(!WebManager.getUpdate().modHasUpdate()) {
            return;
        }

        drawRect(0, 0 - size, 203, 43 - size, -2500134);
        drawRect(0, 0 - size, 200, 40 - size, -10066329);
        drawString("§a§lWynntils", 5, 3 - size, -1);
        drawString("Update §av" + WebManager.getUpdate().getModLatestUpdate() + "§f is available!", 8, 17 - size, -1);
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
