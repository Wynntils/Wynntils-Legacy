/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.visual.entities.conditions;

import java.util.Random;

import com.wynntils.core.framework.entities.instances.FakeEntity;
import com.wynntils.core.framework.entities.interfaces.EntitySpawnCodition;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.core.utils.objects.SquareRegion;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.modules.visual.entities.EntityAsh;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;

public class AshSpawnCondition implements EntitySpawnCodition {

    private static final SquareRegion MOLTEN_HEIGHTS = new SquareRegion(1035, -5647, 1700, -4931);

    @Override
    public boolean shouldSpawn(Location pos, World world, EntityPlayerSP player, Random random) {
        if (!VisualConfig.Ashes.INSTANCE.enabled) return false;
        if (!MOLTEN_HEIGHTS.isInside(pos)) return false;

        double yDistance = Math.abs(pos.clone().subtract(new Location(player)).getY());
        if (yDistance < 5) return false;

        return EntityAsh.ashes.get() < VisualConfig.Ashes.INSTANCE.spawnLimit
                && random.nextInt(VisualConfig.Ashes.INSTANCE.spawnRate) == 0;
    }

    @Override
    public FakeEntity createEntity(Location location, World world, EntityPlayerSP player, Random random) {
        return new EntityAsh(location, random);
    }

}
