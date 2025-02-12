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


import mod.gottsch.neoforge.mageflame.core.config.Config;
import mod.gottsch.neoforge.mageflame.core.entity.ai.goal.SummonedLightSourceAttackWithOwnerGoal;
import mod.gottsch.neoforge.mageflame.core.entity.ai.goal.SummonedLightSourceTrackOwnerAttackerGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Created by Mark Gottschling on 1/9/2025
 */
public class EmberHoundEntity extends SummonedPathAwareEntity {

    public EmberHoundEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world, Config.SERVER.emberHoundLifespan.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new SummonedLightSourceTrackOwnerAttackerGoal(this));
        this.targetSelector.addGoal(2, new SummonedLightSourceAttackWithOwnerGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this, Player.class));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal(this, AbstractSkeleton.class, false));
    }

    public static AttributeSupplier.Builder createWolfAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.ATTACK_KNOCKBACK)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0);
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        int i = this.random.nextInt(10);
        if (i < 2) {
            return SoundEvents.WOLF_GROWL;
        } else if (i < 5) {
            return SoundEvents.WOLF_PANT;
        } else if (i < 7) {
            return SoundEvents.WOLF_AMBIENT;
        } else {
            return super.getAmbientSound();
        }
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WOLF_HURT;
    }

    @Override
    public double updateLifespan() {
        return getLifespan();
    }

    @Override
    public int getLifespan() {
        if (Config.SERVER.isEmberHoundLifespanInfinite.get()) {
            return Integer.MAX_VALUE;
        }
        return super.getLifespan();
    }

    @Override
    public void doLivingEffects() {
        double d1 = this.getRandomY();
        for (int i=0; i < 3; i++) {
            double d0 = this.getRandomX(0.5);
            double d2 = this.getRandomZ(0.75);
            this.level().addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
        if (this.level().getGameTime() % 4 == 0) {
            double d0 = this.getX(0.5);
            double d2 = this.getZ(0.75);
            this.level().addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }
}
