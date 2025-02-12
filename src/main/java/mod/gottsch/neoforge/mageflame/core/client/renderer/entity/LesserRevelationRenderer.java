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
package mod.gottsch.neoforge.mageflame.core.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.client.model.entity.FlameBallModel;
import mod.gottsch.neoforge.mageflame.core.client.renderer.entity.layers.LesserRevelationGlowLayer;
import mod.gottsch.neoforge.mageflame.core.entity.creature.LesserRevelationEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * 
 * @author Mark Gottschling Jan 23, 2023
 *
 * @param <T>
 */
public class LesserRevelationRenderer<T extends LesserRevelationEntity> extends MobRenderer<T, FlameBallModel<T>> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(MageFlame.MOD_ID, "textures/entity/lesser_revelation.png");
	private final float scale;
	
	/**
	 * 
	 * @param context
	 */
	public LesserRevelationRenderer(EntityRendererProvider.Context context) {
		super(context, new FlameBallModel<>(context.bakeLayer(FlameBallModel.LAYER_LOCATION)), 0F);
		this.addLayer(new LesserRevelationGlowLayer<>(this));
		this.scale = 1.2F;
	}

	@Override
	protected void scale(LesserRevelationEntity  mageFlame, PoseStack pose, float scale) {
		pose.scale(this.scale, this.scale, this.scale);
	}

	@Override
	public ResourceLocation getTextureLocation(LesserRevelationEntity entity) {
		return TEXTURE;
	}

}
