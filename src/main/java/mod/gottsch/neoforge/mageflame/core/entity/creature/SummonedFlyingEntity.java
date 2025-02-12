/*
 * This file is part of  Mage Flame.
 * Copyright (c) 2023 Mark Gottschling (gottsch)
 *
 * Mage Flame is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Mage Flame is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURCoordsE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mage Flame.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neoforge.mageflame.core.entity.creature;

import mod.gottsch.neoforge.mageflame.core.MageFlame;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 *
 * @author Mark Gottschling Jan 21, 2023
 *
 */
public abstract class SummonedFlyingEntity extends FlyingMob implements ISummonedEntity {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID;

    // entity for composite inheritance
    private final SummonedEntityBaseHandler<SummonedFlyingEntity> summonedEntityBaseHandler;

    static {
        DATA_OWNER_UUID = SynchedEntityData.defineId(SummonedFlyingEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    }

    /**
     *
     * @param entityType
     * @param world
     */
    protected SummonedFlyingEntity(EntityType<? extends FlyingMob> entityType, Level world, int lifespan) {
        super(entityType, world);
        this.summonedEntityBaseHandler = new SummonedEntityBaseHandler<>(world.getGameTime(), lifespan);

        this.moveControl = new SummonedLightSourceFlyingMoveControl(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SummonedFlyingEntityFollowOwnerGoal(this, 3F));
    }

    /**
     *
     */
    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.MAX_HEALTH, 0.5)
                .add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER_UUID, Optional.empty());
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // do not play a sound
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CAMPFIRE_CRACKLE;
    }

    @Override
    public double updateLifespan() {
        return this.summonedEntityBaseHandler.updateLifespan();
    }

    @Override
    public void doDeathEffects() {
        this.summonedEntityBaseHandler.doDeathEffects(this);
    }

    @Override
    public void tick() {
        super.tick();
        this.summonedEntityBaseHandler.tick(this, getOwner());
    }


    @Override
    public void aiStep() {
        super.aiStep();
        this.summonedEntityBaseHandler.tickMovement(this);
    }

    @Override
    public void kill() {
        MageFlame.LOGGER.info("killing entity -> {}", this.getUUID().toString());
        this.summonedEntityBaseHandler.killAndUnregister(this);
        // set dead
        this.dead = true;
    }

    /**
     *
     * @param damageSource the source of the damage
     */
    public void kill(DamageSource damageSource) {
        this.summonedEntityBaseHandler.killAndUnregister(this, damageSource);

        // set dead
        this.dead = true;
    }

    /**
     *
     * @param nbt
     */
    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        if (this.getOwnerUUID() != null) {
            nbt.putUUID(OWNER, this.getOwnerUUID());
        }

        nbt.putLong(BIRTH_TIME, getBirthTime());
        nbt.putInt(LIFESPAN, getLifespan());
    }

    /**
     *
     * @param nbt
     */
    public void readCustomDataFromNbt(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains(OWNER)) {
            UUID uuid = nbt.getUUID(OWNER);
            try {
                this.setOwnerUUID(uuid);
            } catch (Throwable throwable) {
                MageFlame.LOGGER.warn("Unable to set owner of flame ball to -> {}", uuid);
            }
        }

        if (nbt.contains(BIRTH_TIME)) {
            setBirthTime(nbt.getLong(BIRTH_TIME));
        }
        if (nbt.contains(LIFESPAN)) {
            setLifespan(nbt.getInt(LIFESPAN));
        }
    }

    @Override
    public void checkDespawn() {
        // does NOT despawn
    }

