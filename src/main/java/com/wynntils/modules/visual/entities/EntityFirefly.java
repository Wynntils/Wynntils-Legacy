/*
 *  * Copyright © Wynntils - 2020.
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

public class EntityFirefly extends FakeEntity {

    public static AtomicInteger fireflies = new AtomicInteger();

    int lifespan;
    float r, g, b;

    // movement
    Location step = null;
    long nextChange = 0;

    public EntityFirefly(Location currentLocation, float r, float g, float b) {
        super(currentLocation);

        this.r = r;
        this.g = g;
        this.b = b;

        lifespan = VisualConfig.Fireflies.INSTANCE.maxLiving;
        fireflies.incrementAndGet();
    }

    @Override
    public String getName() {
        return "EntityFirefly";
    }

    private void generateNextGoal(Random r) {
        if (livingTicks < nextChange) return;

        // generates a random location with a 3 blocks radius based on the last one
        Location nextGoal = currentLocation.clone().add(
                r.nextInt(3) * (r.nextBoolean() ? 1 : -1),
                r.nextInt(3) * (r.nextBoolean() ? 1 : -1),
                r.nextInt(3) * (r.nextBoolean() ? 1 : -1));

        // randomize the movement velocity
        double velocity = 0.0008 * r.nextDouble();

        // calculate the difference and how much the particle should move by each tick
        step = nextGoal.clone().subtract(currentLocation).multiply(velocity);

        // generate a random age until the next goal
        nextChange = livingTicks + r.nextInt(VisualConfig.Fireflies.INSTANCE.maxGoal);
    }

    @Override
    public void tick(float partialTicks, Random r, EntityPlayerSP player) {
        if (livingTicks > lifespan) { // verifies if the entity should die
            remove();
            return;
        }

        // generates the next goal if needed and apply the step
        generateNextGoal(r);
        currentLocation.add(step);
    }

    @Override
    public void render(float partialTicks, RenderGlobal context, RenderManager render) {
        float alpha = (1 - (livingTicks / (float)lifespan));
        boolean thirdPerson = render.options.thirdPersonView == 2;

        boolean threeDimensions = VisualConfig.Fireflies.INSTANCE.threeDimensions;

        { // setting up
            depthMask(false);
            enableBlend();
            enableAlpha();
            disableTexture2D();
            color(1f, 1f, 1f, alpha);

            if (!threeDimensions) {
                rotate(-render.playerViewY, 0f, 1f, 0f); // rotates yaw
                rotate((float) (thirdPerson ? -1 : 1) * render.playerViewX, 1.0F, 0.0F, 0.0F); // rotates pitch
            }

            scale(VisualConfig.Fireflies.INSTANCE.scale, VisualConfig.Fireflies.INSTANCE.scale, VisualConfig.Fireflies.INSTANCE.scale);
        }

        // the reason we're applying a texture is because optifine shaders
        // needs a texture to render any tesselated object
        Textures.World.solid_color.bind();

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buffer = tes.getBuffer();
        { // drawing
            buffer.begin(threeDimensions ? GL11.GL_TRIANGLE_STRIP : GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

            // vertexes
            if (!threeDimensions) {
                buffer.pos(-.5, .5, 0).tex(0, 1f).color(r, g, b, alpha).lightmap(0, 15728880).endVertex();
                buffer.pos(.5, .5, 0).tex(1f, 1f).color(r, g, b, alpha).lightmap(0, 15728880).endVertex();
                buffer.pos(.5, -.5, 0).tex(1f, 0f).color(r, g, b, alpha).lightmap(0, 15728880).endVertex();
                buffer.pos(-.5, -.5, 0).tex(0, 0f).color(r, g, b, alpha).lightmap(0, 15728880).endVertex();
            } else generateVertexBox(buffer, -.5, -.5, -.5, .5, .5, .5, r, g, b, alpha);

            tes.draw();
        }

        { // reset to default
            disableBlend();
            enableTexture2D();
            depthMask(true);
            color(1f, 1f, 1f, 1f);
        }
    }

    private void generateVertexBox(BufferBuilder builder, double x1, double y1, double z1, double x2, double y2, double z2, float red, float green, float blue, float alpha) {
        builder.pos(x1, y1, z1).tex(0f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x1, y1, z1).tex(0f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x1, y1, z1).tex(0f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x1, y1, z2).tex(0f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();

        builder.pos(x1, y2, z1).tex(0f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x1, y2, z2).tex(0f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x1, y2, z2).tex(0f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x1, y1, z2).tex(0f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();

        builder.pos(x2, y2, z2).tex(1f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x2, y1, z2).tex(1f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x2, y1, z2).tex(1f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x2, y1, z1).tex(1f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();

        builder.pos(x2, y2, z2).tex(1f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x2, y2, z1).tex(1f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x2, y2, z1).tex(1f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x2, y1, z1).tex(1f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();

        builder.pos(x1, y2, z1).tex(0f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x1, y1, z1).tex(0f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x1, y1, z1).tex(0f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x2, y1, z1).tex(1f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();

        builder.pos(x1, y1, z2).tex(0f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x2, y1, z2).tex(1f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x2, y1, z2).tex(1f, 0f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x1, y2, z1).tex(0f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();

        builder.pos(x1, y2, z1).tex(0f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x1, y2, z2).tex(0f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x2, y2, z1).tex(1f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x2, y2, z2).tex(1f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();

        builder.pos(x2, y2, z2).tex(1f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
        builder.pos(x2, y2, z2).tex(1f, 1f).color(red, green, blue, alpha).lightmap(0, 15728880).endVertex();
    }

    @Override
    public void remove() {
        super.remove();
        fireflies.decrementAndGet();
    }

}
