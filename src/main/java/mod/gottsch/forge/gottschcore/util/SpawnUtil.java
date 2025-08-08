/*
 * This file is part of  GottschCore.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
 *
 * All rights reserved.
 *
 * GottschCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GottschCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GottschCore.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.gottschcore.util;


import mod.gottsch.neo.gottschcore.spatial.Coords;
import mod.gottsch.neo.gottschcore.spatial.ICoords;
import mod.gottsch.neo.gottschcore.world.WorldInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * @author by Mark Gottschling on 5/14/2025
 */
public class SpawnUtil {

    /**
     * NOTE this method spawns the mob, but does NOT add the mob to the world.
     *
     * @param level
     * @param random
     * @param entityType
     * @param mob
     * @param coords
     * @return
     */
    public static Optional<Mob> spawnMob(ServerLevel level, RandomSource random, EntityType<? extends Mob> entityType, Entity mob, ICoords coords) {
        return spawnMob(level, random, entityType, MobSpawnType.TRIGGERED, level.getCurrentDifficultyAt(coords.toPos()), coords);
    }

    public static Optional<Mob> spawnMob(ServerLevel level, RandomSource random, EntityType<? extends Mob> entityType, MobSpawnType spawnType, DifficultyInstance difficulty, ICoords coords) {

        // 20 tries
        for (int i = 0; i < 20; i++) {
            int spawnX = coords.getX() + Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
            int spawnY = coords.getY() + Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
            int spawnZ = coords.getZ() + Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
            ICoords spawnCoords = Coords.of(spawnX, spawnY, spawnZ);

            if (!WorldInfo.isClientSide(level)) {
                SpawnPlacementType placement = SpawnPlacements.getPlacementType(entityType);
                BlockState state = level.getBlockState(spawnCoords.toPos());
                if (NaturalSpawner.isValidEmptySpawnBlock(level, spawnCoords.toPos(), state, state.getFluidState(), entityType)) {
                    Mob mob = entityType.create(level);
                    mob.setPos((double)spawnX, (double)spawnY, (double)spawnZ);
                    level.addFreshEntityWithPassengers(mob);
                    return Optional.of(mob);
                }
            }
        }
        return Optional.empty();
    }


    // convenience method to spawn and add the mob to the world.
    public static Optional<Mob> spawnAndAddMob(ServerLevel level, RandomSource random, EntityType<? extends Mob> entityType, Entity mob, ICoords coords) {
        Optional<Mob> optionalMob =  SpawnUtil.spawnMob(level, random, entityType, MobSpawnType.TRIGGERED, level.getCurrentDifficultyAt(coords.toPos()), coords);
        if (optionalMob.isPresent()) {
            level.addFreshEntityWithPassengers(mob);
        }
        return optionalMob;
    }

}