//    public static boolean canSpawn(EntityType<?> type, Level world, SpawnReason spawnReason, BlockPos pos, Random random) {
//        return false;
//    }

    /**
     *
     */
    public static class SummonedFlyingEntityFollowOwnerGoal extends Goal {
        private SummonedFlyingEntity lightSourceEntity;
        // the distance away at which the flame ball starts to follow
        private float startDistance;
        private LivingEntity owner;

        /**
         *
         * @param lightSourceEntity
         * @param startDistance
         */
        public SummonedFlyingEntityFollowOwnerGoal(SummonedFlyingEntity lightSourceEntity, float startDistance) {
            this.lightSourceEntity = lightSourceEntity;
            this.startDistance = startDistance;
        }

        @Override
        public boolean canUse() {
            if (this.lightSourceEntity.random.nextInt(adjustedTickDelay(7)) == 0) {
                return false;
            }

            LivingEntity ownerEntity = this.lightSourceEntity.getOwner();
            if (ownerEntity == null) {
                return false;
            } else if (ownerEntity.isSpectator()) {
                return false;
            }

            this.owner = ownerEntity;
            MoveControl moveControl = this.lightSourceEntity.getMoveControl();
            double distance = 0;
            if (this.lightSourceEntity.getTarget() != null) {
                double d0 = moveControl.getWantedX() - this.lightSourceEntity.getX();
                double d1 = moveControl.getWantedY() - this.lightSourceEntity.getY();
                double d2 = moveControl.getWantedZ() - this.lightSourceEntity.getZ();
                distance = d0 * d0 + d1 * d1 + d2 * d2;
            }
            boolean outsideProximity = this.lightSourceEntity.distanceToSqr(ownerEntity) > startDistance * startDistance;
            return (distance < 1.0D && outsideProximity) || distance > 3600.0D;
        }

        @Override
        public boolean canContinueToUse() {
            double d0 = this.lightSourceEntity.getMoveControl().getWantedX() - this.lightSourceEntity.getX();
            double d1 = this.lightSourceEntity.getMoveControl().getWantedY() - this.lightSourceEntity.getY();
            double d2 = this.lightSourceEntity.getMoveControl().getWantedZ() - this.lightSourceEntity.getZ();
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d3 >= 1D) {
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            Vec3 initialPos = this.lightSourceEntity.selectSummonOffsetPos(this.owner);
            Vec3 wantedPos = this.lightSourceEntity.selectSpawnPos(this.lightSourceEntity.level(), new Vec3(initialPos.x, initialPos.y, initialPos.z), this.lightSourceEntity.getDirection());
            this.lightSourceEntity.getMoveControl().setWantedPosition(wantedPos.x, wantedPos.y, wantedPos.z, 1.0D);
        }

        @Override
        public void stop() {
            this.owner = null;
        }

        @Override
        public void tick() {
            if (this.lightSourceEntity.random.nextInt(adjustedTickDelay(5)) == 0) {
                if (this.lightSourceEntity.distanceToSqr(this.owner) >= 36.0D) {
                    // teleport to owner
                    Vec3 offsetPos = this.lightSourceEntity.selectSummonOffsetPos(this.owner);
                    Vec3 wantedPos = this.lightSourceEntity.selectSpawnPos(this.lightSourceEntity.level(), new Vec3(offsetPos.x, offsetPos.y, offsetPos.z), this.lightSourceEntity.getDirection());
                    this.lightSourceEntity.getMoveControl().setWantedPosition(wantedPos.x, wantedPos.y, wantedPos.z, 1.0D);
                }
            }
        }
    }

    /*
     * This uses the Vex MoveControl tick() algorithm.
     */
    static class SummonedLightSourceFlyingMoveControl extends MoveControl {
        public SummonedLightSourceFlyingMoveControl(SummonedFlyingEntity entity) {
            super(entity);
        }

        public void tick() {
            if (this.operation == Operation.MOVE_TO) {
                Vec3 vec3d = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
                double d = vec3d.length();
                if (d < this.mob.getBoundingBox().getSize()) {
                    this.operation = Operation.WAIT;
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.5));
                } else {
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(vec3d.scale(this.speedModifier * 0.05 / d)));
                    if (this.mob.getTarget() == null) {
                        Vec3 vec3d2 = this.mob.getDeltaMovement();
                        this.mob.setYHeadRot(-((float) Mth.atan2(vec3d2.x, vec3d2.z)) * 57.295776F);
                        this.mob.yBodyRot = this.mob.getYHeadRot();
                    } else {
                        double e = this.mob.getTarget().getX() - this.mob.getX();
                        double f = this.mob.getTarget().getZ() - this.mob.getZ();
                        this.mob.setYHeadRot(-((float)Mth.atan2(e, f)) * 57.295776F);
                        this.mob.yBodyRot = this.mob.yHeadRot;
                    }
                }

            }
        }
    }

    @Override
    @Nullable
    public LivingEntity getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();
            return uuid == null ? null : this.level().getPlayerByUUID(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER_UUID).orElse(null);
    }

    @Override
    public void setOwnerUUID(UUID uuid) {
        this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Override
    public long getBirthTime() {
//        return birthTime;
        return this.summonedEntityBaseHandler.getBirthTime();
    }

    @Override
    public int getLifespan() {
        return this.summonedEntityBaseHandler.getLifespan();
    }

    @Override
    public void setBirthTime(long birthTime) {
        this.summonedEntityBaseHandler.setBirthTime(birthTime);
    }

    @Override
    public void setLifespan(int lifespan) {
        this.summonedEntityBaseHandler.setLifespan(lifespan);
    }
}