/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.visual.entities.conditions;

import com.wynntils.core.framework.entities.instances.FakeEntity;
import com.wynntils.core.framework.entities.interfaces.EntitySpawnCodition;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.core.utils.objects.SquareRegion;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.modules.visual.entities.EntityFlame;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;

import java.util.Random;

public class FlameSpawnCondition implements EntitySpawnCodition {

    private static final SquareRegion ROOTS_OF_CORRUPTION = new SquareRegion(83, -1408, 360, -1222);

    @Override
    public boolean shouldSpawn(Location pos, World world, EntityPlayerSP player, Random random) {
        if (!ROOTS_OF_CORRUPTION.isInside(pos)) return false;

        // max distance
        double yDistance = Math.abs(pos.getY() - player.posY);
        if (yDistance > 0) return false;
        pos.subtract(0, random.nextInt(10), 0);

        return EntityFlame.flames.get() < VisualConfig.Flames.INSTANCE.spawnLimit
                && random.nextInt(VisualConfig.Snowflakes.INSTANCE.spawnRate) == 0;
    }

    @Override
    public FakeEntity createEntity(Location location, World world, EntityPlayerSP player, Random random) {
        return new EntityFlame(location, random);
    }

}
