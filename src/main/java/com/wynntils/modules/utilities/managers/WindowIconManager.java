package com.wynntils.modules.utilities.managers;

import com.wynntils.Reference;
import com.wynntils.core.utils.reflections.ReflectionMethods;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.instances.ServerIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.Util;
import org.lwjgl.opengl.Display;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class WindowIconManager {

    private static boolean setToServerIcon = false;
    private static boolean serverIconInvalid = false;

    private static void deleteIcon() {
        ReflectionMethods.Minecraft$setWindowIcon.invoke(Minecraft.getMinecraft());
        setToServerIcon = false;
        serverIconInvalid = false;
    }

    private static void setIcon() {
        BufferedImage bufferedimage;
        ServerData server = Minecraft.getMinecraft().getCurrentServerData();
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
        ByteBuffer bytebuffer64 = ByteBuffer.allocate(4 * aint.length);
        ByteBuffer bytebuffer32 = ByteBuffer.allocate(aint.length);
        ByteBuffer bytebuffer16 = ByteBuffer.allocate(aint.length / 4);

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

        bytebuffer64.flip();
        bytebuffer32.flip();
        bytebuffer16.flip();

        Display.setIcon(new ByteBuffer[]{ bytebuffer64, bytebuffer32, bytebuffer16 });
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
