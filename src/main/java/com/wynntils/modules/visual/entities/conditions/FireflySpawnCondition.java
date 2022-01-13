/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.visual.entities.conditions;

import com.wynntils.core.framework.entities.instances.FakeEntity;
import com.wynntils.core.framework.entities.interfaces.EntitySpawnCodition;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.core.utils.objects.SquareRegion;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.modules.visual.entities.EntityFirefly;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class FireflySpawnCondition implements EntitySpawnCodition {

    private static final SquareRegion LIGHT_FOREST = new SquareRegion(-1304, -5088, -560, -4426);
    private static final SquareRegion DARK_FOREST = new SquareRegion(-1433, -5613, -938, -5099);

    @Override
    public boolean shouldSpawn(Location pos, World world, EntityPlayerSP player, Random random) {
        if (!VisualConfig.Fireflies.INSTANCE.enabled) return false;

        BlockPos block = pos.toBlockPos();
        if (world.getBiome(block) != Biomes.FOREST && world.getBiome(block) != Biomes.SWAMPLAND) return false;
        if (!LIGHT_FOREST.isInside(pos) && !DARK_FOREST.isInside(pos)) return false;

        // Night starts at 12542 and ends at 23031
        long worldTime = world.getWorldTime() % 24000;
        if (worldTime < 12542 || worldTime > 23031) return false;

        return EntityFirefly.fireflies.get() < VisualConfig.Fireflies.INSTANCE.spawnLimit
                && random.nextInt(VisualConfig.Fireflies.INSTANCE.spawnRate) == 0;
    }

    @Override
    public FakeEntity createEntity(Location location, World world, EntityPlayerSP player, Random random) {
        float r, g, b;
        if (world.getBiome(location.toBlockPos()) == Biomes.SWAMPLAND) {
            r = 0.29f; g = 0f; b = 0.5f; // dark firefly
        } else {
            r = 1f; g = 1f; b = 0f; // light firefly
        }

        return new EntityFirefly(location, r, g, b);
    }

}
