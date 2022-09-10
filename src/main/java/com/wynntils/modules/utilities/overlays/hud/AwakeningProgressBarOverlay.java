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

public class AwakeningProgressBarOverlay extends Overlay {

    public AwakeningProgressBarOverlay() {
        super("Awakening Progress (Shaman masks) Bar", 81, 21, true, 0.43f, 1.03f, 30, -38, OverlayGrowFrom.BOTTOM_RIGHT, RenderGameOverlayEvent.ElementType.HEALTH);
    }

    @Setting(displayName = "Flip", description = "Should the filling of the bar be flipped?")
    public boolean flip = false;

    @Setting(displayName = "Text Position", description = "The position offset of the text")
    public Pair<Integer, Integer> textPositionOffset = new Pair<>(-40, -10);

    @Setting(displayName = "Text Name", description = "What should the colour of the text be?")
    public CustomColor textColor = CommonColors.BLUE;

    private static float awakeningProgress = 0.0f;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!(visible = (get(CharacterData.class).getCurrentAwakeningProgress() != -1 && !Reference.onLobby))) return;

        if (OverlayConfig.AwakeningProgress.INSTANCE.animated > 0.0f && OverlayConfig.AwakeningProgress.INSTANCE.animated < 10.0f && !(awakeningProgress >= (float) get(CharacterData.class).getMaxAwakeningProgress())) {
            awakeningProgress -= (OverlayConfig.AwakeningProgress.INSTANCE.animated * 0.1f) * (awakeningProgress - (float) get(CharacterData.class).getCurrentAwakeningProgress());
        } else {
            awakeningProgress = get(CharacterData.class).getCurrentAwakeningProgress();
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        switch (OverlayConfig.AwakeningProgress.INSTANCE.awakeningProgressTexture) {
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
        if (OverlayConfig.AwakeningProgress.INSTANCE.overlayRotation == OverlayRotation.NORMAL) {
            drawString(get(CharacterData.class).getCurrentAwakeningProgress() + " ۞ " + get(CharacterData.class).getMaxAwakeningProgress(), textPositionOffset.a  - (81-OverlayConfig.AwakeningProgress.INSTANCE.width), textPositionOffset.b, cc, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.AwakeningProgress.INSTANCE.textShadow);
        }

        rotate(OverlayConfig.AwakeningProgress.INSTANCE.overlayRotation.getDegrees());
        drawProgressBar(Textures.Overlays.bars_health, -OverlayConfig.AwakeningProgress.INSTANCE.width, y1, 0, y2, 0, ty1, 81, ty2, (flip ? -awakeningProgress : awakeningProgress) / (float) get(CharacterData.class).getMaxAwakeningProgress());
    }

}
