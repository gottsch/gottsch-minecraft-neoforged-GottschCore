/*
 * This file is part of  Mage Flame.
 * Copyright (c) 2025 Mark Gottschling (gottsch)
 *
 * Mage Flame is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mage Flame is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mage Flame.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neoforge.mageflame.core.util;

import mod.gottsch.neo.gottschcore.spatial.Coords;
import mod.gottsch.neo.gottschcore.spatial.ICoords;
import mod.gottsch.neo.gottschcore.world.WorldInfo;
import mod.gottsch.neoforge.mageflame.core.config.Config;
import mod.gottsch.neoforge.mageflame.core.entity.creature.ISummonedEntity;
import mod.gottsch.neoforge.mageflame.core.persistence.PlayerData;
import mod.gottsch.neoforge.mageflame.core.persistence.StateSaverAndLoader;
import mod.gottsch.neoforge.mageflame.core.persistence.SummonedEntityData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mark Gottschling on 1/14/2025
 */
public class SpawnUtil {

    public static Optional<Entity> spawnAndRegister(ServerLevel level, RandomSource random, Player owner, EntityType entityType, ICoords coords){
        return spawnAndRegister(level, random, owner, entityType, entityType.create(level), coords  );
    }

    private static Optional<Entity> spawnAndRegister(ServerLevel level, RandomSource random, Player owner, EntityType entityType, Entity entity, ICoords coords) {
        Optional<Entity> mob = spawn(level, level.getRandom(), entityType, entity, coords);
        if (mob.isPresent()) {
            ((ISummonedEntity)mob.get()).setOwner(owner);
            // registry entity
            SpawnUtil.register(level, entityType, (Mob) mob.get(), owner);
        }
        return mob;
    }

    /**
     *
     * @param level
     * @param random
     * @param entityType
     * @param mob
     * @param coords
     * @return
     */
    public static Optional<Entity> spawn(ServerLevel level, RandomSource random, EntityType<?> entityType, Entity mob, ICoords coords) {

        for (int i = 0; i < 20; i++) { // 20 tries
            int spawnX = coords.getX() + Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
            int spawnY = coords.getY() + Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
            int spawnZ = coords.getZ() + Mth.nextInt(random, 1, 2) * Mth.nextInt(random, -1, 1);
            ICoords spawnCoords = new Coords(spawnX, spawnY, spawnZ);
            BlockState state = level.getBlockState(spawnCoords.toPos());
            if (!WorldInfo.isClientSide(level)) {
                SpawnPlacementType placement = SpawnPlacements.getPlacementType(entityType);
                if (NaturalSpawner.isValidEmptySpawnBlock(level, spawnCoords.toPos(), state, state.getFluidState(), entityType)) {
                    mob.setPos((double)spawnX, (double)spawnY, (double)spawnZ);
                    level.addFreshEntityWithPassengers(mob);
                    return Optional.of(mob);
                }
            }
        }
        return Optional.empty();
    }

    /**
     *
     * @param world
     * @param random
     * @param owner
     * @param entityType
     * @param coords
     * @return
     * @param <T>
     */
    public static <T extends Mob & ISummonedEntity>Optional<?> spawnAtPos(ServerLevel world, RandomSource random, LivingEntity owner, EntityType<T> entityType, Vec3 coords) {
        return spawnAtPos(world, random, owner, entityType, Coords.of((int)coords.x, (int)coords.y, (int)coords.z));
    }

    /**
     * use this version when you know the exact location to spawn.
     * ex. when joining world, and loading entities from persistence.
     */
    public static <T extends Mob & ISummonedEntity>Optional<?> spawnAtPos(ServerLevel level, RandomSource random, LivingEntity owner, EntityType<T> entityType, ICoords coords) {

        if (!level.isClientSide) {
            BlockPos spawnPos = coords.toPos();

            // determine if the entity can spawn
            BlockState state = level.getBlockState(spawnPos);
            if(NaturalSpawner.isValidEmptySpawnBlock(level, spawnPos, state, state.getFluidState(), entityType)) {
                // create entity
                Mob mob = entityType.create(level);
                if (mob != null) {
                    mob.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                    ((ISummonedEntity)mob).setOwner(owner);

                    register(level, entityType, mob, owner);

                    // add entity into the world (ie EntityJoinWorldEvent)
                    level.addFreshEntityWithPassengers(mob);

                    return Optional.of(mob);
                }
            }
        }
        return Optional.empty();
    }

