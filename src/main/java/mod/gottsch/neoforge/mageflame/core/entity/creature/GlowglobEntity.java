/*
 * This file is part of Mage Flame.
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

import mod.gottsch.neoforge.mageflame.core.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * @author Mark Gottschling on 1/21/2025
 */
public class GlowglobEntity extends FlyingMob implements ILifespanEntity {
    private int lifespan;

    public GlowglobEntity(EntityType<? extends FlyingMob> entityType, Level world) {
        super(entityType, world);
        setLifespan(Config.SERVER.glowglobLifespan.get());
        this.moveControl = new GlowglobMoveControl(this);
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new FloatGoal(this));
    }

    public static AttributeSupplier.Builder createGlobAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0);
    }

    @Override
    public double updateLifespan() {
        return --this.lifespan;
    }

    @Override
    public void doLivingEffects() {
        double d0 = this.getRandomX(0.25);
        double d1 = this.getY() + 0.35;
        double d2 = this.getRandomZ(0.25);
        this.level().addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        this.level().addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
//        this.level().addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0, 0.0, 0.0);
    }

    @Override
    public void doDeathEffects() {
        Zombie zombie;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            if (updateLifespan() < 0) {
                kill();
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) {
            if (level().getGameTime() % 10 == 0) {
                BlockState state = level().getBlockState(blockPosition());
                if (state.getFluidState().isEmpty() || canLiveInFluid()) {
                    doLivingEffects();
                }
            }
        } else {
            if (level().getGameTime() % 10 == 0) {
                BlockState state = level().getBlockState(blockPosition());
                if (!state.getFluidState().isEmpty() && !canLiveInFluid()) {
                    // kill self
                    kill(); // TODO discard() instead of kill?
                }
            }
        }
    }

    @Override
    public void kill() {
        super.kill();
        doDeathEffects();
        // hide the entity
        setInvisible(true);
        // set dead
        this.dead = true;
    }

    @Override
    protected void playHurtSound(DamageSource source) {
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CAMPFIRE_CRACKLE;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);

        nbt.putInt(LIFESPAN, getLifespan());
    }

    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        if (nbt.contains(LIFESPAN)) {
            setLifespan(nbt.getInt(LIFESPAN));
        }
    }

    @Override
    public void checkDespawn() {
        // does NOT despawn
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

     @Override
    public int getLifespan() {
        return lifespan;
    }

    @Override
    public void setLifespan(int lifespan) {
        this.lifespan = lifespan;
    }


    static class GlowglobMoveControl extends MoveControl {
        private final GlowglobEntity glowglob;
        private int collisionCheckCooldown;

        public GlowglobMoveControl(final GlowglobEntity glowglob) {
            super(glowglob);
            this.glowglob = glowglob;
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                // get the vector between the glowglob and the target
                Vec3 vec3d = new Vec3(this.wantedX - glowglob.getX(), this.wantedY - glowglob.getY(), this.wantedZ - glowglob.getZ());

                double distance = vec3d.length();
                vec3d = vec3d.normalize();
                if (this.willCollide(vec3d, Mth.ceil(distance))) {
                    this.glowglob.setDeltaMovement(this.glowglob.getDeltaMovement().add(vec3d.scale(0.025)));
                }

                // NOTE this is the important part
                // when within body length of target, then start the stopping process
                if (distance < glowglob.getBoundingBox().getSize()) {
                    glowglob.setDeltaMovement(glowglob.getDeltaMovement().scale(0.25));
                    if (glowglob.getDeltaMovement().length() < 0.0125) {
                        glowglob.setDeltaMovement(glowglob.getDeltaMovement().scale(0));
                        this.operation = MoveControl.Operation.WAIT;
                    }
                }
            }
        }

        private boolean willCollide(Vec3 direction, int steps) {
            AABB box = this.glowglob.getBoundingBox();
            for (int i = 1; i < steps; i++) {
                box = box.move(direction);
                if (!this.glowglob.level().noCollision(this.glowglob, box)) {
                    return false;
                }
            }
            return true;
        }
    }

    /*
     *
     */
    static class FloatGoal extends Goal {
        private final GlowglobEntity glowglob;

        public FloatGoal(GlowglobEntity glowglob) {
            this.glowglob = glowglob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            MoveControl moveControl = this.glowglob.getMoveControl();
            if (!moveControl.hasWanted()) {
                return true;
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {

            /*
             * calculate the target position
             */
            int count = 0;
            BlockPos pos = glowglob.blockPosition();
            while (glowglob.level().getBlockState(pos).canBeReplaced() && count < 32) {
                pos = pos.below();
                count++;
            }

            // calculate target y
            double y = pos.getY() + 3.5; // just above players head

            // calculate the vector between glowglob and floor on y-axis
            Vec3 vec3d = new Vec3(0, y - glowglob.getY(), 0);

            // if the distance is greater than the average side size of the glowglob's
            // bounding box then update the target position
            if (vec3d.length() > glowglob.getBoundingBox().getSize()) {
                this.glowglob.getMoveControl().setWantedPosition(glowglob.getX(), y, glowglob.getZ(), 0.05);
            }
        }
    }
}
