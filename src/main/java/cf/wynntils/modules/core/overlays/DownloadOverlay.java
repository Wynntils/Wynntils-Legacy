package cf.wynntils.modules.core.overlays;


import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.webapi.downloader.DownloadProfile;
import cf.wynntils.webapi.downloader.DownloaderManager;
import cf.wynntils.webapi.downloader.enums.DownloadPhase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class DownloadOverlay extends Overlay {
    int size = 40;
    int lastPercent = 0;
    DownloadPhase lastPhase;
    String lastTitle = "";

    int extraY = 0;
    boolean hasMultipleValues = false;

    public DownloadOverlay() {
        super("Downloading Overlay",20,20,true,1.0f,0.0f,-10,10);
    }

    @Override
    public void render(RenderGameOverlayEvent.Post e) {  //TODO, CHECK THIS IS WORKING WITH THE NEW SYSTEM
        if (e.isCanceled() || e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        //just to appears when a download is currently running
        //and when size != 0 cuz whe need to do the "size off" animation
        if(DownloaderManager.currentPhase != DownloadPhase.WAITING || size != 40) {
            DownloadProfile df = DownloaderManager.getCurrentDownload();

            if (df != null) {
                lastPercent = DownloaderManager.progression;
                lastTitle = df.getTitle();
                lastPhase = DownloaderManager.currentPhase;
            }

            drawRect(CommonColors.GRAY, 0, -size, -120, (43 + extraY) - size);
            drawRect(CommonColors.LIGHT_GRAY,-110, 15 - size, 0 - 10, 35 - size);
            drawRect(CommonColors.LIGHT_GREEN,-110, 15 - size, -(110 - lastPercent), 35 - size);

            String percent = lastPercent + "%";
            drawString(percent,-110 + ((101 - mc.fontRenderer.getStringWidth(percent)) / 2), 16 - size,CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            String title = (lastPhase == DownloadPhase.DOWNLOADING ? "Downloading" : "Unzipping") + " " + lastTitle;
            drawString(title,-120 + ((121 - mc.fontRenderer.getStringWidth(title)) / 2), 4 - size,CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (hasMultipleValues && extraY < 20) {
                extraY++;
            } else if (!hasMultipleValues && extraY < 0) {
                extraY--;
            }

            if (size > 0 && DownloaderManager.currentPhase != DownloadPhase.WAITING) {
                size--;
            } else if (size < 40 && DownloaderManager.currentPhase == DownloadPhase.WAITING) {
                size++;
            }
        }
    }

}
