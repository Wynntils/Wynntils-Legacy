package com.wynntils.modules.utilities.managers;

import com.wynntils.Reference;
import com.wynntils.core.utils.reflections.ReflectionMethods;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.instances.ServerIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.Display;

import java.nio.ByteBuffer;

public class WindowIconManager {

    private static ServerIcon currentServerIcon = null;
    private static boolean setToServerIcon = false;

    public static synchronized void deleteIcon() {
        if (currentServerIcon != null) currentServerIcon.delete();
        currentServerIcon = null;
        if (setToServerIcon) {
            ReflectionMethods.Minecraft$setWindowIcon.invoke(Minecraft.getMinecraft());
            setToServerIcon = false;
        }
    }

    private static synchronized void setIcon(DynamicTexture icon) {
        int[] aint = icon.getTextureData();  // 64x64
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
    }

    public static synchronized void createIcon() {
        if (currentServerIcon != null) currentServerIcon.delete();
        currentServerIcon = null;
        if (UtilitiesConfig.INSTANCE.changeWindowIcon && Reference.onServer) {
            currentServerIcon = new ServerIcon(Minecraft.getMinecraft().getCurrentServerData(), true);
            currentServerIcon.onDone(c -> {
                if (c != null && UtilitiesConfig.INSTANCE.changeWindowIcon) {
                    setIcon(c.getIcon());
                    if (currentServerIcon != null) currentServerIcon.delete();
                    currentServerIcon = null;
                }
            });
            currentServerIcon.getServerIcon();
            ServerIcon.ping();
        } else if (setToServerIcon) {
            deleteIcon();
        }
    }

    public static synchronized void update() {
        if (UtilitiesConfig.INSTANCE.changeWindowIcon && Reference.onServer) {
            if (!setToServerIcon) {
                if (currentServerIcon == null) {
                    createIcon();
                } else {
                    ServerIcon.ping();
                }
            }
        } else {
            deleteIcon();
        }
    }

}
