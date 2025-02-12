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
import net.minecraft.world.level.Level;

/**
 * 
 * @author Mark Gottschling Jan 22, 2023
 *
 */
public class LesserRevelationEntity extends SummonedFlyingEntity {

	public LesserRevelationEntity(EntityType<? extends FlyingMob> entityType, Level level) {
		super(entityType, level, Config.SERVER.lesserRevelationLifespan.get());
	}

	@Override
	public void doLivingEffects() {
		double d0 = this.getX();
		double d1 = this.getY() + 0.2;
		double d2 = this.getZ();
		// TODO update with custom particles
		this.level().addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
		this.level().addParticle(Registration.REVELATION_PARTICLE.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
		this.level().addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, d0, d1, d2, 0.0D, 0.0D, 0.0D);

	}
	
}