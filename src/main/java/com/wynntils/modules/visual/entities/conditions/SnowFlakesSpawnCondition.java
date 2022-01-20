/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.visual.entities.conditions;

import com.wynntils.core.framework.entities.instances.FakeEntity;
import com.wynntils.core.framework.entities.interfaces.EntitySpawnCodition;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.core.utils.objects.SquareRegion;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.modules.visual.entities.EntitySnowFlake;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class SnowFlakesSpawnCondition implements EntitySpawnCodition {

    private static final SquareRegion NESAAK = new SquareRegion(-458, -976, 343, -635);
    private static final SquareRegion LUSUCO = new SquareRegion(-492, -550, -66, -127);

    @Override
    public boolean shouldSpawn(Location pos, World world, EntityPlayerSP player, Random random) {
        if (!VisualConfig.Snowflakes.INSTANCE.enabled) return false;

        Biome biome = world.getBiome(pos.toBlockPos());

        // biome check
        // nesaak is TAIGA and lusuco is PLAINS
        boolean nesaak = NESAAK.isInside(pos);
        if (!nesaak && !LUSUCO.isInside(pos)) return false;
        if (biome != (nesaak ? Biomes.TAIGA : Biomes.PLAINS)) return false;

        // max distance
        double yDistance = Math.abs(pos.clone().subtract(new Location(player)).getY());
        if (yDistance < 3) return false;

        return EntitySnowFlake.snowflakes.get() < VisualConfig.Snowflakes.INSTANCE.spawnLimit
                && random.nextInt(VisualConfig.Snowflakes.INSTANCE.spawnRate) == 0;
    }

    @Override
    public FakeEntity createEntity(Location location, World world, EntityPlayerSP player, Random random) {
        return new EntitySnowFlake(location, random);
    }

}
