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
package mod.gottsch.neoforge.mageflame.core.entity.creature;

import mod.gottsch.neoforge.mageflame.core.config.Config;
import mod.gottsch.neoforge.mageflame.core.setup.Registration;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/**
 *
 * @author Mark Gottschling Jan 18, 2025
 *
 */
public class BubbleFlameEntity extends SummonedFlyingEntity {

    public BubbleFlameEntity(EntityType<? extends FlyingMob> entityType, Level level) {
        super(entityType, level, Config.SERVER.bubbleFlameLifespan.get());
    }

    @Override
    public boolean canLiveInFluid() {
        return true;
    }

    @Override
    public void doLivingEffects() {
        double d1 = this.getY() + 0.2;
        if (this.level().getGameTime() % 2 == 0) {
            double d0 = this.getRandomX(0.65);
            double d2 = this.getRandomZ(0.65);
            this.level().addParticle(Registration.BUBBLE_FLAME_PARTICLE.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
        if (this.level().getGameTime() % 3 == 0) {
            double d0 = this.getRandomX(0.65);
            double d2 = this.getRandomZ(0.65);
            this.level().addParticle(Registration.BUBBLE_FLAME_PARTICLE.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }

        if (this.level().getGameTime() % 4 == 0) {
            double d0 = this.getX(0.65);
            double d2 = this.getZ(0.65);
            this.level().addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }
}