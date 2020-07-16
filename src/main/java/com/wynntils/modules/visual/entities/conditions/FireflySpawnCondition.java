/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.visual.entities.conditions;

import com.wynntils.core.framework.entities.instances.FakeEntity;
import com.wynntils.core.framework.entities.interfaces.EntitySpawnCodition;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.modules.visual.entities.EntityFirefly;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;

import java.util.Random;

public class FireflySpawnCondition implements EntitySpawnCodition {

    @Override
    public boolean shouldSpawn(Location pos, World world, EntityPlayerSP player, Random random) {
        if (!VisualConfig.Fireflies.INSTANCE.enabled || world.getBiome(pos.toBlockPos()) != Biomes.FOREST) return false;

        return EntityFirefly.fireflies.get() < VisualConfig.Fireflies.INSTANCE.spawnLimit
                && random.nextInt(VisualConfig.Fireflies.INSTANCE.spawnRate) == 0;
    }

    @Override
    public FakeEntity createEntity(Location location, EntityPlayerSP player, Random random) {
        return new EntityFirefly(location);
    }

}
