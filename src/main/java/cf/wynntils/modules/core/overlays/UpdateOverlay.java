package cf.wynntils.modules.core.overlays;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.webapi.WebManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class UpdateOverlay extends Overlay {

    int size = 50;
    long timeout = System.currentTimeMillis();
    boolean loaded = false;

    public UpdateOverlay(String name, int x, int y) {
        super(name, x, y);
    }

    @Override
    public void postRender(RenderGameOverlayEvent.Post e) {
        if(e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if(!WebManager.getUpdate().modHasUpdate()) {
            return;
        }

        drawRect(CommonColors.LIGHT_GRAY,0, 0 - size, 203, 43 - size);
        drawRect(CommonColors.GRAY,0, 0 - size, 200, 40 - size);

        drawString("Wynntils",5, 3 - size,CommonColors.LIGHT_GREEN);
        drawString("Update v" + WebManager.getUpdate().getModLatestUpdate() + " is available!", 8, 17 - size,CommonColors.WHITE);
        drawString("Currently using: v" + Reference.VERSION, 8, 27 - size, CommonColors.WHITE);

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
