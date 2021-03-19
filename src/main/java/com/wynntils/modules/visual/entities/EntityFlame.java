/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.visual.entities;

import com.wynntils.core.framework.entities.instances.FakeEntity;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.visual.configs.VisualConfig;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minecraft.client.renderer.GlStateManager.*;

public class EntityFlame extends FakeEntity {

    public static AtomicInteger flames = new AtomicInteger();

    float lifespan;
    float scale;

    public EntityFlame(Location currentLocation, Random r) {
        super(currentLocation);

        lifespan = VisualConfig.Flames.INSTANCE.maxLiving * 0.1f;
        scale = VisualConfig.Flames.INSTANCE.maxScale * r.nextFloat();

        flames.incrementAndGet();
    }

    @Override
    public void tick(Random r, EntityPlayerSP player) {
        if (livingTicks < lifespan) return;

        remove();
    }

    @Override
    public void render(float partialTicks, RenderGlobal context, RenderManager render) {
        float percentage = ((livingTicks + partialTicks) / lifespan);

        float alpha = (1f - percentage);
        boolean thirdPerson = render.options.thirdPersonView == 2;

        { // setting up rotation
            translate(0, 10 * percentage * (10 * percentage), 0);
            depthMask(false);
            enableBlend();
            enableAlpha();
            //disableTexture2D();
            color(1f, 1f, 1f, alpha);

            rotate(-render.playerViewY, 0f, 1f, 0f); // rotates yaw
            rotate((float) (thirdPerson ? -1 : 1) * render.playerViewX, 1.0F, 0.0F, 0.0F); // rotates pitch

            scale(scale, scale, scale);
        }

        Textures.Particles.flame.bind();

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buffer = tes.getBuffer();
        { // initial cube
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

            buffer.pos(-.5,  3, 0).tex(0, 1).color(1f, 1f, 1f, alpha).endVertex();
            buffer.pos( .5,  3, 0).tex(1, 1).color(1f, 1f, 1f, alpha).endVertex();
            buffer.pos( .5, -.0, 0).tex(1, 0).color(1f, 1f, 1f, alpha).endVertex();
            buffer.pos(-.5, -.0, 0).tex(0, 0).color(1f, 1f, 1f, alpha).endVertex();

            tes.draw();
        }

        { // reset to default
            disableBlend();
            enableTexture2D();
            depthMask(true);
            color(1f, 1f, 1f, 1f);
        }
    }

    @Override
    public void remove() {
        super.remove();

        flames.decrementAndGet();
    }

}
