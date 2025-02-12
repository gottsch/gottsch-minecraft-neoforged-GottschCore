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


import mod.gottsch.neoforge.mageflame.core.MageFlame;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Mark Gottschling on 1/12/2025
 */
public class StateSaverAndLoader extends SavedData {
    private static final String REGISTRY_TAG = "mageflame_registry";

    public static final Factory<StateSaverAndLoader> FACTORY = new Factory<>(StateSaverAndLoader::new, (compoundTag, provider) -> StateSaverAndLoader.load(compoundTag));

    public Map<UUID, PlayerData> players = new HashMap<>();

    public static PlayerData getPlayerState(LivingEntity player) {
        StateSaverAndLoader stateSaver = get(player.level());

        // Either get the player by the uuid, or we don't have data for him yet, make a new player state
        return stateSaver.players.computeIfAbsent(player.getUUID(), uuid -> new PlayerData());
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
//        MageFlame.LOGGER.info("saving persistent data...");
        CompoundTag playersNbt = new CompoundTag();
        players.forEach((key, value) -> {
            CompoundTag playerNbt = new CompoundTag();

            value.writeNbt(playerNbt);

            playersNbt.put(key.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);
        return nbt;
    }

    public static StateSaverAndLoader load(CompoundTag nbt) {
        StateSaverAndLoader state = new StateSaverAndLoader();

//        MageFlame.LOGGER.info("loading persistent data ie players");
        CompoundTag playersNbt = nbt.getCompound("players");
        playersNbt.getAllKeys().forEach(playerUuid -> {
            PlayerData playerData = new PlayerData();
            CompoundTag data = playersNbt.getCompound(playerUuid);
            playerData.loadNbt(data);
            state.players.put(UUID.fromString(playerUuid), playerData);
        });
        return state;
    }

    public static StateSaverAndLoader get(Level level) {
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        StateSaverAndLoader data = (StateSaverAndLoader) storage.computeIfAbsent(
                StateSaverAndLoader.FACTORY, MageFlame.MOD_ID);
        data.setDirty();
        return data;
    }

//    private static Type<StateSaverAndLoader> type = new Type<>(
//            StateSaverAndLoader::new, // If there's no 'StateSaverAndLoader' yet create one
//            StateSaverAndLoader::load, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
//            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
//    );

//    public static StateSaverAndLoader getServerState(MinecraftServer server) {
//        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
//        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();
//
//        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
//        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
//        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
//        StateSaverAndLoader state = persistentStateManager.getOrCreate(type, MageFlame.MOD_ID);
//
//        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
//        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
//        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
//        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
//        // there were no actual change to any of the mods state (INCREDIBLY RARE).
//        state.markDirty();
//
//        return state;
//    }
}
