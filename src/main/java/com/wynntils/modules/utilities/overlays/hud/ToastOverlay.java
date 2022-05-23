/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.instances.Toast;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class ToastOverlay extends Overlay {

    private static final int DISPLAY_AMNT = 3;
    private static final List<Toast> toastList = new ArrayList<>();
    private static final Toast[] displayedToast = new Toast[DISPLAY_AMNT];
    public transient int topT_X1 = 0, topT_X2 = 160, middleT_X1 = 0, middleT_X2 = 160, bottomT_X1 = 0, bottomT_X2 = 160;

    private static final CustomColor questCompletedColor = new CustomColor(89, 149, 55); // green
    private static final CustomColor discoveryColor = new CustomColor(140, 79, 193);  // purple
    private static final CustomColor territoryColor = new CustomColor(112, 112, 239); // indigo
    private static final CustomColor areaDiscoveredColor = new CustomColor(184, 89, 181); // magenta
    private static final CustomColor levelUpColor = new CustomColor(85, 144, 182); // blue
    private static final CustomColor defaultColor = new CustomColor(0, 0, 0); // black

    public ToastOverlay() {
        super("Toasts", 160, 192, true, 1, 0, 0, 0, OverlayGrowFrom.TOP_RIGHT);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if (Reference.onWorld && OverlayConfig.ToastsSettings.INSTANCE.enableToast) {
            for (int j = 0; j < DISPLAY_AMNT; j++) {
                if (displayedToast[j] == null) continue;
                CustomColor c;
                int iconX, iconY;
                switch (displayedToast[j].getToastType()) {
                    case QUEST_COMPLETED:
                        c = questCompletedColor;
                        iconX = 178; iconY = 0;
                        break;
                    case DISCOVERY:
                        c = discoveryColor;
                        iconX = 161; iconY = 0;
                        break;
                    case TERRITORY:
                        c = territoryColor;
                        iconX = 160; iconY = 16;
                        break;
                    case AREA_DISCOVERED:
                        c = areaDiscoveredColor;
                        iconX = 176; iconY = 16;
                        break;
                    case LEVEL_UP:
                        c = levelUpColor;
                        iconX = 160; iconY = 32;
                        break;
                    default:
                        c = defaultColor;
                        iconX = 178; iconY = 0;
                        break;
                }

                float getAnimated = displayedToast[j].getAnimated();
                int y = displayedToast[j].getY();
                int height = displayedToast[j].getHeight();

                // Rolling Parchement:
                drawRectF(Textures.Overlays.toast, getAnimated -160, y, getAnimated, y + 22, topT_X1, 0, topT_X2, 22);  // top
                drawRectF(Textures.Overlays.toast, getAnimated -160, y + 22, getAnimated, y + height + 41, middleT_X1, 23, middleT_X2, 42);  // middle
                drawRectF(Textures.Overlays.toast, getAnimated -160, y + height + 41, getAnimated, y + height + 64, bottomT_X1, 43, bottomT_X2, 66);  // bottom
                // Icon
                drawRectF(Textures.Overlays.toast, getAnimated + (OverlayConfig.ToastsSettings.INSTANCE.flipToast ? -32 : -144), y + (height / 2.0f) + 24, getAnimated + (OverlayConfig.ToastsSettings.INSTANCE.flipToast ? -16 : -128), y + (height / 2.0f) + 40, iconX, iconY, iconX+16, iconY+16);
                // Text
                drawString(displayedToast[j].getTitle(), getAnimated -160 + (OverlayConfig.ToastsSettings.INSTANCE.flipToast ? 8 : 35), 22 + y, c, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                for (int n = 0; n < displayedToast[j].getSubtitle().length; n++) {
                    drawString(displayedToast[j].getSubtitle()[n], getAnimated -160 + (OverlayConfig.ToastsSettings.INSTANCE.flipToast ? 8 : 35), 33 + 10*n + y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                }
                // Animation
                if (OverlayConfig.ToastsSettings.INSTANCE.flipToast) {
                    if ((McIf.getSystemTime() - displayedToast[j].getCreationTime()) > 5000L) {
                        displayedToast[j].setAnimated(displayedToast[j].getAnimated() - .3f);
                    } else if (displayedToast[j].getAnimated() < 0) {
                        displayedToast[j].setAnimated(Math.min(displayedToast[j].getAnimated() + .3f, 0));
                    }
                } else {
                    if ((McIf.getSystemTime() - displayedToast[j].getCreationTime()) > 5000L) {
                        displayedToast[j].setAnimated(displayedToast[j].getAnimated() + .3f);
                    } else if (displayedToast[j].getAnimated() > 0) {
                        displayedToast[j].setAnimated(Math.max(displayedToast[j].getAnimated() - .3f, 0));
                    }
                }
            }
        }
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (McIf.mc().currentScreen != null) return;  // HeyZeer0: This will avoid toasts being processed when the user can't view them

        if (OverlayConfig.ToastsSettings.INSTANCE.enableToast) {
            // Flip coordinates:
            if (OverlayConfig.ToastsSettings.INSTANCE.flipToast) {
                topT_X2 = 0; topT_X1 = 160;
                middleT_X2 = 0; middleT_X1 = 160;
                bottomT_X2 = 0; bottomT_X1 = 160;
            } else {
                topT_X1 = 0; topT_X2 = 160;
                middleT_X1 = 0; middleT_X2 = 160;
                bottomT_X1 = 0; bottomT_X2 = 160;
            }
            // Adds new toasts
            List<Integer> toBeRemoved = new ArrayList<>();
            int curHeight = 0;
            for (int j = 0; j < DISPLAY_AMNT; j++) {
                if (displayedToast[j] != null) {
                    curHeight += displayedToast[j].getHeight();
                } else if (toastList.size() > 0) {
                    displayedToast[j] = toastList.get(0);
                    displayedToast[j].setY(j * 64 + curHeight);
                    curHeight += (displayedToast[j].getSubtitle().length - 1) * 10;
                    displayedToast[j].setHeight((displayedToast[j].getSubtitle().length - 1) * 10);
                    if (OverlayConfig.ToastsSettings.INSTANCE.flipToast) { displayedToast[j].setAnimated(-160); }
                    toastList.remove(0);
                }
                if (displayedToast[j] == null) continue;
                if ((displayedToast[j].getAnimated() > 160 || displayedToast[j].getAnimated() < -160) && (McIf.getSystemTime() - displayedToast[j].getCreationTime()) > 5000L)
                    toBeRemoved.add(j);
            }
            // Removes expired toasts
            for (Integer i : toBeRemoved) {
                displayedToast[i] = null;
            }
        }
    }

    public static void addToast(Toast toast) {
        toastList.add(toast);
    }
}
