package cf.wynntils.modules.richpresence.overlays;

import cf.wynntils.core.framework.instances.HudOverlay;
import cf.wynntils.core.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class LocationOverlay extends HudOverlay {

    public static String location = "Waiting";
    public static String last_loc = "Waiting";

    int size = 50;
    long timeout = System.currentTimeMillis();

    boolean showing = false;
    boolean animation = false;

    public LocationOverlay(Minecraft mc, int x, int y) {
        super(mc, x, y);
    }

    @Override
    public void postRender(RenderGameOverlayEvent.Post e) {
        if(last_loc.equals(location) && !showing) {
            return;
        }

        if(!showing) {
            last_loc = Utils.removeAfterChar(location, 15);
        }

        showing = true;

        drawRect(x, y - size, 143, 43 - size, -2500134);
        drawRect(x, y - size, 140, 40 - size, -10066329);

        drawString("§a§lYou are now entering", x + 5, y + 5 - size, -1);
        drawString("§e" + last_loc, x + 7, y + 20 - size, 1.5f, 13782543);

        if(size > 0 && !animation) {
            size-=1;
            timeout = System.currentTimeMillis();
        }else{
            if(System.currentTimeMillis() - timeout >= 3000) {
                animation = true;
            }
            if(animation) {
                if(size < 50) {
                    size+=1;
                    timeout = System.currentTimeMillis();
                }
                if(size >= 50) {
                    showing = false;
                    animation = false;
                }
            }
        }
    }

    @Override
    public boolean isActive() {
        //add configuration handler
        return true;
    }
}
