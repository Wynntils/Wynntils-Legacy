/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.modules.utilities.configs.OverlayConfig;
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
        this.visible = this.getPlayerInfo().getXpNeededToLevelUp() != -1;
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (((event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) || (event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR)) && Reference.onWorld && getPlayerInfo().getCurrentClass() != ClassType.NONE) {
            String text = OverlayConfig.Leveling.INSTANCE.levelingText.replace("%actual%", "" + getPlayerInfo().getCurrentXP())
                    .replace("%max%", "" + getPlayerInfo().getXpNeededToLevelUp())
                    .replace("%percent%", getPlayerInfo().getCurrentXPAsPercentage())
                    .replace("%needed%", "" + (getPlayerInfo().getXpNeededToLevelUp() - getPlayerInfo().getCurrentXP()))
                    .replace("%actualg%", GROUPED_FORMAT.format(getPlayerInfo().getCurrentXP()))
                    .replace("%maxg%", GROUPED_FORMAT.format(getPlayerInfo().getXpNeededToLevelUp()))
                    .replace("%neededg%", GROUPED_FORMAT.format(getPlayerInfo().getXpNeededToLevelUp() - getPlayerInfo().getCurrentXP()))
                    .replace("%curlvl%", "" + getPlayerInfo().getLevel())
                    .replace("%nextlvl%", getPlayerInfo().getLevel() == 104 ? "" : "" + (getPlayerInfo().getLevel() + 1));
            drawString(text, 0, 0, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.Leveling.INSTANCE.textShadow);
            staticSize.x = (int) getStringWidth(text);
        }
    }

}
