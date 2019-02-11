/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.utils.Pair;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ExpBarOverlay extends Overlay {
    public ExpBarOverlay() {
        super("Experience Bar", 182, 7, true, 0.5f, 1.0f, 0, -29, OverlayGrowFrom.MIDDLE_CENTRE, RenderGameOverlayEvent.ElementType.JUMPBAR, RenderGameOverlayEvent.ElementType.EXPERIENCE);
    }


    @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
    @Setting(displayName = "Animation Speed",description = "How fast should the bar changes happen? (0 for instant)")
    public float animated = 2f;

    /*
    @Setting(displayName = "Texture", description = "What texture to use")
    public ExpTextures texture = ExpTextures.wynn;
    */

    @Setting(displayName = "Flip", description = "Should the filling of the bar be flipped")
    public boolean flip = false;

    @Setting(displayName = "Level Number Position", description = "The position offset of the level number")
    public Pair<Integer,Integer> textPositionOffset = new Pair<>(0,-6);

    @Setting(displayName = "Text Name", description = "What should the colour of the text be?")
    public CustomColor textColor = CustomColor.fromString("aaff00",1f);

    private static float exp = 0.0f;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!(visible = (getPlayerInfo().getExperiencePercentage() != -1 && !Reference.onLobby && mc.player.getAir() == 300))) return;
        if (OverlayConfig.Exp.INSTANCE.animated > 0.0f && OverlayConfig.Exp.INSTANCE.animated < 10.0f)
            exp -= (OverlayConfig.Exp.INSTANCE.animated * 0.1f) * (exp - getPlayerInfo().getExperiencePercentage());
        else
            exp = getPlayerInfo().getExperiencePercentage();
        if (ModCore.mc().player.getHorseJumpPower() > 0) exp = ModCore.mc().player.getHorseJumpPower();
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        switch (OverlayConfig.Exp.INSTANCE.expTexture) {
            case Wynn:
                drawDefaultBar(0, 5, 0, 9);
                break;
            case Liquid:
                drawDefaultBar(0, 5, 40, 49);
                break;
            case Emerald:
                drawDefaultBar(0, 5, 50, 59);
                break;
            case a: drawDefaultBar(0,5,10,19);
                break;
            case b: drawDefaultBar(0,5,20,29);
                break;
            case c: drawDefaultBar(0,5,30,39);
                break;
        }
    }

    private void drawDefaultBar(int y1, int y2, int ty1, int ty2) {
        drawProgressBar(Textures.Overlays.bars_exp,-91, y1, 91, y2, ty1, ty2, (flip ? -exp : exp));
        drawString(getPlayerInfo().getLevel() + "", textPositionOffset.a, textPositionOffset.b, textColor, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.Exp.INSTANCE.textShadow);
    }
}
