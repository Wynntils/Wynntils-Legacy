package cf.wynntils.modules.core.overlays;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.core.config.CoreDBConfig;
import cf.wynntils.modules.core.enums.UpdateStream;
import cf.wynntils.webapi.WebManager;
import cf.wynntils.webapi.downloader.DownloaderManager;
import cf.wynntils.webapi.downloader.enums.DownloadAction;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.io.File;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class UpdateOverlay extends Overlay {

    private static CustomColor background = CustomColor.fromString("333341",1);
    private static CustomColor box = CustomColor.fromString("434355",1);
    private static CustomColor yes = CustomColor.fromString("80fd80",1);
    private static CustomColor no = CustomColor.fromString("fd8080",1);

    public UpdateOverlay() {
        super("Update", 20, 20, true, 1f, 0f, 0, 0, null);
    }

    static boolean disappear = false;
    static boolean acceptYesOrNo = false;
    static boolean download = false;

    public static int size = 63;
    public static long timeout = 0;


    @Override
    public void render(RenderGameOverlayEvent.Post e) {
        if(e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if(!WebManager.getUpdate().hasUpdate()) {
            return;
        }

        if(disappear) {
            return;
        }

        if(timeout == 0) {
            timeout = System.currentTimeMillis();
        }

        drawRect(background, -172,0 - size, 0, 62 - size);
        drawRect(box, -170,0 - size, 0, 60 - size);

        drawString("Wynntils §av" + Reference.VERSION + " - §f" + (((timeout + 35000) - System.currentTimeMillis()) / 1000) + "s left", -165, 5 - size, CommonColors.ORANGE);
        if (WebManager.getUpdate().getLatestUpdate().startsWith("B")) {
            drawString("§eBuild " + WebManager.getUpdate().getLatestUpdate().replace("B", "") + " §fis available.", -165, 15 - size, CommonColors.WHITE);
        } else {
            drawString("A new update is available §ev" + WebManager.getUpdate().getLatestUpdate(), -165, 15 - size, CommonColors.WHITE);
        }

        drawString("Download automagically? §a(y/n)", -165, 25 - size, CommonColors.LIGHT_GRAY);

        drawRect(yes, -155,40 - size, -95, 55 - size);
        drawRect(no, -75 ,40 - size, -15, 55 - size);

        drawString("Yes (y)", -125, 44 - size, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
        drawString("No (n)", -43, 44 - size, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);

        if (size > 0 && System.currentTimeMillis() - timeout < 35000) {
            size--;
            if(size <= 0) {
                acceptYesOrNo = true;
            }
        }else if(size < 63 && System.currentTimeMillis() - timeout >= 35000) {
            size++;
            if(size >= 63) {
                disappear = true;
                acceptYesOrNo = false;
                download = false;
            }
        }
    }

    public static void reset() {
        disappear = false;
        acceptYesOrNo = false;
        download = false;
        size = 63;
        timeout = 0;
    }

    public static void forceDownload() {
        disappear = true;
        acceptYesOrNo = false;
        download = true;
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks){
        if(download && disappear) {
            download = false;

            try{
                File f = new File(Reference.MOD_STORAGE_ROOT + "/updates");

                String url;
                if (CoreDBConfig.INSTANCE.updateStream == UpdateStream.CUTTING_EDGE) {
                    url = WebManager.getCuttingEdgeJarFileUrl();
                } else {
                    return;
//                    url = WebManager.getStableJarFileUrl();
                }
                String[] sUrl = url.split("/");
                String jar_name = sUrl[sUrl.length - 1];

                DownloadOverlay.size = 0;
                DownloaderManager.restartGameOnNextQueue();
                DownloaderManager.queueDownload("Updating to " + WebManager.getUpdate().getLatestUpdate(), url, f, DownloadAction.SAVE, (x) -> {
                    if(x) {
                        try{
                            copyUpdate(jar_name);
                        }catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if(acceptYesOrNo) {
            if(Keyboard.isKeyDown(Keyboard.KEY_Y)) {
                disappear = true;
                acceptYesOrNo = false;
                download = true;
            }else if(Keyboard.isKeyDown(Keyboard.KEY_N)) {
                timeout = 35000;
                acceptYesOrNo = false;
                download = false;
            }
        }
    }

    public void copyUpdate(String jarName) throws Exception {
        File oldJar = ModCore.jarFile;

        if (oldJar == null || !oldJar.exists() || oldJar.isDirectory()) {
            TextComponentString message = new TextComponentString("The jar Forge provided Wynntils either no longer exists or is a directory, perhaps you renamed the file or you are a developer. ");
            TextComponentString uwu = new TextComponentString("^_^");
            uwu.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("You probably need to update the project")));
            message.getStyle().setColor(TextFormatting.AQUA);
            ModCore.mc().player.sendMessage(message.appendSibling(uwu));
            return;
        }

        File newJar = new File(Reference.MOD_STORAGE_ROOT + "/updates", jarName);
        Utils.copyFile(newJar, oldJar);
        TextComponentString message = new TextComponentString("Update (" + newJar.toString().replace("wynntils\\updates\\", "") + ") has been downloaded to: ");
        TextComponentString file = new TextComponentString(oldJar.toString());
        message.getStyle().setColor(TextFormatting.AQUA);
        file.getStyle().setColor(TextFormatting.DARK_AQUA);
        file.getStyle().setUnderlined(true);
        file.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to open folder")));
        file.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, oldJar.getParent()));
        ModCore.mc().player.sendMessage(message.appendSibling(file));
        newJar.delete();
    }

}
