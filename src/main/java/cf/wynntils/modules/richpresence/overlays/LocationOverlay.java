package cf.wynntils.modules.richpresence.overlays;

import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.utils.Utils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class LocationOverlay extends Overlay {

    public static String location = "Waiting";
    public static String last_loc = "Waiting";

    static int size = 50;
    static long timeout = System.currentTimeMillis();

    static boolean showing = false;
    static boolean animation = false;

    public LocationOverlay() {
        super("Update Overlay", 20, 20, false, 1.0f, 0.0f, 0, 0);
    }

    @Override
    public void render(RenderGameOverlayEvent.Post e) {
        if(last_loc.equals(location) && !showing) {
            return;
        }

        if(!showing) {
            last_loc = Utils.removeAfterChar(location, 15);
        }

        showing = true;

        drawRect(CommonColors.LIGHT_GRAY, 0, 0 - size, 143, 43 - size);
        drawRect(CommonColors.GRAY, 0, 0 - size, 140, 40 - size);

        drawString("You are now entering:",  5, 5, CommonColors.WHITE);
        scale(1.5f);
        drawString(last_loc, 7,  20 - size, CommonColors.YELLOW);

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

}
