/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.utilities.entities;

import com.wynntils.core.framework.enums.DamageType;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.entities.instances.FakeEntity;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.client.renderer.GlStateManager.*;

public class DamageSplashEntity extends FakeEntity {

    private static final ScreenRenderer renderer = new ScreenRenderer();

    String displayText;

    public DamageSplashEntity(HashMap<DamageType, Integer> damages, Location currentLocation) {
        super(currentLocation);

        StringBuilder text = new StringBuilder();
        for (Map.Entry<DamageType, Integer> damage : damages.entrySet()) {
            text.append(damage.getKey().getColor());
            text.append(damage.getKey().getSymbol());
            text.append(" ");
            text.append(damage.getValue());
            text.append(" ");
        }

        displayText = text.toString();
        displayText = displayText.substring(0, displayText.length() -1);
    }

    @Override
    public String getName() {
        return "DamageSplashEntity";
    }

    @Override
    public void render(float partialTicks, RenderGlobal context, RenderManager render) {
        int maxLiving = UtilitiesConfig.DamageSplash.INSTANCE.maxLiving;
        // remove the damage after 150 rendering ticks
        if (livingTicks > maxLiving) {
            remove();
            return;
        }

        float initialScale = UtilitiesConfig.DamageSplash.INSTANCE.initialScale;

        // makes the text goes down and resize

        currentLocation.subtract(0, 2 / (double)maxLiving, 0);
        float scale = initialScale - ((livingTicks * initialScale) / maxLiving);

        boolean thirdPerson = render.options.thirdPersonView == 2;
        Location loc = getCurrentLocation();

        renderer.setRendering(true);
        {
            { // setting up
                rotate(-render.playerViewY, 0f, 1f, 0f); // rotates yaw
                rotate((float) (thirdPerson ? -1 : 1) * render.playerViewX, 1.0F, 0.0F, 0.0F); // rotates pitch
                scale(-0.025F, -0.025F, 0.025F); // size the text to the same size as a nametag

                scale(scale, scale, scale);

                disableLighting();
                alphaFunc(516, 0.1F);
                color(1.0f, 1.0f, 1.0f, 1.0f);
            }

            renderer.drawString(displayText, 0, 0, CommonColors.WHITE,
                    SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
        }
        renderer.setRendering(false);
    }

}
