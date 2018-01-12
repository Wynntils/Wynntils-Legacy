package com.wynndevs.modules.richpresence.guis.overlay;

import com.wynndevs.ConfigValues;
import com.wynndevs.core.Reference;
import com.wynndevs.modules.richpresence.guis.WRPGui;
import com.wynndevs.modules.richpresence.utils.RichUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by HeyZeer0 on 12/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class LocationGUI extends WRPGui {

    public static String location = "Waiting";
    public static String last_loc = "Waiting";

    int size = 50;
    long timeout = System.currentTimeMillis();

    boolean showing = false;
    boolean animation = false;

    public LocationGUI(Minecraft mc) {
        super(mc);
    }

    /**
     * Renders the entering notifier
     *
     * @param e
     *        Origem event
     */
    @SubscribeEvent(priority= EventPriority.NORMAL)
    public void onRenderExperienceBar(RenderGameOverlayEvent.Post e) {
        if(e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        if(!ConfigValues.wynnRichPresence.enteringNotifier || !Reference.onServer()) {
            return;
        }

        if(last_loc.equals(location) && !showing) {
            return;
        }

        if(!showing) {
            last_loc = RichUtils.removeAfterChar(location, 15);
        }

        showing = true;

        drawRect(0, 0 - size, 143, 43 - size, -2500134);
        drawRect(0, 0 - size, 140, 40 - size, -10066329);

        drawString("§a§lYou are now entering", 5, 5 - size, -1);
        drawString("§e" + last_loc, 7,20 - size, 1.5f, 13782543);

        //Animation
        if(size > 0 && !animation) {
            //Showing
            size-=1;
            timeout = System.currentTimeMillis();
        }else{
            //Timeout
            if(System.currentTimeMillis() - timeout >= 3000) {
                animation = true;
            }
            //Hiding
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