    /**
     *
     * @param level
     * @param entityType
     * @param mob
     * @param owner
     * @param <T>
     */
    public static <T extends Mob & ISummonedEntity> void register(ServerLevel level, EntityType<T> entityType, Mob mob, LivingEntity owner) {
        PlayerData playerData = StateSaverAndLoader.getPlayerState(owner);
        if (playerData.getKeys().size() >= Config.SERVER.maxSummonedEntitiesPerPlayer.get()) {
            cullSummonedEntities(level, playerData);
        }

        // register entity to player
        SummonedEntityData summonedEntityData = new SummonedEntityData();
        summonedEntityData.setId(mob.getUUID());
        summonedEntityData.setDimension(level.dimension().location());
        summonedEntityData.setEntityType(entityType);
        summonedEntityData.setLifespan(((ISummonedEntity) mob).getLifespan());
        summonedEntityData.setCreateTime(level.getGameTime());
        playerData.register(mob.getUUID(), summonedEntityData);
    }

    public static List<EntityType<?>> getAllSummonedEntities(ServerLevel world, Player player) {
        List<SummonedEntityData> entities = StateSaverAndLoader.getPlayerState(player).getValues();
        List<EntityType<?>> list = new ArrayList<>();
        entities.forEach(summonedEntityData -> {
            list.add(summonedEntityData.getEntityType());
        });
        return list;
    }

    public static void killAllSummonedEntities(ServerLevel world, Player player) {
        killAllSummonedEntities(world, StateSaverAndLoader.getPlayerState(player));
    }

    public static void killAllSummonedEntities(ServerLevel world, PlayerData playerData) {
        playerData.getValues().forEach(summonedEntityData -> {
            Entity worldEntity = world.getEntity(summonedEntityData.getId());
            if (worldEntity != null) {
                worldEntity.kill();
            }
            playerData.unregister(summonedEntityData.getId());
        });
    }

    public static void killSummonedEntity(ServerLevel world, Player player, String entityName) {
        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);

        List<SummonedEntityData> summonedEntityDataList = playerData.getValues();
        // update data with lifespan of actual in game entities
        summonedEntityDataList.forEach(summonedEntityData -> {
            ISummonedEntity worldEntity = (ISummonedEntity) world.getEntity(summonedEntityData.getId());
            if (worldEntity != null) {
                summonedEntityData.setLifespan(worldEntity.getLifespan());
            }
        });
        // sort the data
        summonedEntityDataList.sort(SummonedEntityData.lifespanComparator);

        List<SummonedEntityData> toRemove = new ArrayList<>();
        for(SummonedEntityData data : summonedEntityDataList) {

            if (data.getEntityType().getDescription().getString().equals(entityName)) {
                Entity worldEntity = world.getEntity(summonedEntityDataList.get(0).getId());
                if (worldEntity != null) {
                    worldEntity.kill();
                }
                toRemove.add(data);
                break;
            }
        }

