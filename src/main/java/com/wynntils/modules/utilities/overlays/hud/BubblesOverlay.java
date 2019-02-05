/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.hud;

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

public class BubblesOverlay extends Overlay {

    public BubblesOverlay() {
        super("Bubbles Overlay", 182, 7, true, 0.5f, 1.0f, 0, -29, OverlayGrowFrom.MIDDLE_CENTRE, RenderGameOverlayEvent.ElementType.EXPERIENCE, RenderGameOverlayEvent.ElementType.JUMPBAR);
    }

    @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
    @Setting(displayName = "Animation Speed",description = "How fast should the bar changes happen (0 for instant)")
    public float animated = 2f;

    @Setting(displayName = "Flip", description = "Should the filling of the bar be flipped")
    public boolean flip = false;

    @Setting(displayName = "Level Number Position", description = "The position offset of the level number")
    public Pair<Integer,Integer> textPositionOffset = new Pair<>(0,-6);

    @Setting(displayName = "Text Name", description = "The color of the text")
    public CustomColor textColor = CustomColor.fromString("6aabf5",1f);

    private static float amount = 0.0f;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!(visible = (mc.player.getAir() != 300 && !Reference.onLobby))) return;
        if (OverlayConfig.Bubbles.INSTANCE.animated > 0.0f && OverlayConfig.Bubbles.INSTANCE.animated < 10.0f && !(amount >= 300))
            amount -= (OverlayConfig.Bubbles.INSTANCE.animated * 0.1f) * (amount - mc.player.getAir());
        else amount = getPlayerInfo().getCurrentHealth();

        if(amount <= 0) amount = 0;
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if(!Reference.onWorld) return;

        switch (OverlayConfig.Bubbles.INSTANCE.bubblesTexture) {
            case Wynn:
                drawDefaultBar(0, 5, 0, 9);
                break;
            case Liquid:
                drawDefaultBar(0, 5, 40, 49);
                break;
            case Saphire:
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
        drawProgressBar(Textures.Overlays.bars_bubbles,-91, y1, 91, y2, ty1, ty2, (flip ? -amount : amount) / 300);
        drawString((mc.player.getAir() / 3 <= 0 ? 0 : mc.player.getAir()/3) + "", textPositionOffset.a, textPositionOffset.b, textColor, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.Exp.INSTANCE.textShadow);
    }

}
