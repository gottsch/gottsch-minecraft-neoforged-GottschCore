/*
 * This file is part of Mage Flame.
 * Copyright (c) 2022 Mark Gottschling (gottsch)
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
package mod.gottsch.neoforge.mageflame.core.setup;

import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.entity.creature.*;
import mod.gottsch.neoforge.mageflame.core.entity.projectile.thrown.GlowglobBallEntity;
import mod.gottsch.neoforge.mageflame.core.item.ModItems;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 
 * @author Mark Gottschling on Nov 6, 2022
 *
 */
public class Registration {
	public static final String MAGE_FLAME = "mage_flame";
	public static final String LESSER_REVELATION = "lesser_revelation";
	public static final String GREATER_REVELATION = "greater_revelation"; 
	public static final String WINGED_TORCH = "winged_torch"; 
	public static final String EMBER_HOUND = "ember_hound";
	public static final String BUBBLE_FLAME = "bubble_flame";
	public static final String GLOWGLOB = "glowglob";
	public static final String GLOWGLOB_BALL = "glowglob_ball";

	/*
	 * deferred registries
	 */
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MageFlame.MOD_ID);
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, MageFlame.MOD_ID);

	// entities
	public static final DeferredHolder<EntityType<?>, EntityType<MageFlameEntity>> MAGE_FLAME_ENTITY  = Registration.ENTITIES.register(MAGE_FLAME, () -> EntityType.Builder.of(MageFlameEntity::new, MobCategory.CREATURE)
			.sized(0.125F, 0.125F)
			.clientTrackingRange(8)
			.setTrackingRange(20)
			.setShouldReceiveVelocityUpdates(false)
			.build(MAGE_FLAME));
	
	public static final DeferredHolder<EntityType<?>, EntityType<LesserRevelationEntity>> LESSER_REVELATION_ENTITY  = Registration.ENTITIES.register(LESSER_REVELATION, () -> EntityType.Builder.of(LesserRevelationEntity::new, MobCategory.CREATURE)
			.sized(0.125F, 0.125F)
			.clientTrackingRange(8)
			.setTrackingRange(20)
			.setShouldReceiveVelocityUpdates(false)
			.build(LESSER_REVELATION));
	
	public static final DeferredHolder<EntityType<?>, EntityType<GreaterRevelationEntity>> GREATER_REVELATION_ENTITY  = Registration.ENTITIES.register(GREATER_REVELATION, () -> EntityType.Builder.of(GreaterRevelationEntity::new, MobCategory.CREATURE)
			.sized(0.1875F, 0.1875F)
			.clientTrackingRange(8)
			.setTrackingRange(20)
			.setShouldReceiveVelocityUpdates(false)
			.build(GREATER_REVELATION));
	
	public static final DeferredHolder<EntityType<?>, EntityType<WingedTorchEntity>> WINGED_TORCH_ENTITY  = Registration.ENTITIES.register(WINGED_TORCH, () -> EntityType.Builder.of(WingedTorchEntity::new, MobCategory.CREATURE)
			.sized(0.375F, 0.25F)
			.clientTrackingRange(8)
			.setTrackingRange(20)
			.setShouldReceiveVelocityUpdates(false)
			.build(WINGED_TORCH));

	public static final DeferredHolder<EntityType<?>, EntityType<EmberHoundEntity>> EMBER_HOUND_ENTITY = Registration.ENTITIES.register(
			EMBER_HOUND, () -> EntityType.Builder.of(EmberHoundEntity::new, MobCategory.CREATURE)
			.sized(0.6F, 0.85F)
			.clientTrackingRange(8)
			.setTrackingRange(20)
			.setShouldReceiveVelocityUpdates(false)
			.fireImmune()
			.build(EMBER_HOUND)
	);

	public static final DeferredHolder<EntityType<?>, EntityType<BubbleFlameEntity>> BUBBLE_FLAME_ENTITY = Registration.ENTITIES.register(
			BUBBLE_FLAME, () -> EntityType.Builder.of(BubbleFlameEntity::new, MobCategory.CREATURE)
			.sized(0.25F, 0.25F)
			.clientTrackingRange(8)
			.setTrackingRange(20)
			.setShouldReceiveVelocityUpdates(false)
			.fireImmune()
			.build(BUBBLE_FLAME)
	);

	public static final DeferredHolder<EntityType<?>, EntityType<GlowglobEntity>> GLOWGLOB_ENTITY = Registration.ENTITIES.register(
			GLOWGLOB, () -> EntityType.Builder.of(GlowglobEntity::new, MobCategory.CREATURE)
			.sized(0.55F, 0.55F)
			.clientTrackingRange(8)
			.setTrackingRange(20)
			.setShouldReceiveVelocityUpdates(false)
			.fireImmune()
			.build(GLOWGLOB)
	);

	public static final DeferredHolder<EntityType<?>, EntityType<GlowglobBallEntity>> GLOWGLOB_BALL_ENTITY = Registration.ENTITIES.register(
			GLOWGLOB_BALL, () -> EntityType.Builder.<GlowglobBallEntity>of(GlowglobBallEntity::new, MobCategory.MISC)
			.sized(0.25f, 0.25f)
			.build(GLOWGLOB_BALL)
	);

	// particles
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> REVELATION_PARTICLE = Registration.PARTICLES.register("revelation_particle", () -> new SimpleParticleType(true));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GREATER_REVELATION_PARTICLE = Registration.PARTICLES.register("greater_revelation_particle", () -> new SimpleParticleType(true));
	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BUBBLE_FLAME_PARTICLE = Registration.PARTICLES.register("bubble_flame_particle", () -> new SimpleParticleType(true));


	/**
	 * 
	 */
	public static void init(IEventBus eventBus) {
		ModItems.register(eventBus);
		ENTITIES.register(eventBus);
		PARTICLES.register(eventBus);
	}
}