        toRemove.forEach(summonedEntityDataList::remove);
    }

    public static void cullSummonedEntities(ServerLevel world, PlayerData playerData) {
        /*
         * kill and unregister with least lifespan remaining entities for registry until number <= maxEntities
         */
        List<SummonedEntityData> summonedEntityDataList = playerData.getValues();
        // update data with lifespan of actual in game entities
        summonedEntityDataList.forEach(summonedEntityData -> {
            ISummonedEntity worldEntity = (ISummonedEntity) world.getEntity(summonedEntityData.getId());
            if (worldEntity != null) {
                summonedEntityData.setLifespan(worldEntity.getLifespan());
            }
        });
        // sort the data
        summonedEntityDataList.sort(SummonedEntityData.lifespanComparator);

        while (summonedEntityDataList.size() >= Config.SERVER.maxSummonedEntitiesPerPlayer.get()) {
            Entity worldEntity = world.getEntity(summonedEntityDataList.get(0).getId());
            if (worldEntity != null) {
                worldEntity.kill();
            }
            summonedEntityDataList.remove(0);
        }
    }


    public static Vec3 selectSpawnPos(Level world, Vec3 coords, Direction direction) {

        if (!world.getBlockState(new BlockPos(vec3ToBlockPos(coords))).isAir()) {
            // test to the left
            switch (direction) {
                default:
                case NORTH:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(-1, 0, 0))).isAir()) coords.add(-1, 0, 0);
                case SOUTH:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(1, 0, 0))).isAir()) coords.add(1, 0, 0);
                case EAST :
                    if (world.getBlockState(vec3ToBlockPos(coords.add(0, 0, -1))).isAir()) coords.add(0, 0, -1);
                case WEST:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(0, 0, 1))).isAir()) coords.add(0, 0, 1);
            };

            // test to the left+down
            switch (direction) {
                default:
                case NORTH:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(-1, -1, 0))).isAir()) coords.add(-1, -1, 0);
                case SOUTH:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(1, -1, 0))).isAir()) coords.add(1, -1, 0);
                case EAST :
                    if (world.getBlockState(vec3ToBlockPos(coords.add(0, -1, -1))).isAir()) coords.add(0, -1, -1);
                case WEST:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(0, -1, 1))).isAir()) coords.add(0, -1, 1);
            };

            // test behind
            switch (direction) {
                default:
                case NORTH:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(0, 0, 1))).isAir()) coords.add(0, 0, 1);
                case SOUTH:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(0, 0, -1))).isAir()) coords.add(0, 0, -1);
                case EAST :
                    if (world.getBlockState(vec3ToBlockPos(coords.add(-1, 0, 0))).isAir()) coords.add(-1, 0, 0);
                case WEST:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(1, 0, 0))).isAir()) coords.add(1, 0, 0);
            };

            // test down
            if (world.getBlockState(vec3ToBlockPos(coords.add(0, 1, 0))).isAir()) coords.add(0, 1, 0);

            // test right
            switch (direction) {
                default:
                case NORTH:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(1, 0, 0))).isAir()) coords.add(1, 0, 0);
                case SOUTH:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(-1, 0, 0))).isAir()) coords.add(-1, 0, 0);
                case EAST :
                    if (world.getBlockState(vec3ToBlockPos(coords.add(0, 0, 1))).isAir()) coords.add(0, 0, 1);
                case WEST:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(0, 0, -1))).isAir()) coords.add(0, 0, -1);
            };

            // test right+down
            switch (direction) {
                default:
                case NORTH:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(1, -1, 0))).isAir()) coords.add(1, -1, 0);
                case SOUTH:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(-1, -1, 0))).isAir()) coords.add(-1, -1, 0);
                case EAST :
                    if (world.getBlockState(vec3ToBlockPos(coords.add(0, -1, 1))).isAir()) coords.add(0, -1, 1);
                case WEST:
                    if (world.getBlockState(vec3ToBlockPos(coords.add(0, -1, -1))).isAir()) coords.add(0, -1, -1);
            };
        }
        return coords;
    }

    public static BlockPos vec3ToBlockPos(Vec3 vec3) {
        return new BlockPos((int)vec3.x, (int)vec3.y, (int)vec3.z);
    }

    public static Vec3 getByPlayerPos(Player player) {
        Vec3 eyePos = player.getEyePosition();
        Direction direction = player.getDirection();
        return switch (direction) {
            case NORTH -> eyePos.add(new Vec3(0.5, 0, 0.35));
            case SOUTH -> eyePos.add(new Vec3(-0.5, 0, -0.35));
            case EAST -> eyePos.add(new Vec3(-0.35, 0, 0.5));
            case WEST -> eyePos.add(new Vec3(0.35, 0, -0.5));
            default -> eyePos.add(new Vec3(0.5, 0, 0.35));
        };
    }

}
