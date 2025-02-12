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
package mod.gottsch.neoforge.mageflame.core.persistence;

import com.google.common.collect.Maps;
import mod.gottsch.neo.gottschcore.spatial.Coords;
import mod.gottsch.neo.gottschcore.spatial.ICoords;
import mod.gottsch.neoforge.mageflame.core.util.ModUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;

import java.util.*;

/**
 * Created by Mark Gottschling on 1/14/2025
 */
public class PlayerData {
    public static final String DIMENSION = "dimension";

    /*
     * map of summoned entity data
     * key = entity.uuid
     */
    private final Map<UUID, SummonedEntityData> entityRegistry = Maps.newHashMap();

    public Optional<SummonedEntityData> register(UUID key, SummonedEntityData value) {
        if (key == null || value == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entityRegistry.put(key, value));
    }

    public boolean isRegistered(UUID key) {
        if (entityRegistry.containsKey(key)) {
            return true;
        }
        return false;
    }

    public Optional<SummonedEntityData> unregister(UUID key) {
        if (key != null && isRegistered(key)) {
            return Optional.of(entityRegistry.remove(key));
        }
        return Optional.empty();
    }

    public Optional<SummonedEntityData> get(UUID key) {
        if (entityRegistry.containsKey(key)) {
            return Optional.of(entityRegistry.get(key));
        }
        return Optional.empty();
    }

    public Optional<SummonedEntityData> get(int index) {
        if (!entityRegistry.isEmpty() && index < entityRegistry.size()) {
            Object o = entityRegistry.values().toArray()[index];
        }
        return Optional.empty();
    }


    public void clear() {
        entityRegistry.clear();
    }

    public Map<UUID, SummonedEntityData> getDetachedRegistry() {
        Map<UUID, SummonedEntityData> map = Maps.newHashMap();
        map.putAll(entityRegistry);
        return map;
    }

    public CompoundTag writeNbt(CompoundTag nbt) {
        entityRegistry.forEach((key, value) -> {
            CompoundTag data = new CompoundTag();
            if (value.getId() != null) {
                data.putUUID("id", value.getId());
            }
            data.putString(DIMENSION, value.getDimension().toString());
            data.putString("type", BuiltInRegistries.ENTITY_TYPE.getKey(value.getEntityType()).toString());
            data.putInt("lifespan", value.getLifespan());
            data.putLong("createTime", value.getCreateTime());
            CompoundTag coords = new CompoundTag();
            if (value.getCoords() != null && value.getCoords() != Coords.EMPTY) {
                data.put("coords", value.getCoords().save(coords));
            }
            nbt.put(key.toString(), data);
        });
        return nbt;
    }

    public void loadNbt(CompoundTag nbt) {
        nbt.getAllKeys().forEach(key -> {
            SummonedEntityData data = new SummonedEntityData();
            CompoundTag summonedNbt = nbt.getCompound(key);

            if (summonedNbt.contains("id")) {
                UUID id = summonedNbt.getUUID("id");
                data.setId(id);
            }

            if (summonedNbt.contains(DIMENSION)) {
                data.setDimension(ModUtil.asLocation(summonedNbt.getString(DIMENSION)));
            } else {
                data.setDimension(BuiltinDimensionTypes.OVERWORLD.location());
            }

            if (summonedNbt.contains("type")) {
                EntityType entityType = BuiltInRegistries.ENTITY_TYPE.get(ModUtil.asLocation(summonedNbt.getString("type")));
                data.setEntityType(entityType);
            }
            if (summonedNbt.contains("lifespan")) {
                int lifespan = summonedNbt.getInt("lifespan");
                data.setLifespan(lifespan);
            }
            if (summonedNbt.contains("createTime")) {
                long createTime = summonedNbt.getLong("createTime");
                data.setCreateTime(createTime);
            }
            if (summonedNbt.contains("coords")) {
                ICoords coords = Coords.EMPTY.load(summonedNbt.getCompound("coords"));
                data.setCoords(coords);
            }

            UUID uuid = UUID.fromString(key);
            register(uuid, data);
        });
    }

    public List<UUID> getKeys() {
        return new ArrayList<>(entityRegistry.keySet());
    }

    public List<SummonedEntityData> getValues() {
        return new ArrayList<>(entityRegistry.values());
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "entityRegistry=" + entityRegistry +
                '}';
    }
}
