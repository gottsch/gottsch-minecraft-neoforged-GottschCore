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
package mod.gottsch.neoforge.mageflame.core.client.renderer.entity.layers;

import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.client.model.entity.EmberHoundModel;
import mod.gottsch.neoforge.mageflame.core.entity.creature.EmberHoundEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * 
 * @author Mark Gottschling Feb 1, 2025
 *
 */
@OnlyIn(Dist.CLIENT)
public class EmberHoundGlowLayer<T extends EmberHoundEntity, M extends EmberHoundModel<T>> extends EyesLayer<T, M> {
	private static final RenderType GLOW = RenderType.eyes(ResourceLocation.fromNamespaceAndPath(MageFlame.MOD_ID, "textures/entity/emberhound_glow.png"));

	public EmberHoundGlowLayer(RenderLayerParent<T, M> layer) {
		super(layer);
	}

	public RenderType renderType() {
		return GLOW;
	}	   

}
