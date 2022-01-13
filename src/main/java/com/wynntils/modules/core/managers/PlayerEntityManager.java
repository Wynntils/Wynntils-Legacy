/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.core.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;

public class PlayerEntityManager {
    private static Map<UUID, EntityPlayer> map = new HashMap<>();

    /**
     * @param uuid UUID of player
     * @return The {@link EntityPlayer} with the given uuid, or null if no such player exists
     */
    public static EntityPlayer getPlayerByUUID(UUID uuid) {
        return map.get(uuid);
    }

    /**
     * @param uuid UUID of player
     * @return If true, {@link #getPlayerByUUID(UUID)} will not return null.
     */
    public static boolean containsUUID(UUID uuid) {
        return map.containsKey(uuid);
    }

    static void onPlayerJoin(EntityPlayer e) {
        map.put(e.getUniqueID(), e);
    }

    static void onPlayerLeave(EntityPlayer e) {
        map.remove(e.getUniqueID());
    }

    public static void onWorldLoad(World w) {
        w.addEventListener(new Listener());
    }

    public static void onWorldUnload() {
        map.clear();
    }

    private static class Listener implements IWorldEventListener {

        @Override public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) { }
        @Override public void notifyLightSet(BlockPos pos) { }
        @Override public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) { }
        @Override public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) { }
        @Override public void playRecord(SoundEvent soundIn, BlockPos pos) { }
        @Override public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) { }
        @Override public void spawnParticle(int id, boolean ignoreRange, boolean minimiseParticleLevel, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters) { }
        @Override public void broadcastSound(int soundID, BlockPos pos, int data) { }
        @Override public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) { }
        @Override public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) { }

        @Override
        public void onEntityAdded(Entity entityIn) {
            if (entityIn instanceof EntityPlayer) {
                onPlayerJoin((EntityPlayer) entityIn);
            }
        }

        @Override
        public void onEntityRemoved(Entity entityIn) {
            if (entityIn instanceof EntityPlayer) {
                onPlayerLeave((EntityPlayer) entityIn);
            }
        }

    }
}
