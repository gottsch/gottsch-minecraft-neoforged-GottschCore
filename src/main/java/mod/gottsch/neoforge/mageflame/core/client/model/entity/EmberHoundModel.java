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
import mod.gottsch.neoforge.mageflame.core.entity.creature.SummonedPathAwareEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;


/**
 *
 * @author Mark Gottschling Jan 11, 2025
 *
 */
public class EmberHoundModel<T extends SummonedPathAwareEntity> extends EntityModel<T> {
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MageFlame.MOD_ID, "ember_hound"), "main");

	private final ModelPart head;
	private final ModelPart torso;
	private final ModelPart neck;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;

	private float bodyY;

	/**
	 *
	 * @param root
	 */
	public EmberHoundModel(ModelPart root) {
		this.head = root.getChild("head");
		this.torso = root.getChild("body");
		this.neck = root.getChild("upperBody");
		this.rightHindLeg = root.getChild("rightRearLeg");
		this.leftHindLeg = root.getChild("leftRearLeg");
		this.rightFrontLeg = root.getChild("rightFrontLeg");
		this.leftFrontLeg = root.getChild("leftFrontLeg");
		this.tail = root.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(25, 14).addBox(-3.0F, -4.0F, -4.0F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(38, 36).addBox(-3.0F, -6.0F, -2.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(38, 36).addBox(1.0F, -6.0F, -2.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
				.texOffs(25, 25).addBox(-1.5F, -1.0156F, -7.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 13.5F, -5.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
				.texOffs(33, 33).addBox(-1.0F, -2.0F, 3.0F, 0.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, 2.0F, 1.5708F, 0.0F, 0.0F));
		PartDefinition upperBody = partdefinition.addOrReplaceChild("upperBody", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -3.0F, 8.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 14.0F, 2.0F, 1.5708F, 0.0F, 0.0F));
		PartDefinition cube_r1 = upperBody.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(38, 33).addBox(2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -8.0F, 4.0F, -0.7854F, 0.0F, 0.0F));
		PartDefinition cube_r2 = upperBody.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(9, 32).addBox(0.0F, 0.0F, 0.0F, 4.0F, 0.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -6.0F, 4.0F, -0.7854F, 0.0F, 0.0F));
		PartDefinition cube_r3 = upperBody.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(31, 11).addBox(-2.0F, 0.0F, 0.0F, 6.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -4.0F, 4.0F, -0.7854F, 0.0F, 0.0F));
		PartDefinition cube_r4 = upperBody.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 30).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 0.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 4.0F, -0.7854F, 0.0F, 0.0F));
		PartDefinition rightRearLeg = partdefinition.addOrReplaceChild("rightRearLeg", CubeListBuilder.create().texOffs(31, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.5F, 16.0F, 7.0F));
		PartDefinition leftRearLeg = partdefinition.addOrReplaceChild("leftRearLeg", CubeListBuilder.create().texOffs(31, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 16.0F, 7.0F));
		PartDefinition rightFrontLeg = partdefinition.addOrReplaceChild("rightFrontLeg", CubeListBuilder.create().texOffs(31, 0).mirror().addBox(-2.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.5F, 16.0F, -4.0F));
		PartDefinition leftFrontLeg = partdefinition.addOrReplaceChild("leftFrontLeg", CubeListBuilder.create().texOffs(31, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(18, 36).addBox(1.0F, 5.0F, 1.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(18, 36).addBox(-4.0F, 5.0F, 1.0F, 0.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 16.0F, -4.0F));
		PartDefinition tail = partdefinition.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
				.texOffs(24, 33).addBox(0.0F, 5.0F, -2.0F, 0.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
				.texOffs(9, 36).addBox(-2.0F, 5.0F, 0.0F, 4.0F, 3.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 12.0F, 8.0F, 0.9599F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float tickDelta) {
		this.tail.yRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

//		this.torso.setPivot(0.0F, 14.0F, 2.0F);
//		this.torso.pitch = ((float)Math.PI / 2F);
//		this.neck.setPivot(-1.0F, 14.0F, -3.0F);
//		this.neck.pitch = this.torso.pitch;
		this.tail.setPos(-1.0F, 12.0F, 8.0F);
		this.rightHindLeg.setPos(-2.5F, 16.0F, 7.0F);
		this.leftHindLeg.setPos(0.5F, 16.0F, 7.0F);
		this.rightFrontLeg.setPos(-2.5F, 16.0F, -4.0F);
		this.leftFrontLeg.setPos(0.5F, 16.0F, -4.0F);
		this.rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		this.rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		this.leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		this.head.yRot = headYaw * ((float)Math.PI / 180F);
//		this.tail.pitch = age;
	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int pColor) {
		head.render(matrices, vertexConsumer, light, overlay);
		torso.render(matrices, vertexConsumer, light, overlay);
		neck.render(matrices, vertexConsumer, light, overlay);
		rightHindLeg.render(matrices, vertexConsumer, light, overlay);
		leftHindLeg.render(matrices, vertexConsumer, light, overlay);
		rightFrontLeg.render(matrices, vertexConsumer, light, overlay);
		leftFrontLeg.render(matrices, vertexConsumer, light, overlay);
		tail.render(matrices, vertexConsumer, light, overlay);
	}

	public static void bob(ModelPart part, float originY, float age) {
		part.y = originY + (Mth.cos(age * 0.25F) * 0.5F + 0.05F);
	}
}