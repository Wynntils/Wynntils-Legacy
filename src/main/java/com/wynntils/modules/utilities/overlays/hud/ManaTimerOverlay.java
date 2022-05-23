package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.InventoryData;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.core.enums.OverlayRotation;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ManaTimerOverlay extends Overlay {
    public ManaTimerOverlay() {
        super("Mana Timer Bar", 81, 21, true, 0.57f, 1.039f, 10, -38, OverlayGrowFrom.MIDDLE_LEFT);
    }

    @Setting(displayName = "Flip", description = "Should the filling of the bar be flipped")
    public boolean flip = false;

    @Setting(displayName = "Text Position", description = "The position offset of the text")
    public Pair<Integer, Integer> textPositionOffset = new Pair<>(40, -10);

    @Setting(displayName = "Text Name", description = "The color of the text")
    public CustomColor textColor = CommonColors.LIGHT_BLUE;

    private static float manaTime = 0.0f;
    public static boolean isTimeFrozen = false;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        visible = PlayerInfo.get(InventoryData.class).getTicksToNextSoulPoint() != -1;
        if (!visible || Reference.onLobby) return;

        int percentUntilProc = PlayerInfo.get(InventoryData.class).getTicksToNextManaRegen();
        float displayedValue = (OverlayConfig.ManaTimer.INSTANCE.manaTimerUsePercentage) ? percentUntilProc : percentUntilProc / 20.0f;

        if (OverlayConfig.ManaTimer.INSTANCE.animated > 0.0f && OverlayConfig.ManaTimer.INSTANCE.animated < 10.0f) {
            manaTime -= (OverlayConfig.ManaTimer.INSTANCE.animated * 0.1f) * (manaTime - displayedValue);
        } else {
            manaTime = displayedValue;
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!OverlayConfig.ManaTimer.INSTANCE.showManaTimer) return;
        switch (OverlayConfig.ManaTimer.INSTANCE.manaTexture) {
            case Wynn:
                drawDefaultBar(-1, 8, 0, 17, textColor);
                break;
            case a: drawDefaultBar(-1, 7, 18, 33, textColor);
                break;
            case b: drawDefaultBar(-1, 8, 34, 51, textColor);
                break;
            case c: drawDefaultBar(-1, 7, 52, 67, textColor);
                break;
            case d: drawDefaultBar(-1, 7, 68, 83, textColor);
                break;
            case Brune:
                drawDefaultBar(-1, 8, 83, 100, textColor);
                break;
            case Inverse:
                drawDefaultBar(-1, 7, 100, 115, CommonColors.MAGENTA);
                break;
            case Aether:
                drawDefaultBar(-1, 7, 116, 131, textColor);
                break;
            case Skull:
                drawDefaultBar(-1, 8, 132, 147, textColor);
                break;
            case Skyrim:
                drawDefaultBar(-1, 8, 148, 163, textColor);
                break;
            case Rune:
                drawDefaultBar(-1, 8, 164, 179, textColor);
                break;
        }
    }

    private void drawDefaultBar(int y1, int y2, int ty1, int ty2, CustomColor cc) {
        int maxProgress = (OverlayConfig.ManaTimer.INSTANCE.manaTimerUsePercentage) ? 100 : 5;
        char suffix = (OverlayConfig.ManaTimer.INSTANCE.manaTimerUsePercentage) ? '%' : 's';

        String displayedMax = String.valueOf(maxProgress) + suffix;
        String decimalFormat = (OverlayConfig.ManaTimer.INSTANCE.manaTimerUsePercentage) ?
                OverlayConfig.ManaTimer.ManaTimerDecimalFormats.Zero.getDecimalFormat() :
                OverlayConfig.ManaTimer.INSTANCE.manaTimerDecimal.getDecimalFormat();

        String displayedProgress = (!OverlayConfig.ManaTimer.INSTANCE.manaTimerUsePercentage && OverlayConfig.ManaTimer.INSTANCE.manaTimerCountDown) ?
                String.format(decimalFormat, 5.0f - manaTime) :
                String.format(decimalFormat, manaTime);
        displayedProgress += suffix;

        String formattedDisplayed = (isTimeFrozen) ? "Unavailable" : displayedProgress + " ✺ " + displayedMax;
        float progress = (isTimeFrozen) ? 0.0f : flip ? -manaTime : manaTime / (float) maxProgress;

        if (OverlayConfig.ManaTimer.INSTANCE.overlayRotation == OverlayRotation.NORMAL) {
            drawString(formattedDisplayed, textPositionOffset.a - (81-OverlayConfig.ManaTimer.INSTANCE.width), textPositionOffset.b, cc, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.Mana.INSTANCE.textShadow);
        }
        rotate(OverlayConfig.ManaTimer.INSTANCE.overlayRotation.getDegrees());
        drawProgressBar(Textures.Overlays.bars_mana, OverlayConfig.ManaTimer.INSTANCE.width, y1, 0, y2, 0, ty1, 81, ty2, progress);

    }
}
