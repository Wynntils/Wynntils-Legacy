package com.wynntils.modules.utilities.overlays.hud;

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

public class ToastOverlay extends Overlay {

    private static final int DISPLAY_AMNT = 3;
    private static ArrayList<Toast> toastList = new ArrayList<>();
    private static Toast[] displayedToast = new Toast[DISPLAY_AMNT];

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
                        c = new CustomColor(.514f,.96f,.259f);
                        iconX = 178; iconY = 0;
                        break;
                    case DISCOVERY:
                        c = new CustomColor(.718f,.384f,1);
                        iconX = 161; iconY = 0;
                        break;
                    case TERRITORY:
                        c = new CustomColor(.392f,.392f,1);
                        iconX = 160; iconY = 16;
                        break;
                    case AREA_DISCOVERED:
                        c = new CustomColor(.949f, .588f, .937f);
                        iconX = 176; iconY = 16;
                        break;
                    default:
                        c = new CustomColor(1,1,1);
                        iconX = 178; iconY = 0;
                        break;
                }

                float getAnimated = displayedToast[j].getAnimated();
                int y = displayedToast[j].getY();
                int height = displayedToast[j].getHeight();
                //Rolling Parchement:
                drawRectF(Textures.Overlays.toast, getAnimated -160, y,getAnimated, y + 22,0,0,160,22); //top
                drawRectF(Textures.Overlays.toast, getAnimated -160, y + 22, getAnimated, y + height + 41, 0, 23, 160, 42); //middle
                drawRectF(Textures.Overlays.toast, getAnimated -160, y + height + 41, getAnimated, y + height + 64, 0, 43, 160, 66); //bottom
                //Icon
                drawRectF(Textures.Overlays.toast, getAnimated + 16 -160, y + (height/2) + 24, getAnimated + 32 -160, y + (height/2) + 40, iconX, iconY, iconX+16, iconY+16);
                //Text
                drawString(displayedToast[j].getTitle(), 35 + getAnimated -160, 22 + y, c, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                for (int n = 0; n < displayedToast[j].getSubtitle().length; n++) {
                    drawString(displayedToast[j].getSubtitle()[n], 35 + getAnimated -160, 33 + 10*n + y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                }

                if ((Minecraft.getSystemTime() - displayedToast[j].getCreationTime()) > 5000L) {
                    displayedToast[j].setAnimated(displayedToast[j].getAnimated() + .3f);
                } else if (displayedToast[j].getAnimated() > 0) {
                    displayedToast[j].setAnimated(Math.max(displayedToast[j].getAnimated() - .3f, 0));
                }
            }
        }
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (OverlayConfig.ToastsSettings.INSTANCE.enableToast) {
            ArrayList<Integer> toBeRemoved = new ArrayList<>();
            int curHeight = 0;
            for (int j = 0; j < DISPLAY_AMNT; j++) {
                if (displayedToast[j] != null) {
                    curHeight += displayedToast[j].getHeight();
                } else if (toastList.size() > 0) {
                    displayedToast[j] = toastList.get(0);
                    displayedToast[j].setY(j * 64 + curHeight);
                    curHeight += (displayedToast[j].getSubtitle().length - 1) * 10;
                    displayedToast[j].setHeight((displayedToast[j].getSubtitle().length - 1) * 10);
                    toastList.remove(0);
                }
                if (displayedToast[j] == null) continue;
                if (displayedToast[j].getAnimated() >= 160 && (Minecraft.getSystemTime() - displayedToast[j].getCreationTime()) > 5000L)
                    toBeRemoved.add(j);
            }
            for (Integer i : toBeRemoved) {
                displayedToast[i] = null;
            }
        }
    }

    public static void addToast(Toast toast) {
        toastList.add(toast);
    }
}
