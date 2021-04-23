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

public class EntitySnowFlake extends FakeEntity {

    public static AtomicInteger snowflakes = new AtomicInteger();

    int lifespan;
    float scale;
    float color;

    public EntitySnowFlake(Location currentLocation, Random r) {
        super(currentLocation);

        // adds a little Y offset
        currentLocation.add(0, r.nextInt(14), 0);

        lifespan = r.nextInt(VisualConfig.Snowflakes.INSTANCE.maxLiving);
        scale = VisualConfig.Snowflakes.INSTANCE.maxScale * r.nextFloat();
        color = VisualConfig.Snowflakes.INSTANCE.maxWhiteScale * r.nextFloat() + 0.1f;

        snowflakes.incrementAndGet();
    }

    @Override
    public void tick(Random r, EntityPlayerSP player) {
        if (livingTicks < lifespan) return;

        remove();
    }

    @Override
    public void render(float partialTicks, RenderGlobal context, RenderManager render) {
        float percentage = ((livingTicks + partialTicks) / (float) lifespan);
        float alpha = (1f - percentage);
        boolean thirdPerson = render.options.thirdPersonView == 2;

        { // setting up
            translate(0, -25 * percentage, 0);
            depthMask(false);
            enableBlend();
            enableAlpha();
            //disableTexture2D();
            color(1f, 1f, 1f, alpha);

            rotate(-render.playerViewY, 0f, 1f, 0f); // rotates yaw
            rotate((float) (thirdPerson ? -1 : 1) * render.playerViewX, 1.0F, 0.0F, 0.0F); // rotates pitch

            scale(scale, scale, scale);
        }

        Textures.Particles.snow.bind();

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buffer = tes.getBuffer();
        { // drawing
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

            buffer.pos(-.5,  .5, 0).tex(0, 1).color(color, color, color, alpha).endVertex();
            buffer.pos( .5,  .5, 0).tex(1, 1).color(color, color, color, alpha).endVertex();
            buffer.pos( .5, -.5, 0).tex(1, 0).color(color, color, color, alpha).endVertex();
            buffer.pos(-.5, -.5, 0).tex(0, 0).color(color, color, color, alpha).endVertex();

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

        snowflakes.decrementAndGet();
    }

}
