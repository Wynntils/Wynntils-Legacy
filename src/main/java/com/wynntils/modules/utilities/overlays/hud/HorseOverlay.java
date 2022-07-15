/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.core.framework.instances.data.HorseData;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import java.lang.Math;


public class HorseOverlay extends Overlay {

    public HorseOverlay() {
        super("Horses", 110, 42, true, 0.0f, 0.5f, 10, 80, OverlayGrowFrom.MIDDLE_LEFT, RenderGameOverlayEvent.ElementType.ALL);
    }

    @Override
    public void tick(ClientTickEvent event, long ticks) {
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        HorseData horse = get(HorseData.class);
        int horseInv = horse.getInventorySlot();
        double time_Required;
        if (((event.getType() == RenderGameOverlayEvent.ElementType.ALL) || (event.getType() == RenderGameOverlayEvent.ElementType.ALL)) && Reference.onWorld && horseInv >= 1 && horseInv <= 9 && OverlayConfig.HorseInfo.INSTANCE.enableHorseInfo) {
            if (horse.getLevel() != horse.getMaxLevel()) {
                double mlvl = 3.0 * horse.getLevel();

                double plvl = mlvl + 2;

                double dlvl = plvl / 6.0;

                double sxp = 100.0 - horse.getXp();

                double dxp = sxp / 100;

                double lvlxp = dlvl * dxp;

                double tenlvlxp = lvlxp * 10;

                double lvlxpceil = Math.ceil(tenlvlxp);

                time_Required = lvlxpceil / 10;
            }
            else {
                time_Required = 0;
            }
            String lvl = "Horse Level: " + horse.getLevel() + "/" + horse.getMaxLevel();
            String xp = "Horse Xp: " + horse.getXp();
            String b_Req = "Distance to next: " + time_Required + " Minutes";
            drawString(lvl, 0, -16, CommonColors.LIGHT_GREEN, SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.Leveling.INSTANCE.textShadow);
            drawString(xp, 0, -4, CommonColors.LIGHT_GREEN, SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.Leveling.INSTANCE.textShadow);
            drawString(b_Req, 0, 8, CommonColors.LIGHT_GREEN, SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.Leveling.INSTANCE.textShadow);
            staticSize.x = (int) getStringWidth(b_Req) + 10;
        }
    }

}
