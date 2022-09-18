package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.instances.ShamanMaskType;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrentMaskOverlay extends Overlay {
    public CurrentMaskOverlay() {
        super("Current Shaman Mask Display", 66, 10, true, 0.66f, 1f, -10, -38, OverlayGrowFrom.BOTTOM_RIGHT);
    }

    @Setting(displayName = "Text Position", description = "The position offset of the text")
    public Pair<Integer, Integer> textPositionOffset = new Pair<>(-40, -10);

    private static final Pattern SINGLE_PATTERN = Pattern.compile("§cMask of the (Coward|Lunatic|Fanatic)§r");

    public void onTitle(PacketEvent<SPacketTitle> e) {
        if (e.getPacket() == null || e.getPacket().getMessage() == null) return;

        String title = e.getPacket().getMessage().getFormattedText();
        if (title.contains("Mask of the ") || title.contains("➤")) {
            parseMask(title);
            if (OverlayConfig.MaskOverlay.INSTANCE.hideSwitchingTitle) e.setCanceled(true);
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!visible) return;
        if (get(CharacterData.class).getCurrentClass() != ClassType.SHAMAN) return;
        ShamanMaskType currentMask = get(CharacterData.class).getCurrentShamanMask();
        String text = currentMask.getText();
        drawString(text, textPositionOffset.a, textPositionOffset.b, CustomColor.fromTextFormatting(currentMask.getColor()), SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.MaskOverlay.INSTANCE.textShadow);
    }

    private void parseMask(String title) {
        Matcher matcher = SINGLE_PATTERN.matcher(title);

        ShamanMaskType currentMask = ShamanMaskType.NONE;
        if(title.contains("Awakened")) currentMask = ShamanMaskType.AWAKENED;
        else if (title.contains("§cL")) currentMask = ShamanMaskType.LUNATIC;
        else if (title.contains("§6F")) currentMask = ShamanMaskType.FANATIC;
        else if (title.contains("§bC")) currentMask = ShamanMaskType.COWARD;
        else if (title.startsWith("§8")) currentMask = ShamanMaskType.NONE;
        else if (matcher.matches()) currentMask = ShamanMaskType.find(matcher.group(1));

        get(CharacterData.class).setCurrentShamanMask(currentMask);
    }


}
