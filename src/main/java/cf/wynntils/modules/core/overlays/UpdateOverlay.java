package cf.wynntils.modules.core.overlays;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.downloader.DownloaderManager;
import cf.wynntils.webapi.downloader.enums.DownloadAction;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.io.File;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class UpdateOverlay extends Overlay {

    private CustomColor background = new CustomColor("333341");
    private CustomColor box = new CustomColor("434355");
    private CustomColor yes = new CustomColor("80fd80");
    private CustomColor no = new CustomColor("fd8080");

    public UpdateOverlay() {
        super("Update Overlay", 20, 20, true, 1f, 0f, 0, 0);
    }

    boolean disappear = false;
    boolean acceptYesOrNo = false;
    boolean download = false;

    @Override
    public void render(RenderGameOverlayEvent.Post e) { //TODO ANIMATIONS
        if(e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if(!WebManager.getUpdate().hasUpdate()) {
            return;
        }

        //disappear = false;
        if(disappear) {
            return;
        }

        acceptYesOrNo = true;

        drawRect(background, -172,0, 0, 62);
        drawRect(box, -170,0, 0, 60);

        drawString("Wynntils §av" + Reference.VERSION, -165, 5, CommonColors.ORANGE);
        drawString("A new update is available §ev" + WebManager.getUpdate().getLatestUpdate(), -165, 15, CommonColors.WHITE);
        drawString("Download automagically? §a(y/n)", -165, 25, CommonColors.LIGHT_GRAY);

        drawRect(yes, -155,40, -95, 55);
        drawRect(no, -75 ,40, -15, 55);

        drawString("Yes (y)", -125, 44, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
        drawString("No (n)", -43, 44, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event){
        if(download && disappear) {
            download = false;

            try{
                //String jar = WebManager.getLatestJarFileUrl();
                File f = new File(Reference.MOD_STORAGE_ROOT + "/updates");
                DownloaderManager.queueDownload("Update " + WebManager.getUpdate().getLatestUpdate(), "http://dl.heyzeer0.cf/WynnRP/entering.gif", f, DownloadAction.SAVE, (x) -> {
                    if(x) {

                    }
                });

            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if(acceptYesOrNo) {
            if(Keyboard.isKeyDown(Keyboard.KEY_Y)) {
                //TODO start popout animation, when finish set disappear to TRUE to start the download
                disappear = true;
                acceptYesOrNo = false;
                download = true;
            }else if(Keyboard.isKeyDown(Keyboard.KEY_N)) {
                disappear = true;
                acceptYesOrNo = false;
                download = false;
            }
        }
    }

}
