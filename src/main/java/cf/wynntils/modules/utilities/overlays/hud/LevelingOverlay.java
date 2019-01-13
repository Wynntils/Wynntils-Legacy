package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.enums.ClassType;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import java.text.DecimalFormat;

/**
 * Created by HeyZeer0 on 17/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class LevelingOverlay extends Overlay {

    public static final DecimalFormat GROUPED_FORMAT = new DecimalFormat("#,###");

    public LevelingOverlay() {
        super("Leveling Helper", 80, 10, true, 0.5f, 1.0f, 0, -58, OverlayGrowFrom.TOP_CENTRE);
    }

    @Setting.Features.StringParameters(parameters = {"actual", "max", "percent"})
    @Setting(displayName = "Current Text", description = "What will be showed at the Leveling Text")
    public String levelingText = "§a(%actual%/%max%) §6%percent%%";

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
                    .replace("%nextlvl%", getPlayerInfo().getLevel() == 101 ? "" : "" + (getPlayerInfo().getLevel() + 1));
            drawString(text, 0, 0, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.Leveling.INSTANCE.textShadow);
            staticSize.x = (int) getStringWidth(text);
        }
    }

}
