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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mage Flame.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neoforge.mageflame.core.client.renderer.entity;

import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.client.model.entity.EmberHoundModel;
import mod.gottsch.neoforge.mageflame.core.entity.creature.EmberHoundEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 *
 * @author Mark Gottschling Jan 18, 2025
 *
 */
public class EmberHoundRenderer<T extends EmberHoundEntity> extends MobRenderer<T, EmberHoundModel<T>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MageFlame.MOD_ID, "textures/entity/ember_hound.png");

    public EmberHoundRenderer(EntityRendererProvider.Context context) {
        super(context, new EmberHoundModel<>(context.bakeLayer(EmberHoundModel.LAYER_LOCATION)), 0);
    }

    @Override
    public ResourceLocation getTextureLocation(EmberHoundEntity entity) {
        return TEXTURE;
    }
}
