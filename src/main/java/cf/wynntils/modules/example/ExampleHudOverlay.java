package cf.wynntils.modules.example;

import cf.wynntils.core.framework.instances.HudOverlay;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ExampleHudOverlay extends HudOverlay {

    /**
     * Default constructor
     *
     * @param name Overlay Name
     * @param x Default X
     * @param y Default Y
     */
    public ExampleHudOverlay(String name, int x, int y) {
        super(name, x, y);

        addDefaultConfigValue("Example", true); //true is the default value

        loadConfig();
    }

    static int t = 0;

    /**
     * Post render event
     *
     * @param e event
     */
    @Override
    public void postRender(RenderGameOverlayEvent.Post e) {
        if(e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            return;
        }
/*        t++;


        String st = "rotation: " + (t%360 >100 ? "" : "0") + (t%360 >10 ? "" : "0") + t%360;
        int stWidth = getStringWidth(st);
        scale(1.1f);
        transformationOrigin(stWidth/2,5);
        rotate(t%360);
        drawRect(new CustomColor(0.1f,0.1f,0.1f),-2,-2,2+stWidth,2+fontRenderer.FONT_HEIGHT);
        drawString(st, CommonColors.WHITE,0,0);
        resetRotation();
        resetScale();
        drawRect(CommonColors.PURPLE,-1,-1,1,1);
        drawRect(CommonColors.MAGENTA,transformationOrigin().x-1,transformationOrigin().y-1,transformationOrigin().x+1,transformationOrigin().y+1);
        /*
        if(getConfigBoolean("Example")) {
            drawString("This is an example", x, y, -1);
        }else{
            drawString("This is a test", x, y, -1);
        }*/
    }

    /**
     * Pre render event
     *
     * @param e event
     */
    @Override
    public void preRender(RenderGameOverlayEvent.Pre e) {
        if(e.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            //e.setCanceled(true);
        }
    }

    /**
     * If the Hud Overlay is active
     *
     * @return if the overlay is active
     */
    @Override
    public boolean isActive() {
        return true;
    }
}
