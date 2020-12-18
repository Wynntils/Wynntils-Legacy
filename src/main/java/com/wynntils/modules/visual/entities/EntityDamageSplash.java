/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.visual.entities;

import com.wynntils.core.framework.entities.instances.FakeEntity;
import com.wynntils.core.framework.enums.DamageType;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.visual.configs.VisualConfig;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

import static net.minecraft.client.renderer.GlStateManager.*;

public class EntityDamageSplash extends FakeEntity {

    private static final ScreenRenderer renderer = new ScreenRenderer();
    private static final WeakHashMap<String, Boolean> added = new WeakHashMap<>();

    String displayText;
    private float scale = 1f;

    public EntityDamageSplash(Map<DamageType, Integer> damages, Location currentLocation) {
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

        if (added.containsKey(displayText)) {
            remove();
            return;
        }

        added.put(displayText, true);
    }

    @Override
    public String getName() {
        return "EntityDamageSplash";
    }

    @Override
    public void tick(float partialTicks, Random r, EntityPlayerSP player) {
        int maxLiving = VisualConfig.DamageSplash.INSTANCE.maxLiving;
        if (livingTicks > maxLiving) {
            remove();
            return;
        }

        float initialScale = VisualConfig.DamageSplash.INSTANCE.initialScale;

        // makes the text goes down and resize
        currentLocation.subtract(0, 2 / (double)maxLiving, 0);
        scale = initialScale - ((livingTicks * initialScale) / maxLiving);
    }

    @Override
    public void render(float partialTicks, RenderGlobal context, RenderManager render) {
        boolean thirdPerson = render.options.thirdPersonView == 2;
        Location loc = getCurrentLocation();

        renderer.setRendering(true);
        {
            { // setting up
                rotate(-render.playerViewY, 0f, 1f, 0f); // rotates yaw
                rotate((float) (thirdPerson ? -1 : 1) * render.playerViewX, 1.0F, 0.0F, 0.0F); // rotates pitch
                scale(-0.025F, -0.025F, 0.025F); // size the text to the same size as a nametag

                scale(scale, scale, scale);

                color(1.0f, 1.0f, 1.0f, 1.0f);
            }

            renderer.drawString(displayText, 0, 0, CommonColors.WHITE,
                    SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
        }
        renderer.setRendering(false);
    }

}
