/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.instances.data.CharacterData;
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

public class FocusBarOverlay extends Overlay {

    public FocusBarOverlay() {
        super("Focus Bar Overlay", 81, 21, true, 0.65f, 1.03f, -10, -38, OverlayGrowFrom.BOTTOM_RIGHT, RenderGameOverlayEvent.ElementType.HEALTH);
    }

    @Setting(displayName = "Flip", description = "Should the filling of the bar be flipped?")
    public boolean flip = false;

    @Setting(displayName = "Text Position", description = "The position offset of the text")
    public Pair<Integer, Integer> textPositionOffset = new Pair<>(-40, -10);

    @Setting(displayName = "Text Name", description = "What should the colour of the text be?")
    public CustomColor textColor = CommonColors.YELLOW;

    private static float focusProgress = 0.0f;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!(visible = (get(CharacterData.class).getFocus() != -1 && !Reference.onLobby))) return;

        if (OverlayConfig.Focus.INSTANCE.animated > 0.0f && OverlayConfig.Focus.INSTANCE.animated < 10.0f && !(focusProgress >= (float) get(CharacterData.class).getMaxFocus())) {
            focusProgress -= (OverlayConfig.Focus.INSTANCE.animated * 0.1f) * (focusProgress - (float) get(CharacterData.class).getFocus());
        } else {
            focusProgress = get(CharacterData.class).getFocus();
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        switch (OverlayConfig.Focus.INSTANCE.focusBarTexture) {
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
            case Aether:
                drawDefaultBar(-1, 7, 100, 115, textColor);
                break;
            case Skull:
                drawDefaultBar(-1, 8, 116, 131, textColor);
                break;
            case Skyrim:
                drawDefaultBar(-1, 8, 132, 147, textColor);
                break;
            case Rune:
                drawDefaultBar(-1, 8, 148, 163, textColor);
                break;
        }
    }

    private void drawDefaultBar(int y1, int y2, int ty1, int ty2, CustomColor cc) {
        if (OverlayConfig.Focus.INSTANCE.overlayRotation == OverlayRotation.NORMAL) {
            drawString(get(CharacterData.class).getFocus() + " ➸ " + get(CharacterData.class).getMaxFocus(), textPositionOffset.a  - (81 - OverlayConfig.Focus.INSTANCE.width), textPositionOffset.b, cc, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.Focus.INSTANCE.textShadow);
        }

        rotate(OverlayConfig.Focus.INSTANCE.overlayRotation.getDegrees());
        drawProgressBar(Textures.Overlays.bars_health, -OverlayConfig.Focus.INSTANCE.width, y1, 0, y2, 0, ty1, 81, ty2, (flip ? -focusProgress : focusProgress) / (float) get(CharacterData.class).getMaxFocus());
    }

}
