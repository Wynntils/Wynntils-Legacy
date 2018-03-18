package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.enums.ClassType;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.overlays.OverlayOption;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by HeyZeer0 on 17/03/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class LevelingOverlay extends Overlay {

    public LevelingOverlay() {
        super("Leveling Helper Overlay", 20, 20, true, 0.5f, 1.0f, 10, -38);
    }

    @OverlayOption.StringParameters(parameters = {"actual", "max", "percent"})
    @OverlayOption(displayName = "Current Text", description = "What will be showed at the Leveling Text")
    public String levelingText = "§a(%actual%/%max%) %percent%%";

    @OverlayOption(displayName = "Text Shadow", description = "The Levelling Text shadow type")
    public SmartFontRenderer.TextShadow shadow = SmartFontRenderer.TextShadow.OUTLINE;

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE && Reference.onWorld && getPlayerInfo().getCurrentClass() != ClassType.NONE) {
            String text = levelingText.replace("%actual%", "" + getPlayerInfo().getCurrentXP()).replace("%max%", "" + getPlayerInfo().getXpNeededToLevelUp()).replace("%percent%", getPlayerInfo().getCurrentXPAsPercentage());
            drawString(text, 0, -20, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, shadow);
        }
    }

}
