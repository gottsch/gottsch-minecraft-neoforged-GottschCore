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
package mod.gottsch.neoforge.mageflame.core.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.gottsch.neoforge.mageflame.core.MageFlame;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

/**
 * @author Mark Gottschling on 1/21/2025
 */
public class GlowglobModel<T extends Mob> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MageFlame.MOD_ID, "glowglob"), "main");

	private final ModelPart main;
	private final float bodyY;
	private final float scale;

	/**
	 *
	 * @param root
	 */
	public GlowglobModel(ModelPart root) {
		this.main = root.getChild("main");
		this.bodyY = main.y;
		this.scale = main.xScale;
	}

	/**
	 *
	 * @return
	 */
	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -30.0F, -1.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 9).addBox(-3.0F, -26.0F, -1.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(0, 16).addBox(-1.5F, -26.0F, 0.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 50.0F, -1.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Mob entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		bob(this.main, 0.15F, 0.125F, ageInTicks);
		pulse(this.main, 0.075F, 0.05F, ageInTicks);
		rotate(this.main, 0.025F, 0.04F);
	}

	public void bob(ModelPart part, float bobAmount, float speed, float age) {
		part.y = this.bodyY + (Mth.cos(age * speed) * bobAmount); // + 0.05F);
	}

	public void pulse(ModelPart part, float scale, float speed, float age) {
		float changeScale = Mth.cos(age * speed) * scale; // + 0.05F;
		part.xScale = this.scale + changeScale;
		part.zScale = this.scale + changeScale;
		part.yScale = this.scale + changeScale;
	}

	public void rotate(ModelPart part, float radians, float speed) {
		part.yRot = part.yRot + (speed * radians); // + 0.05F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		main.render(poseStack, vertexConsumer, packedLight, packedOverlay);
	}
}