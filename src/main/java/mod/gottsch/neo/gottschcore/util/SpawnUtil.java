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
package mod.gottsch.neo.gottschcore.util;

import mod.gottsch.neo.gottschcore.spatial.ICoords;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.neoforged.neoforge.event.EventHooks;

import java.util.Optional;

/**
 * @author by Mark Gottschling on 5/14/2025
 */
public class SpawnUtil {

    private static final int MAX_SPAWN_TRIES = 20;

    /**
     * @deprecated the {@code mob} argument was never used; call
     *             {@link #spawnMob(ServerLevel, RandomSource, EntityType, ICoords)}.
     */
    @Deprecated(forRemoval = true, since = "2.6.0")
    public static Optional<? extends LivingEntity> spawnMob(ServerLevel level, RandomSource random, EntityType<? extends LivingEntity> entityType, Entity mob, ICoords coords) {
        return spawnMob(level, random, entityType, coords);
    }

    /**
     * Creates, positions and finalizes a mob at a valid position near {@code coords},
     * but does NOT add it to the world. Returning the (un-added) mob gives callers
     * the opportunity to modify it -- e.g. alter attributes, equipment or NBT -- before
     * adding it via {@link ServerLevel#addFreshEntityWithPassengers(Entity)}.
     * Use {@link #spawnAndAddMob} to create and add in a single step.
     *
     * <p>Uses {@link MobSpawnType#TRIGGERED} and the difficulty at {@code coords}.
     *
     * @param level the server level
     * @param random the random source
     * @param entityType the type of mob to create
     * @param coords the target coordinates to spawn near
     * @return the created (positioned, finalized, un-added) mob, or empty if no valid position was found
     */
    public static Optional<? extends LivingEntity> spawnMob(ServerLevel level, RandomSource random, EntityType<? extends LivingEntity> entityType, ICoords coords) {
        return spawnMob(level, random, entityType, MobSpawnType.TRIGGERED, level.getCurrentDifficultyAt(coords.toPos()), coords);
    }

    /**
     * Creates, positions and finalizes a mob at a valid position near {@code coords},
     * but does NOT add it to the world. See {@link #spawnMob(ServerLevel, RandomSource, EntityType, ICoords)}.
     *
     * @param spawnType the spawn type passed to the finalize-spawn event
     * @param difficulty the local difficulty used to finalize the spawn
     */
    public static Optional<? extends LivingEntity> spawnMob(ServerLevel level, RandomSource random, EntityType<? extends LivingEntity> entityType, MobSpawnType spawnType, DifficultyInstance difficulty, ICoords coords) {
        if (level.isClientSide()) {
            return Optional.empty();
        }

        final SpawnPlacementType placementType = SpawnPlacements.getPlacementType(entityType);

        for (int i = 0; i < MAX_SPAWN_TRIES; i++) {
            // generate random offset coordinates
            int offsetX = Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
            int offsetY = Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
            int offsetZ = Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);

            BlockPos spawnPos = coords.toPos().offset(offsetX, offsetY, offsetZ);

            // skip invalid positions and try again
            if (!placementType.isSpawnPositionOk(level, spawnPos, entityType)) {
                continue;
            }

            LivingEntity mob = entityType.create(level);
            if (mob == null) {
                continue;
            }
            // center on the block for x/z
            mob.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);

            // isSpawnPositionOk only tests spawnPos and spawnPos.above() for a full/suffocating
            // block -- a single-BlockPos heuristic. It says nothing about whether THIS mob's real
            // bounding box (which can be wider than one block, or just off-center against a
            // column/pilaster/corner) actually clips solid geometry once positioned. Vanilla's own
            // BaseSpawner guards exactly this with checkSpawnObstruction before adding the mob;
            // without it here, a mob can be created, added and left overlapping a wall, taking
            // suffocation damage until it dies -- intermittently, since it only bites on the
            // offsets that land against decorative geometry the single-block check let through.
            if (mob instanceof Mob checkedMob && !checkedMob.checkSpawnObstruction(level)) {
                mob.discard();
                continue;
            }

            if (mob instanceof Mob finalizing) {
                // Allow the mob (and other mods) to finalize the spawn: equipment, difficulty
                // scaling, etc. Called for its SIDE EFFECTS; the return value is deliberately
                // ignored.
                //
                // DO NOT reinstate a null check here. The returned SpawnGroupData is whatever was
                // passed IN (here null) for most mobs, so an ordinary, uncancelled spawn returns
                // null. Treating that as cancellation discarded the mob on all MAX_SPAWN_TRIES
                // attempts and made spawnMob return empty: proximity spawners appeared to fire and
                // spawn nothing. NeoForge's own javadoc on EventHooks.finalizeMobSpawn says both
                // halves of this: "The return value of this method has no bearing on if the entity
                // will be spawned" and "Callers do not need to check if the entity's spawn was
                // cancelled, as the spawn will be blocked by Forge."
                //
                // Found 2026-08-16 from Dungeons2 on the Forge 1.20.1 build; the NeoForge build
                // had the same check on mob.finalizeSpawn's return.
                EventHooks.finalizeMobSpawn(finalizing, level, difficulty, spawnType, null);
            }
            return Optional.of(mob);
        }
        return Optional.empty();
    }

    /**
     * Convenience method that creates a mob via {@link #spawnMob(ServerLevel, RandomSource, EntityType, ICoords)}
     * and adds it (and any passengers) to the world.
     *
     * @return the mob that was added, or empty if no valid position was found
     */
    public static Optional<? extends LivingEntity> spawnAndAddMob(ServerLevel level, RandomSource random, EntityType<? extends LivingEntity> entityType, ICoords coords) {
        Optional<? extends LivingEntity> optionalMob = spawnMob(level, random, entityType, coords);
        optionalMob.ifPresent(level::addFreshEntityWithPassengers);
        return optionalMob;
    }

    /**
     * @deprecated the {@code mob} argument was never used, and before 2.10.0 this added that
     *             un-positioned instance instead of the mob it created. Call
     *             {@link #spawnAndAddMob(ServerLevel, RandomSource, EntityType, ICoords)}.
     */
    @Deprecated(forRemoval = true, since = "2.10.0")
    public static Optional<? extends LivingEntity> spawnAndAddMob(ServerLevel level, RandomSource random, EntityType<? extends LivingEntity> entityType, Entity mob, ICoords coords) {
        return spawnAndAddMob(level, random, entityType, coords);
    }

}
