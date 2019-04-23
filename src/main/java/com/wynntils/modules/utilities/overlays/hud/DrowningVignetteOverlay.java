/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class DrowningVignetteOverlay extends Overlay {

    public DrowningVignetteOverlay() {
        super("Drowning Vignette", 20, 20, true, 0, 0, 0, 0, null);
    }

    private static float animation = 300.0f;

    @Override
    public void render(RenderGameOverlayEvent.Post e) {
        if (!Reference.onWorld || !OverlayConfig.Bubbles.INSTANCE.drowningVignette || e.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        if(ScreenRenderer.mc.player.getAir() == 300 && animation >= 300) return;

        if(ScreenRenderer.mc.player.getAir() == 300) animation = Utils.easeOut(animation, 300, 1.5f, 20f);
        else animation = ScreenRenderer.mc.player.getAir();

        float value = Math.abs((animation / 300.0f) - 1.0f);

        GlStateManager.pushMatrix();
        {
            ScreenRenderer.transformationOrigin(0, 0);
            GlStateManager.color(0, 0.500f, 1, value);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.depthMask(false);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.disableAlpha();

            Textures.Masks.vignette.bind();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            {
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(0.0D, (double) ScreenRenderer.screen.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
                bufferbuilder.pos((double) ScreenRenderer.screen.getScaledWidth(), (double) ScreenRenderer.screen.getScaledHeight(), -90.0D).tex(1.0D, 1.0D).endVertex();
                bufferbuilder.pos((double) ScreenRenderer.screen.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
                bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
            }
            tessellator.draw();

            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
        GlStateManager.popMatrix();
    }
}
