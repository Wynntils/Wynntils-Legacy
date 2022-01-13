/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.entities.interfaces;

import java.util.Random;

import com.wynntils.core.framework.entities.instances.FakeEntity;
import com.wynntils.core.utils.objects.Location;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;

public interface EntitySpawnCodition {

    boolean shouldSpawn(Location pos, World world, EntityPlayerSP player, Random random);
    FakeEntity createEntity(Location location, World world, EntityPlayerSP player, Random random);

}
