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
package mod.gottsch.neoforge.mageflame.core.item;

import mod.gottsch.neo.gottschcore.world.WorldInfo;
import mod.gottsch.neoforge.mageflame.core.entity.creature.ISummonFlameEntity;
import mod.gottsch.neoforge.mageflame.core.registry.SummonFlameRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * 
 * @author Mark Gottschling Jan 20, 2023
 *
 */
public interface ISummonFlameItem {

	EntityType<? extends Mob> getSummonFlameEntity();
	
	/**
	 * 
	 * @param player
	 * @return
	 */
	default public Vec3 getByPlayerPos(Player player) {
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
	
	/**
	 *
	 */
	default public Optional<Mob> spawn(ServerLevel level, Random random, LivingEntity owner, EntityType<? extends Mob> entityType, Vec3 coords) {
		Direction direction = owner.getDirection();

		if (!WorldInfo.isClientSide(level)) {
			// select the first available spawn pos from origin (coords)
			Vec3 spawnVec3 = selectSpawnPos(level, coords, direction);
			BlockPos spawnPos = new BlockPos((int)spawnVec3.x, (int)spawnVec3.y, (int)spawnVec3.z);
			BlockState state = level.getBlockState(spawnPos);
			if (NaturalSpawner.isValidEmptySpawnBlock(level, spawnPos, state, state.getFluidState(), entityType)) {
				// create entity
				Mob mob = entityType.create(level);
				if (mob != null) {
					// MageFlame.LOGGER.debug("new entity is created -> {}", mob.getStringUUID());
					mob.setPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
					((ISummonFlameEntity)mob).setOwner(owner);

					// MageFlame.LOGGER.debug("is owner registered -> {}", SummonFlameRegistry.isRegistered(owner.getUUID()));
					// check and remove existing owner's entity, regardless if existing entity is located
					if (SummonFlameRegistry.isRegistered(owner.getUUID())) {
						// unregister existing entity for player
						UUID existingUuid = SummonFlameRegistry.unregister(owner.getUUID());
						// MageFlame.LOGGER.debug("owner is registered to entity -> {}", existingUuid.toString());
						Entity existingMob = level.getEntity(existingUuid);
						if (existingMob != null) {
							// MageFlame.LOGGER.debug("located and killing exisiting entity -> {}", existingUuid.toString());
							((LivingEntity)existingMob).die(level.damageSources().generic());
						}
					}

					// registry entity
					// MageFlame.LOGGER.debug("registering entity -> {} to owner -> {}", mob.getUUID(), owner.getUUID());
					SummonFlameRegistry.register(owner.getUUID(), mob.getUUID());

					// add entity into the level (ie EntityJoinWorldEvent)
					level.addFreshEntityWithPassengers(mob);

					// cast effects
					for (int p = 0; p < 20; p++) {
						double xSpeed = random.nextGaussian() * 0.02D;
						double ySpeed = random.nextGaussian() * 0.02D;
						double zSpeed = random.nextGaussian() * 0.02D;
						level.sendParticles(ParticleTypes.POOF, owner.getX(), owner.getY() + 0.5, owner.getZ(), 1, xSpeed, ySpeed, zSpeed, (double)0.15F);
					}

					return Optional.of(mob);
				}
				return Optional.of(mob);
			}
		}
		return Optional.empty();
	}
	
	/**
	 * TODO this might need to move to a Util 
	 * @param level
	 * @param coords
	 * @param direction
	 * @return
	 */
	default public Vec3 selectSpawnPos(Level level, Vec3 coords, Direction direction) {

		if (!level.getBlockState(vec3ToBlockPos(coords)).isAir()) {
			// test to the left
			switch (direction) {
			default:
			case NORTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(-1, 0, 0))).isAir()) coords.add(-1, 0, 0);
			case SOUTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(1, 0, 0))).isAir()) coords.add(1, 0, 0);
			case EAST :
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, 0, -1))).isAir()) coords.add(0, 0, -1);
			case WEST:
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, 0, 1))).isAir()) coords.add(0, 0, 1);
			};

			// test to the left+down
			switch (direction) {
			default:
			case NORTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(-1, -1, 0))).isAir()) coords.add(-1, -1, 0);
			case SOUTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(1, -1, 0))).isAir()) coords.add(1, -1, 0);
			case EAST :
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, -1, -1))).isAir()) coords.add(0, -1, -1);
			case WEST:
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, -1, 1))).isAir()) coords.add(0, -1, 1);
			};

			// test behind
			switch (direction) {
			default:
			case NORTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, 0, 1))).isAir()) coords.add(0, 0, 1);
			case SOUTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, 0, -1))).isAir()) coords.add(0, 0, -1);
			case EAST :
				if (level.getBlockState(vec3ToBlockPos(coords.add(-1, 0, 0))).isAir()) coords.add(-1, 0, 0);
			case WEST:
				if (level.getBlockState(vec3ToBlockPos(coords.add(1, 0, 0))).isAir()) coords.add(1, 0, 0);
			};

			// test down
			if (level.getBlockState(vec3ToBlockPos(coords.add(0, 1, 0))).isAir()) coords.add(0, 1, 0);

			// test right
			switch (direction) {
			default:
			case NORTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(1, 0, 0))).isAir()) coords.add(1, 0, 0);
			case SOUTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(-1, 0, 0))).isAir()) coords.add(-1, 0, 0);
			case EAST :
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, 0, 1))).isAir()) coords.add(0, 0, 1);
			case WEST:
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, 0, -1))).isAir()) coords.add(0, 0, -1);
			};

			// test right+down
			switch (direction) {
			default:
			case NORTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(1, -1, 0))).isAir()) coords.add(1, -1, 0);
			case SOUTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(-1, -1, 0))).isAir()) coords.add(-1, -1, 0);
			case EAST :
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, -1, 1))).isAir()) coords.add(0, -1, 1);
			case WEST:
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, -1, -1))).isAir()) coords.add(0, -1, -1);
			};
		}
		return coords;
	}
	
	private BlockPos vec3ToBlockPos(Vec3 vec3) {
		return new BlockPos((int)vec3.x, (int)vec3.y, (int)vec3.z);
	}
}
