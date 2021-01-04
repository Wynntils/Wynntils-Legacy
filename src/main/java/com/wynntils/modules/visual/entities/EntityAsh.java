/*
 *  * Copyright © Wynntils - 2018 - 2021.
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

public class EntityAsh extends FakeEntity {

    public static AtomicInteger ashes = new AtomicInteger();

    int lifespan;
    float scale;
    float color;

    double sinDegrees, cosDegrees;

    public EntityAsh(Location currentLocation, Random r) {
        super(currentLocation);

        // adds a little Y offset
        currentLocation.add(0, r.nextInt(14), 0);

        lifespan = r.nextInt(VisualConfig.Ashes.INSTANCE.maxLiving);
        scale = VisualConfig.Ashes.INSTANCE.maxScale * r.nextFloat();
        color = VisualConfig.Ashes.INSTANCE.maxGrayScale * r.nextFloat();

        // generates a random sin and cossine offset
        sinDegrees = r.nextInt(360);
        cosDegrees = r.nextInt(360);

        ashes.incrementAndGet();
    }

    @Override
    public void tick(float partialTicks, Random r, EntityPlayerSP player) {
        if (livingTicks > lifespan) { // verifies if the entity should die
            remove();
            return;
        }

        if (sinDegrees + 1 > 360) sinDegrees = 0;
        sinDegrees++;

        if (cosDegrees + 1 > 360) cosDegrees = 0;
        cosDegrees++;

        currentLocation.add(0.01 * Math.cos(Math.toRadians(cosDegrees)), -0.01, 0.01 * Math.sin(Math.toRadians(sinDegrees)));
    }

    @Override
    public void render(float partialTicks, RenderGlobal context, RenderManager render) {
        float alpha = (1 - (livingTicks / (float)lifespan));
        boolean thirdPerson = render.options.thirdPersonView == 2;

        { // setting up
            depthMask(false);
            enableBlend();
            enableAlpha();
            disableTexture2D();
            color(1f, 1f, 1f, alpha);

            rotate(-render.playerViewY, 0f, 1f, 0f); // rotates yaw
            rotate((float) (thirdPerson ? -1 : 1) * render.playerViewX, 1.0F, 0.0F, 0.0F); // rotates pitch

            scale(scale, scale, scale);
        }

        // the reason we're applying a texture is because optifine shaders
        // needs a texture to render any tesselated object
        Textures.World.solid_color.bind();

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buffer = tes.getBuffer();
        { // drawing
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

            buffer.pos(-.5, .5, 0).tex(0, 1f).color(color, color, color, alpha).lightmap(0, 15728880).endVertex();
            buffer.pos(.5, .5, 0).tex(1f, 1f).color(color, color, color, alpha).lightmap(0, 15728880).endVertex();
            buffer.pos(.5, -.5, 0).tex(1f, 0f).color(color, color, color, alpha).lightmap(0, 15728880).endVertex();
            buffer.pos(-.5, -.5, 0).tex(0, 0f).color(color, color, color, alpha).lightmap(0, 15728880).endVertex();

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

        ashes.decrementAndGet();
    }

}
