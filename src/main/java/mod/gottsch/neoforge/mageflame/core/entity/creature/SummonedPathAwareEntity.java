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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Mark Gottschling on 1/9/2025
 */
public abstract class SummonedPathAwareEntity extends PathfinderMob implements ISummonedEntity {
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID;

    // entity for composite inheritance
    private final SummonedEntityBaseHandler<SummonedPathAwareEntity> summonedEntityBaseHandler;

    static {
        DATA_OWNER_UUID = SynchedEntityData.defineId(SummonedPathAwareEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    }

    protected SummonedPathAwareEntity(EntityType<? extends PathfinderMob> entityType, Level world, int lifespan) {
        super(entityType, world);
        this.summonedEntityBaseHandler = new SummonedEntityBaseHandler<>(world.getGameTime(), lifespan);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, (double)1.0F, 5.0F, 2.0F));
    }

    // TODO why isn't his used?!
    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 0.5)
                .add(Attributes.MOVEMENT_SPEED, 0.25F);
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

//    protected boolean testPlacement(BlockPos pos) {
//        BlockState state = this.level().getBlockState(pos);
//        // check block
//        return state.isAir();
//    }

    @Override
    public LivingEntity getOwner() {
        try {
            UUID uuid = this.getOwnerUUID();
            return (uuid == null) ? null : this.level().getPlayerByUUID(uuid);
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    /**
     * override vanilla entity.kill()
     */
    @Override
    public void kill() {
        MageFlame.LOGGER.info("killing entity -> {}", this.getUUID().toString());
        this.summonedEntityBaseHandler.killAndUnregister(this);
        // set dead
        this.dead = true;
    }

    /**
     * override MageFlame#ISummonedEntity.kill(DamageSource)
     * @param damageSource
     */
    @Override
    public void kill(DamageSource damageSource) {
        this.summonedEntityBaseHandler.killAndUnregister(this, damageSource);

        // set dead
        this.dead = true;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        if (this.getOwnerUUID() != null) {
            nbt.putUUID(OWNER, this.getOwnerUUID());
        }

        nbt.putLong(BIRTH_TIME, getBirthTime());
        nbt.putInt(LIFESPAN, getLifespan());
    }

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

    @Override
    public UUID getOwnerUUID() {
        return this.getEntityData().get(DATA_OWNER_UUID).orElse(null);
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
    public void setBirthTime(long birthTime) {
//        this.birthTime = birthTime;
        this.summonedEntityBaseHandler.setBirthTime(birthTime);
    }

    @Override
    public int getLifespan() {
        return this.summonedEntityBaseHandler.getLifespan();
    }

    @Override
    public void setLifespan(int lifespan) {
//        this.lifespan =lifespan;
        this.summonedEntityBaseHandler.setLifespan(lifespan);
    }

    ///// from TameableEntity /////
    public final boolean cannotFollowOwner() {
        return this.isPassenger() || this.getOwner() != null && this.getOwner().isSpectator();
    }

    protected boolean canTeleportOntoLeaves() {
        return true;
    }

    public void tryTeleportToOwner() {
        LivingEntity livingEntity = this.getOwner();
        if (livingEntity != null) {
            this.tryTeleportNear(livingEntity.blockPosition());
        }
    }

    public boolean shouldTryTeleportToOwner() {
        LivingEntity livingEntity = this.getOwner();
        return livingEntity != null && this.distanceToSqr(this.getOwner()) >= 144.0;
    }

    private void tryTeleportNear(BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            int j = randomIntInclusive(-3, 3);
            int k = randomIntInclusive(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = randomIntInclusive(-1, 1);
                if (this.tryTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
                    return;
                }
            }
        }
    }

    private boolean tryTeleportTo(int x, int y, int z) {
        if (!this.canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            this.moveTo((double)x + 0.5, (double)y, (double)z + 0.5, this.getYRot(), this.getXRot());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        PathType blockPathType = WalkNodeEvaluator.getPathTypeStatic(this, pos.mutable());
        if (blockPathType != PathType.WALKABLE) {
            return false;
        } else {
            BlockState blockState = this.level().getBlockState(pos.below());
            if (!this.canTeleportOntoLeaves() && blockState.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockPos = pos.subtract(this.blockPosition());
                return this.level().noCollision(this, this.getBoundingBox().move(blockPos));
            }
        }
    }

    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        return true;
    }

    private int randomIntInclusive(int p_25301_, int p_25302_) {
        return this.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
    }

    ///// end from Tameable /////

    public static class FollowOwnerGoal extends Goal {
        private final SummonedPathAwareEntity lightSourceEntity;
        private LivingEntity owner;
        private final double speed;
        private final PathNavigation navigation;
        private int updateCountdownTicks;
        private final float maxDistance;
        private final float minDistance;
        private float oldWaterPathfindingPenalty;

        public FollowOwnerGoal(SummonedPathAwareEntity lightSourceEntity, double speed, float minDistance, float maxDistance) {
            this.lightSourceEntity = lightSourceEntity;
            this.speed = speed;
            this.navigation = lightSourceEntity.getNavigation();
            this.minDistance = minDistance;
            this.maxDistance = maxDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            if (!(lightSourceEntity.getNavigation() instanceof GroundPathNavigation) && !(lightSourceEntity.getNavigation() instanceof FlyingPathNavigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public boolean canUse() {
            LivingEntity ownerEntity = this.lightSourceEntity.getOwner();
            if (ownerEntity == null) {
                return false;
            } else if (this.lightSourceEntity.cannotFollowOwner()) {
                return false;
            } else if (this.lightSourceEntity.distanceToSqr(ownerEntity) < (double)(this.minDistance * this.minDistance)) {
                return false;
            } else {
                this.owner = ownerEntity;
                return true;
            }
        }

        public boolean shouldContinue() {
            if (this.navigation.isDone()) {
                return false;
            } else if (this.lightSourceEntity.cannotFollowOwner()) {
                return false;
            } else {
                return !(this.lightSourceEntity.distanceToSqr(this.owner) <= (double)(this.maxDistance * this.maxDistance));
            }
        }

        public void start() {
            this.updateCountdownTicks = 0;
            this.oldWaterPathfindingPenalty = this.lightSourceEntity.getPathfindingMalus(PathType.WATER);
            this.lightSourceEntity.setPathfindingMalus(PathType.WATER, 0.0F);
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
            this.lightSourceEntity.setPathfindingMalus(PathType.WATER, this.oldWaterPathfindingPenalty);
        }

        public void tick() {
            boolean bl = this.lightSourceEntity.shouldTryTeleportToOwner();
            if (!bl) {
                this.lightSourceEntity.getLookControl().setLookAt(this.owner, 10.0F, (float)this.lightSourceEntity.getMaxHeadXRot());
            }

            if (--this.updateCountdownTicks <= 0) {
                this.updateCountdownTicks = this.adjustedTickDelay(10);
                if (bl) {
                    this.lightSourceEntity.tryTeleportToOwner();
                } else {
                    this.navigation.moveTo(this.owner, this.speed);
                }
            }
        }
    }
}
