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
package mod.gottsch.neoforge.mageflame.core.loot.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import mod.gottsch.neoforge.mageflame.core.MageFlame;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * 
 * @author Mark Gottschling on Feb 2, 2025
 *
 */
public class MageFlameLootModifiers {
	public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
			DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MageFlame.MOD_ID);
	
	public static final Supplier<MapCodec<? extends IGlobalLootModifier>> MAGIC_TREASUREs_LOOT_MODIFIER =
			LOOT_MODIFIER_SERIALIZERS.register("default", () -> AddScrolls.CODEC);

	public static void register(IEventBus bus) {
		LOOT_MODIFIER_SERIALIZERS.register(bus);
	}

}
