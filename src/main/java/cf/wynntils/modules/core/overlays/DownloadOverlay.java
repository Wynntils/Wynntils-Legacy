package cf.wynntils.modules.core.overlays;


import cf.wynntils.Reference;
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

    private CustomColor background = new CustomColor("333341");
    private CustomColor box = new CustomColor("434355");
    private CustomColor progress = new CustomColor("80fd80");
    private CustomColor back = new CustomColor("ececec");

    int lastPercent = 0;
    DownloadPhase lastPhase;
    String lastTitle = "";

    boolean hasMultipleValues = false;

    public DownloadOverlay() {
        super("Downloading Overlay",20,20,true,1.0f,0.0f,0,0);
    }

    @Override
    public void render(RenderGameOverlayEvent.Post e) {  //TODO ANIMATIONS
        if (e.isCanceled() || e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if(DownloaderManager.currentPhase != DownloadPhase.WAITING) {
            DownloadProfile df = DownloaderManager.getCurrentDownload();

            if (df != null) {
                lastPercent = DownloaderManager.progression;
                lastTitle = df.getTitle();
                lastPhase = DownloaderManager.currentPhase;
            }

            drawRect(background, -172,0, 0, 52);
            drawRect(box, -170,0, 0, 50);
            drawString(lastTitle, -85, 5, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);

            drawRect(back, -160, 20, -10, 36);

            drawRect(progress, -160, 20, ((lastPercent * (-10 + 160)) + 100 * -160) / 100, 36);
            drawString(lastPercent + "%", -84, 25, CommonColors.LIGHT_GRAY, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
        }
    }

}
