/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.core.overlays;

import com.wynntils.McIf;
import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.CoreModule;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.downloader.DownloaderManager;
import com.wynntils.webapi.downloader.enums.DownloadAction;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;

public class UpdateOverlay extends Overlay {

    private static CustomColor background = CustomColor.fromInt(0x333341, 1);
    private static CustomColor box = CustomColor.fromInt(0x434355, 1);
    private static CustomColor yes = CustomColor.fromInt(0x80fd80, 1);
    private static CustomColor no = CustomColor.fromInt(0xfd8080, 1);

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
        if (e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if (Reference.developmentEnvironment || WebManager.getUpdate() == null || !WebManager.getUpdate().hasUpdate()) {
            return;
        }

        if (disappear) {
            return;
        }

        if (timeout == 0) {
            timeout = System.currentTimeMillis();
        }

        drawRect(background, -172, 0 - size, 0, 62 - size);
        drawRect(box, -170, 0 - size, 0, 60 - size);

        drawString("Wynntils " + TextFormatting.GREEN + "v" + Reference.VERSION + " - " + TextFormatting.WHITE + (((timeout + 35000) - System.currentTimeMillis()) / 1000) + "s left", -165, 5 - size, CommonColors.ORANGE);
        drawString("Update " + TextFormatting.YELLOW + WebManager.getUpdate().getLatestUpdate() + TextFormatting.WHITE + " found.", -165, 15 - size, CommonColors.WHITE);

        drawString("Download automagically? " + TextFormatting.GREEN + "(y/n)", -165, 25 - size, CommonColors.LIGHT_GRAY);

        drawRect(yes, -155, 40 - size, -95, 55 - size);
        drawRect(no, -75, 40 - size, -15, 55 - size);

        drawString("Yes (y)", -125, 44 - size, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
        drawString("No (n)", -43, 44 - size, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);

        if (size > 0 && System.currentTimeMillis() - timeout < 35000) {
            size--;
            if (size <= 0) {
                acceptYesOrNo = true;
            }
        } else if (size < 63 && System.currentTimeMillis() - timeout >= 35000) {
            size++;
            if (size >= 63) {
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

    public static void ignore() {
        reset();
        disappear = true;
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (download && disappear) {
            download = false;

            try {
                File directory = new File(Reference.MOD_STORAGE_ROOT, "updates");
                String url = WebManager.getUpdate().getDownloadUrl();
                String jarName = getJarNameFromUrl(url);

                DownloadOverlay.size = 0;
                DownloaderManager.restartGameOnNextQueue();
                DownloaderManager.queueDownload("Updating to " + WebManager.getUpdate().getLatestUpdate(), url, directory, DownloadAction.SAVE, (x) -> {
                    if (x) {
                        try {
                            String message = TextFormatting.DARK_AQUA + "An update to Wynntils (" + WebManager.getUpdate().getLatestUpdate() + ") has been downloaded and will be applied when the game is restarted.";
                            McIf.player().sendMessage(new TextComponentString(message));
                            scheduleCopyUpdateAtShutdown(jarName);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (acceptYesOrNo) {
            if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
                disappear = true;
                acceptYesOrNo = false;
                download = true;

                CoreDBConfig.INSTANCE.showChangelogs = true;
                CoreDBConfig.INSTANCE.lastVersion = Reference.VERSION;
                CoreDBConfig.INSTANCE.saveSettings(CoreModule.getModule());
            } else if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
                timeout = 35000;
                acceptYesOrNo = false;
                download = false;
            }
        }
    }

    public static String getJarNameFromUrl(String url) {
        String[] sUrl = url.split("/");
        return sUrl[sUrl.length - 1];
    }

    public static String getUpdateDownloadUrl() throws IOException {
        return WebManager.getUpdateData(CoreDBConfig.INSTANCE.updateStream).get("url");
    }

    public static void scheduleCopyUpdateAtShutdown(String jarName) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Reference.LOGGER.info("Attempting to apply Wynntils update.");
                File oldJar = ModCore.jarFile;

                if (oldJar == null || !oldJar.exists() || oldJar.isDirectory()) {
                    Reference.LOGGER.warn("Old jar file not found.");
                    return;
                }

                File newJar = new File(new File(Reference.MOD_STORAGE_ROOT, "updates"), jarName);
                Utils.copyFile(newJar, oldJar);
                newJar.delete();
                Reference.LOGGER.info("Successfully applied Wynntils update.");
            } catch (IOException ex) {
                Reference.LOGGER.error("Unable to apply Wynntils update.", ex);
            }
        }, "wynntils-autoupdate-applier"));
        WebManager.getUpdate().updateDownloaded();
    }

    public static boolean isDownloading() {
        return download;
    }

}
