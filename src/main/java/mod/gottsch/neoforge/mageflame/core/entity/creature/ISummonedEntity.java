/*
 * This file is part of Mage Flame.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
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
package mod.gottsch.neoforge.mageflame.core.entity.creature;


import mod.gottsch.neoforge.mageflame.core.util.SpawnUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 *
 */
public interface ISummonedEntity {
    static final String OWNER = "owner";
    static final String BIRTH_TIME = "birthTime";
    static final String LIFESPAN = "lifespan";

    void doLivingEffects();
    void doDeathEffects();
    double updateLifespan();
    default boolean canLiveInFluid() {
        return false;
    }

    void kill(DamageSource damageSource);

    LivingEntity getOwner();
    default void setOwner(LivingEntity entity) {
        if (entity == null) {
            setOwnerUUID(null);
        }
        else {
            setOwnerUUID(entity.getUUID());
        }
    }

    UUID getOwnerUUID();
    void setOwnerUUID(UUID uuid);

    long getBirthTime();
    int getLifespan();

    void setBirthTime(long birthTime);
    void setLifespan(int lifespan);

    /**
     *
     * @return
     */
    default public Vec3 selectSummonOffsetPos(LivingEntity entity) {
        Vec3 eyePos = entity.getEyePosition();
        Direction direction = entity.getDirection();
        Vec3 offsetPos = switch (direction) {
            case NORTH -> eyePos.add(new Vec3(0.5, 0, 0.35));
            case SOUTH -> eyePos.add(new Vec3(-0.5, 0, -0.35));
            case EAST -> eyePos.add(new Vec3(-0.35, 0, 0.5));
            case WEST -> eyePos.add(new Vec3(0.35, 0, -0.5));
            default -> eyePos.add(new Vec3(1, 0, 1));
        };
        return offsetPos;
    }

    /**
     * determines where the entity should spawn in relation to the owner.
     * default implementation uses the SpawnUtil version which is over and behind the shoulder.
     * @param level
     * @param coords
     * @param direction
     * @return
     */
    default public Vec3 selectSpawnPos(Level level, Vec3 coords, Direction direction) {
        return SpawnUtil.selectSpawnPos(level, coords, direction);
    }

}
