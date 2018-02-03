package cf.wynntils.webapi.downloader;

import cf.wynntils.webapi.downloader.enums.DownloadPhase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class DownloadOverlay {

    /*int size = 40;
    int lastPercent = 0;
    DownloadPhase lastPhase;
    String lastTitle = "";

    int extraY = 0;
    boolean hasMultipleValues = false;

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
        if(DownloaderManager.currentPhase != DownloadPhase.WAITING || size != 40) {
            DownloadProfile df = DownloaderManager.getCurrentDownload();

            if(df != null) {
                lastPercent = DownloaderManager.progression;
                lastTitle = df.getTitle();
                lastPhase = DownloaderManager.currentPhase;
            }

            ScaledResolution res = new ScaledResolution(mc);

            int x = res.getScaledWidth() - 10;
            int y = 10;

            drawRect(x, y - size, x - 120, (43 + extraY) - size, new Color(58, 65, 88).getRGB());
            drawRect(x - 110, y + 15 - size, x - 10, 35 - size, new Color(229, 229, 229).getRGB());
            drawRect(x - 110, y + 15 - size, x - (110 - lastPercent), 35 - size, new Color(0, 255, 0).getRGB());
            String percent = lastPercent + "%";
            drawStringWithoutShadow(percent, x - 110 + ((101 - mc.fontRenderer.getStringWidth(percent)) / 2), y + 16 - size, 0);
            String title = (lastPhase == DownloadPhase.DOWNLOADING ? "Downloading" : "Unzipping") + " " + lastTitle;
            drawStringWithoutShadow(title, x - 120 + ((121 - mc.fontRenderer.getStringWidth(title)) / 2), y + 4 - size, -1);

            if(hasMultipleValues && extraY < 20) {
                extraY++;
            }else if(!hasMultipleValues && extraY < 0){
                extraY--;
            }

            if(size > 0 && DownloaderManager.currentPhase != DownloadPhase.WAITING) {
                size--;
            }else if(size < 40 && DownloaderManager.currentPhase == DownloadPhase.WAITING) {
                size++;
            }
        }
    }*/

}
