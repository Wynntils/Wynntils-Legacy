/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.managers.LevelingManager;
import io.netty.util.internal.StringUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import java.text.DecimalFormat;

public class LevelingOverlay extends Overlay {

    public static final DecimalFormat GROUPED_FORMAT = new DecimalFormat("#,###");

    public LevelingOverlay() {
        super("Leveling Helper", 80, 10, true, 0.5f, 1.0f, 0, -58, OverlayGrowFrom.TOP_CENTRE);
    }

    @Setting.Features.StringParameters(parameters = {"actual", "max", "percent"})
    @Setting(displayName = "Current Text", description = "What will be showed at the Leveling Text")
    public String levelingText = TextFormatting.GREEN + "(%actual%/%max%) " + TextFormatting.GOLD + "%percent%%";

    @Setting(displayName = "Text Shadow", description = "The Levelling Text shadow type")
    public SmartFontRenderer.TextShadow shadow = SmartFontRenderer.TextShadow.OUTLINE;

    @Override
    public void tick(ClientTickEvent event, long ticks) {
        this.visible = get(CharacterData.class).getXpNeededToLevelUp() != -1;
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        CharacterData data = get(CharacterData.class);
        if (((event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) || (event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR)) && Reference.onWorld && data.isLoaded()) {
            String text = OverlayConfig.Leveling.INSTANCE.levelingText.replace("%actual%", "" + data.getCurrentXP())
                    .replace("%max%", "" + data.getXpNeededToLevelUp())
                    .replace("%percent%", data.getCurrentXPAsPercentage())
                    .replace("%needed%", "" + (data.getXpNeededToLevelUp() - data.getCurrentXP()))
                    .replace("%actualg%", GROUPED_FORMAT.format(data.getCurrentXP()))
                    .replace("%maxg%", GROUPED_FORMAT.format(data.getXpNeededToLevelUp()))
                    .replace("%neededg%", GROUPED_FORMAT.format(data.getXpNeededToLevelUp() - data.getCurrentXP()))
                    .replace("%curlvl%", "" + data.getLevel())
                    .replace("%nextlvl%", data.getLevel() == 104 ? "" : "" + (data.getLevel() + 1))
                    .replace("%grindtime%", StringUtils.durationIntegerToShortString(LevelingManager.getLevelingGrindTime()));

            drawString(text, 0, 0, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.Leveling.INSTANCE.textShadow);
            staticSize.x = (int) getStringWidth(text);
        }
    }

}
