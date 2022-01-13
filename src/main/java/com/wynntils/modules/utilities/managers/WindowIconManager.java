/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.managers;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.utils.reflections.ReflectionMethods;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.instances.ServerIcon;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.Util;

public class WindowIconManager {

    private static boolean setToServerIcon = false;
    private static boolean serverIconInvalid = false;

    private static void deleteIcon() {
        McIf.mc().addScheduledTask(() -> ReflectionMethods.Minecraft$setWindowIcon.invoke(Minecraft.getMinecraft()));
        setToServerIcon = false;
        serverIconInvalid = false;
    }

    private static void setIcon() {
        BufferedImage bufferedimage;
        ServerData server = McIf.mc().getCurrentServerData();
        String base64;
        if (server == null || (base64 = server.getBase64EncodedIconData()) == null) {
            serverIconInvalid = true;
            return;
        }
        try {
            bufferedimage = ServerIcon.parseServerIcon(base64);
        } catch (Throwable throwable) {
            Reference.LOGGER.error("Invalid icon for server " + server.serverName + " (" + server.serverIP + ")", throwable);
            serverIconInvalid = true;
            return;
        }


        int[] aint = bufferedimage.getRGB(0, 0, 64, 64, null, 0, 64);  // 64x64
        ByteBuffer bytebuffer128 = ByteBuffer.allocateDirect(16 * aint.length);
        ByteBuffer bytebuffer64 = ByteBuffer.allocateDirect(4 * aint.length);
        ByteBuffer bytebuffer32 = ByteBuffer.allocateDirect(aint.length);
        ByteBuffer bytebuffer16 = ByteBuffer.allocateDirect(aint.length / 4);

        int w = 64;
        int y = 0;
        int x = 0;
        for (int argb : aint) {
            int rgba = argb << 8 | argb >> 24 & 255;
            bytebuffer64.putInt(rgba);
            if (y % 2 == 0 && x % 2 == 0) {
                bytebuffer32.putInt(rgba);
                if (y % 4 == 0 && x % 4 == 0) {
                    bytebuffer16.putInt(rgba);
                }
            }
            ++x;
            if (x == w) {
                ++y;
                x = 0;
            }
        }

        for (y = 0; y < 128; y += 2) {
            ByteBuffer row = ByteBuffer.allocate(128 * 4);
            for (x = 0; x < 128; x += 2) {
                int argb = aint[y / 2 * w + x / 2];
                int rgba = argb << 8 | argb >> 24 & 255;
                row.putInt(rgba);
                row.putInt(rgba);
            }
            row.flip();
            bytebuffer128.put(row);
            row.rewind();
            bytebuffer128.put(row);
        }

        bytebuffer128.flip();
        bytebuffer64.flip();
        bytebuffer32.flip();
        bytebuffer16.flip();

        McIf.mc().addScheduledTask(() -> Display.setIcon(new ByteBuffer[]{ bytebuffer128, bytebuffer64, bytebuffer32, bytebuffer16 }));
        setToServerIcon = true;
        serverIconInvalid = false;
    }

    public static synchronized void update() {
        if (Util.getOSType() == Util.EnumOS.OSX) return;  // Does not work on macOS

        if (UtilitiesConfig.INSTANCE.changeWindowIcon && Reference.onServer) {
            if (!setToServerIcon && !serverIconInvalid) {
                setIcon();
            }
        } else if (setToServerIcon) {
            deleteIcon();
        }
    }

}
