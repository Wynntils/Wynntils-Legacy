package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.instances.ShamanMaskType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class CurrentMaskOverlay extends Overlay {
    public CurrentMaskOverlay() {
        super("Current Shaman Mask Display", 66, 10, true, 0.66f, 1f, -10, -38, OverlayGrowFrom.BOTTOM_RIGHT);
    }

    @Setting(displayName = "Text Position", description = "The position offset of the text")
    public Pair<Integer, Integer> textPositionOffset = new Pair<>(-40, -10);

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!visible) return;
        if (get(CharacterData.class).getCurrentClass() != ClassType.SHAMAN) return;
        ShamanMaskType currentMask = get(CharacterData.class).getCurrentShamanMask();
        String text = currentMask.getText();
        drawString(text, textPositionOffset.a, textPositionOffset.b, CustomColor.fromTextFormatting(currentMask.getColor()), SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.MaskOverlay.INSTANCE.textShadow);
    }

}
