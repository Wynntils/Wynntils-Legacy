/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.visual.entities;

import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.entities.instances.FakeEntity;
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

    // movement
    Location step = null;
    long nextChange = 0;

    public EntityFirefly(Location currentLocation) {
        super(currentLocation);

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

        { // setting up
            disableLighting();
            enableBlend();
            disableTexture2D();

            rotate(-render.playerViewY, 0f, 1f, 0f); // rotates yaw
            rotate((float) (thirdPerson ? -1 : 1) * render.playerViewX, 1.0F, 0.0F, 0.0F); // rotates pitch

            scale(VisualConfig.Fireflies.INSTANCE.scale, VisualConfig.Fireflies.INSTANCE.scale, VisualConfig.Fireflies.INSTANCE.scale);
        }

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buffer = tes.getBuffer();
        { // drawing
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

            // square
            buffer.pos(-.5, .5, 0).color(0f, 1f, 0, alpha).endVertex();
            buffer.pos(.5, .5, 0).color(0f, 1f, 0, alpha).endVertex();
            buffer.pos(.5, -.5, 0).color(0f, 1f, 0, alpha).endVertex();
            buffer.pos(-.5, -.5, 0).color(0f, 1f, 0, alpha).endVertex();

            tes.draw();
        }

        { // reset to default
            enableTexture2D();
        }
    }

    @Override
    public void remove() {
        super.remove();
        fireflies.decrementAndGet();
    }

}
