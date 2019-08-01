/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.utils.Pair;
import com.wynntils.modules.core.enums.OverlayRotation;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ManaBarOverlay extends Overlay {
    public ManaBarOverlay() {
        super("Mana Bar", 81, 21, true, 0.5f, 1.0f, 10, -38, OverlayGrowFrom.MIDDLE_LEFT, RenderGameOverlayEvent.ElementType.FOOD, RenderGameOverlayEvent.ElementType.HEALTHMOUNT);
    }

//    @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
//    @Setting(displayName = "Animation Speed",description = "How fast should the bar changes happen(0 for instant)")
//    public float animated = 2f;


    /* Temp in UtilitiesConfig so users can change textures on the fly
    @Setting(displayName = "Texture", description = "What texture to use")
    public ManaTextures texture = ManaTextures.a;
    */

    @Setting(displayName = "Flip", description = "Should the filling of the bar be flipped")
    public boolean flip = true;

    @Setting(displayName = "Text Position", description = "The position offset of the text")
    public Pair<Integer,Integer> textPositionOffset = new Pair<>(40,-10);

    @Setting(displayName = "Text Name", description = "The color of the text")
    public CustomColor textColor = CommonColors.LIGHT_BLUE;

    private static float mana = 0.0f;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!(visible = (getPlayerInfo().getCurrentMana() != -1 && !Reference.onLobby))) return;
        if (OverlayConfig.Mana.INSTANCE.animated > 0.0f && OverlayConfig.Mana.INSTANCE.animated < 10.0f)
            mana -= (OverlayConfig.Mana.INSTANCE.animated * 0.1f) * (mana - (float) getPlayerInfo().getCurrentMana());
        else mana = getPlayerInfo().getCurrentMana();
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        switch (OverlayConfig.Mana.INSTANCE.manaTexture) {
            case Wynn:
                drawDefaultBar(-1, 8, 0, 17,textColor);
                break;
            case a: drawDefaultBar(-1,7,18,33,textColor);
                break;
            case b: drawDefaultBar(-1,8,34,51,textColor);
                break;
            case c: drawDefaultBar(-1,7,52,67,textColor);
                break;
            case d: drawDefaultBar(-1,7,68,83,textColor);
                break;
            case Brune:
                drawDefaultBar(-1, 8, 83, 100,textColor);
                break;
            case Inverse:
                drawDefaultBar(-1, 7, 100, 115,CommonColors.MAGENTA);
                break;
            case Aether:
                drawDefaultBar(-1, 7, 116, 131,textColor);
                break;
            case Skull:
                drawDefaultBar(-1, 8, 132, 147, textColor);
                break;
            case Skyrim:
                drawDefaultBar(-1, 8, 148, 163, textColor);
                break;
        }
    }

    private void drawDefaultBar(int y1, int y2, int ty1, int ty2, CustomColor cc) {
        if (OverlayConfig.Mana.INSTANCE.overlayRotation == OverlayRotation.NORMAL) {
            drawString(getPlayerInfo().getCurrentMana() + " ✺ " + getPlayerInfo().getMaxMana(), textPositionOffset.a - (81-OverlayConfig.Mana.INSTANCE.width), textPositionOffset.b, cc, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.Mana.INSTANCE.textShadow);
        }
        rotate(OverlayConfig.Mana.INSTANCE.overlayRotation.getDegrees());
        drawProgressBar(Textures.Overlays.bars_mana, OverlayConfig.Mana.INSTANCE.width, y1, 0, y2, ty1, ty2, (flip ? -mana : mana) / (float) getPlayerInfo().getMaxMana());
    }
}