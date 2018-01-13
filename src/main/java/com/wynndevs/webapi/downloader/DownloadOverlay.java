package com.wynndevs.webapi.downloader;

import com.wynndevs.core.Reference;
import com.wynndevs.modules.richpresence.guis.WRPGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.logging.LogManager;

public class DownloadOverlay extends WRPGui {

    int size = 40;
    int lastPercent = 0;
    String lastTitle = "";

    public DownloadOverlay(Minecraft mc) {
        super(mc);
    }

    @SubscribeEvent(priority= EventPriority.HIGHEST)
    public void onRenderExperienceBar(RenderGameOverlayEvent.Post e) {
        if (e.isCanceled() || e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        //just to appears when a download is currently running
        //and when size != 0 cuz whe need to do the "size off" animation
        if(DownloaderManager.inDownload || size != 40) {
            DownloadProfile df = DownloaderManager.getCurrentDownload();

            if(df != null) {
                lastPercent = DownloaderManager.progression;
                lastTitle = df.getTitle();
            }

            ScaledResolution res = new ScaledResolution(mc);

            int x = res.getScaledWidth() - 10;
            int y = 10;

            drawRect(x, y - size, x - 120, 43 - size, new Color(58, 65, 88).getRGB());
            drawRect(x - 110, y + 15 - size, x - 10, 35 - size, new Color(229, 229, 229).getRGB());
            drawRect(x - 110, y + 15 - size, x - (110 - lastPercent), 35 - size, new Color(0, 255, 0).getRGB());
            String percent = lastPercent + "%";
            drawStringWithoutShadow(percent, x - 110 + ((101 - mc.fontRenderer.getStringWidth(percent)) / 2), y + 16 - size, 0);
            String title = "Downloading " + lastTitle;
            drawStringWithoutShadow(title, x - 120 + ((121 - mc.fontRenderer.getStringWidth(title)) / 2), y + 4 - size, -1);

            if(size > 0 && DownloaderManager.inDownload) {
                size--;
            }else if(size < 40 && !DownloaderManager.inDownload) {
                size++;
            }
        }
    }

}
