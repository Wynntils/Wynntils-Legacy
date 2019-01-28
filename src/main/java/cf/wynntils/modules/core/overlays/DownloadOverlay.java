package cf.wynntils.modules.core.overlays;

import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.webapi.downloader.DownloadProfile;
import cf.wynntils.webapi.downloader.DownloaderManager;
import cf.wynntils.webapi.downloader.enums.DownloadPhase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class DownloadOverlay extends Overlay {

    private static CustomColor background = CustomColor.fromString("333341",1);
    private static CustomColor box = CustomColor.fromString("434355",1);
    private static CustomColor progress = CustomColor.fromString("80fd80",1);
    private static CustomColor back = CustomColor.fromString("ececec",1);

    private static CustomColor brackgroundRed = CustomColor.fromString("6e3737",1);
    private static CustomColor boxRed = CustomColor.fromString("fd8080",1);

    static int lastPercent = 0;
    static DownloadPhase lastPhase;
    static String lastTitle = "";

    static long timeToRestart = 0;

    public static int size = 53;

    public DownloadOverlay() {
        super("Downloading",20,20,true,1.0f,0.0f,0,0, null);
    }

    @Override
    public void render(RenderGameOverlayEvent.Post e) {
        if (e.isCanceled() || e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if(DownloaderManager.currentPhase != DownloadPhase.WAITING || size < 53) {
            if(DownloaderManager.restartOnQueueFinish && DownloaderManager.currentPhase == DownloadPhase.WAITING) {
                if(timeToRestart == 0) {
                    timeToRestart = System.currentTimeMillis() + 10000;
                }
                if(timeToRestart - System.currentTimeMillis() <= 0) {
                    mc.shutdown();
                    return;
                }

                drawRect(brackgroundRed, -172,0 - size, 0, 52 - size);
                drawRect(boxRed, -170,0 - size, 0, 50 - size);
                drawString("Your game will be closed in", -84, 15 - size, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
                drawString(((timeToRestart - System.currentTimeMillis()) / 1000) + " seconds", -84, 25 - size, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
                return;
            }

            DownloadProfile df = DownloaderManager.getCurrentDownload();

            if (df != null) {
                lastPercent = DownloaderManager.progression;
                lastTitle = df.getTitle();
                lastPhase = DownloaderManager.currentPhase;
            }

            drawRect(background, -172,0 - size, 0, 52 - size);
            drawRect(box, -170,0 - size, 0, 50 - size);
            drawString(lastTitle, -85, 5 - size, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);

            drawRect(back, -160, 20 - size, -10, 36 - size);

            drawRect(progress, -160, 20 - size, ((lastPercent * (-10 + 160)) + 100 * -160) / 100, 36 - size);
            drawString(lastPercent + "%", -84, 25 - size, CommonColors.LIGHT_GRAY, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);

            drawString((DownloaderManager.getQueueSizeLeft()) + " files left", -84, 40 - size, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
        }

        if (size > 0 && DownloaderManager.currentPhase != DownloadPhase.WAITING) {
            size--;
        } else if (size < 53 && DownloaderManager.currentPhase == DownloadPhase.WAITING && !DownloaderManager.restartOnQueueFinish) {
            size++;
        }
    }

}
