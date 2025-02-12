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
package mod.gottsch.neoforge.mageflame.core.event;

import mod.gottsch.neo.gottschcore.spatial.Coords;
import mod.gottsch.neo.gottschcore.spatial.ICoords;
import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.entity.creature.ISummonedEntity;
import mod.gottsch.neoforge.mageflame.core.entity.creature.SummonedFlyingEntity;
import mod.gottsch.neoforge.mageflame.core.persistence.PlayerData;
import mod.gottsch.neoforge.mageflame.core.persistence.StateSaverAndLoader;
import mod.gottsch.neoforge.mageflame.core.persistence.SummonedEntityData;
import mod.gottsch.neoforge.mageflame.core.util.SpawnUtil;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

import java.util.*;

/**
 * 
 * @author Mark Gottschling on Nov 6, 2022
 *
 */
@EventBusSubscriber(modid = MageFlame.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class LivingEntityEventHandler {

	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
		if (event.getEntity().level().isClientSide) {
			return;
		}
		if (event.getEntity() instanceof Player player) {
			// load registry
//			MageFlame.LOGGER.info("player entity joining world -> {}, {}", player.getName().getString(), event.getLevel().dimension().location().toString());

			Level level = event.getLevel();
			PlayerData playerData = StateSaverAndLoader.getPlayerState((LivingEntity) player);
			Map<UUID, SummonedEntityData> entityDataMap = playerData.getDetachedRegistry();
//			MageFlame.LOGGER.info("size of playerData registry -> {}", entityDataMap.size());
			playerData.clear();
			List<SummonedEntityData> reAddList = new ArrayList<>();

			ResourceLocation dimensionId = ((ServerLevel)level).dimension().location();
//			MageFlame.LOGGER.info("current dimension -> {}", dimensionId);
			entityDataMap.forEach((id, entityData) -> {
//				MageFlame.LOGGER.info("summon data -> {}", entityData);
				if (entityData.getDimension() == null ||
						!dimensionId.equals(entityData.getDimension())) {
					/*
					 * spawn pos was not saved or player has changed dimensions, spawn near player if possible.
					 */
					Mob mob = entityData.getEntityType().create(level);
					Optional<?> optionalMob;
					if (mob instanceof SummonedFlyingEntity) {
						// TODO these 5 lines are exact from SummonFlyingScrollItem - create method
						Direction direction = player.getDirection();
						Vec3 playerPos = SpawnUtil.getByPlayerPos(player);
						Vec3 spawnVec3 = SpawnUtil.selectSpawnPos(level, playerPos, direction);
						ICoords coords = Coords.of(SpawnUtil.vec3ToBlockPos(spawnVec3));
						optionalMob = SpawnUtil.spawnAtPos((ServerLevel) level, level.random, player, entityData.getEntityType(), coords);
//						MageFlame.LOGGER.info("called spawnAtPos...");
					} else {
						optionalMob = SpawnUtil.spawnAndRegister((ServerLevel) level, level.random, player, entityData.getEntityType(), Coords.of(player.blockPosition()));
//						MageFlame.LOGGER.info("called spawnAndRegister...");
					}
					// if unsuccessful, re-register the entity
					if (optionalMob.isEmpty()) {
//						MageFlame.LOGGER.info("unable to create mob, re-add to playerData");
						reAddList.add(entityData);
					}
					reAddList.forEach(data -> {
						playerData.register(data.getId(), data);
					});
				} else {
					/*
					 * same dimension as when registered. spawn at saved position.
					 */
					SpawnUtil.spawnAtPos((ServerLevel) level, level.random, (LivingEntity) player, entityData.getEntityType(), entityData.getCoords());
//					MageFlame.LOGGER.info("same dimension, spawnAtPos");
				}
			});
		}
	}

	@SubscribeEvent
	public static void onEntityLeaveWorld(EntityLeaveLevelEvent event) {
		if (event.getLevel().isClientSide) {
			return;
		}

		ServerLevel level = (ServerLevel) event.getLevel();
		if (event.getEntity() instanceof Player player) {
			MageFlame.LOGGER.info("player entity leaving world -> {}", player.getName().getString());

			PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
			playerData.getKeys().forEach(modId -> {
				// get the entity from the world
				Entity mob = level.getEntity(modId);
				if (mob instanceof ISummonedEntity lightSourceEntity) {
					// update player's entities
					playerData.get(modId).ifPresent(data -> {
						data.setLifespan(lightSourceEntity.getLifespan());
						data.setCoords(Coords.of(mob.blockPosition()));

					});
					// kill mob
					mob.kill();
				} else {
					// can't find mob so unregister
					playerData.unregister(modId);
				}
			});
		}
	}
}
