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
package mod.gottsch.neoforge.mageflame.core.util;

import mod.gottsch.neoforge.mageflame.core.MageFlame;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

/**
 * @author Mark Gottschling on Jul 22, 2021
 *
 */
public class ModUtil {

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static ResourceLocation asLocation(String name) {
		return hasDomain(name) ? ResourceLocation.parse(name) : ResourceLocation.fromNamespaceAndPath(MageFlame.MOD_ID, name);
	}


	public static ResourceLocation getName(Holder<Biome> biome) {
		return biome.unwrapKey().get().location();	
	}
	
	public static boolean hasDomain(String name) {
		return name.indexOf(":") >= 0;
	}
}
