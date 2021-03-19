/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.core.framework.entities;

import com.wynntils.core.framework.entities.instances.FakeEntity;
import com.wynntils.core.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static net.minecraft.client.renderer.GlStateManager.*;

public class EntityManager {

    private static final Set<FakeEntity> entityList = new HashSet<>();
    private static final Set<FakeEntity> toSpawn = new HashSet<>();

    /**
     * Spawns and register a fake entity into the world
     * This method is THREAD SAFE.
     *
     * @param entity the entity you want to register
     */
    public static void spawnEntity(FakeEntity entity) {
        toSpawn.add(entity);
    }

    /**
     * Removes every single FakeEntity from the world.
     * This method is THREAD SAFE.
     */
    public static void clearEntities() {
        entityList.forEach(FakeEntity::remove);
    }

    /**
     * Called on RenderWorldLastEvent, proccess the rendering queue
     */
    public static void tickEntities() {
        if (entityList.isEmpty() && toSpawn.isEmpty()) return;

        Minecraft.getMinecraft().profiler.startSection("fakeEntities");
        {
            // adds all new entities to the set
            Iterator<FakeEntity> it = toSpawn.iterator();
            while (it.hasNext()) {
                entityList.add(it.next());
                it.remove();
            }

            RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
            if (renderManager == null || renderManager.options == null) return;

            EntityPlayerSP player = Minecraft.getMinecraft().player;
            // ticks each entity
            it = entityList.iterator();
            while (it.hasNext()) {
                FakeEntity next = it.next();

                // remove marked entities
                if (next.toRemove()) {
                    it.remove();
                    continue;
                }

                Minecraft.getMinecraft().profiler.startSection(next.getName());
                { // render
                    next.livingTicks += 1;
                    next.tick(Utils.getRandom(), player);
                }
                Minecraft.getMinecraft().profiler.endSection();
            }
        }
        Minecraft.getMinecraft().profiler.endSection();
    }

    /**
     * Called on RenderWorldLastEvent, proccess the rendering queue
     *
     * @param partialTicks the world partial ticks
     * @param context the rendering context
     */
    public static void renderEntities(float partialTicks, RenderGlobal context) {
        if (entityList.isEmpty() && toSpawn.isEmpty()) return;

        Minecraft.getMinecraft().profiler.startSection("fakeEntities");
        {
            // adds all new entities to the set
            Iterator<FakeEntity> it = toSpawn.iterator();
            while (it.hasNext()) {
                entityList.add(it.next());
                it.remove();
            }

            RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
            if (renderManager == null || renderManager.options == null) return;

            EntityPlayerSP player = Minecraft.getMinecraft().player;
            // ticks each entity
            it = entityList.iterator();
            while (it.hasNext()) {
                FakeEntity next = it.next();

                Minecraft.getMinecraft().profiler.startSection(next.getName());
                {
                    pushMatrix();
                    {
                        next.preRender(partialTicks, context, renderManager);
                        // translates to the correctly entity position
                        // subtracting the viewer position offset
                        translate(
                                next.currentLocation.x - renderManager.viewerPosX,
                                next.currentLocation.y - renderManager.viewerPosY,
                                next.currentLocation.z - renderManager.viewerPosZ
                        );
                        next.render(partialTicks, context, renderManager);
                    }
                    popMatrix();
                }
                Minecraft.getMinecraft().profiler.endSection();
            }
        }
        Minecraft.getMinecraft().profiler.endSection();
    }

}
