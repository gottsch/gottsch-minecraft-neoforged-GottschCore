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
package mod.gottsch.neoforge.mageflame.core.entity.creature;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import mod.gottsch.neo.gottschcore.spatial.ICoords;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

/**
 * 
 * @author Mark Gottschling Jan 21, 2023
 *
 */
public interface ISummonFlameEntity {
	public UUID getOwnerUUID();
	public LivingEntity getOwner();
	public void setOwner(LivingEntity entity);

	public ICoords getCurrentLightCoords();
	public void setCurrentLightCoords(ICoords currentLightCooords);

	public ICoords getLastLightCoords();
	public void setLastLightCoords(ICoords lastLighCoords);
	
	long getBirthTime();
	void setBirthTime(long birthdate);

	double getLifespan();
	void setLifespan(double lifespan);
	
	public void die();
	
	default public boolean canLiveInFluid() {
		return false;
	}

	public boolean updateLightCoords();
	public void updateLightBlocks();

	public @NotNull Block getFlameBlock();
	
	public void doLivingEffects();
	
	void doDeathEffects();
	
	/**
	 * 
	 * @return
	 */
	default public Vec3 selectSummonOffsetPos(LivingEntity entity) {
		Vec3 eyePos = entity.getEyePosition();
		Direction direction = entity.getDirection();
		Vec3 offsetPos = switch (direction) {
			case NORTH -> eyePos.add(new Vec3(0.5, 0, 0.35));
			case SOUTH -> eyePos.add(new Vec3(-0.5, 0, -0.35));
			case EAST -> eyePos.add(new Vec3(-0.35, 0, 0.5));
			case WEST -> eyePos.add(new Vec3(0.35, 0, -0.5));
			default -> eyePos.add(new Vec3(1, 0, 1));
		};
		return offsetPos;
	}
	
	/**
	 * TODO this might need to move to a Util - this is currently duplicated in ISummonFlameItem
	 * @param level
	 * @param coords
	 * @param direction
	 * @return
	 */
	default public Vec3 selectSpawnPos(Level level, Vec3 coords, Direction direction) {

		if (!level.getBlockState(vec3ToBlockPos(coords)).isAir()) {
			// test to the left
			switch (direction) {
			default:
			case NORTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(-1, 0, 0))).isAir()) coords.add(-1, 0, 0);
			case SOUTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(1, 0, 0))).isAir()) coords.add(1, 0, 0);
			case EAST :
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, 0, -1))).isAir()) coords.add(0, 0, -1);
			case WEST:
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, 0, 1))).isAir()) coords.add(0, 0, 1);
			};

			// test to the left+down
			switch (direction) {
			default:
			case NORTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(-1, -1, 0))).isAir()) coords.add(-1, -1, 0);
			case SOUTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(1, -1, 0))).isAir()) coords.add(1, -1, 0);
			case EAST :
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, -1, -1))).isAir()) coords.add(0, -1, -1);
			case WEST:
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, -1, 1))).isAir()) coords.add(0, -1, 1);
			};

			// test behind
			switch (direction) {
			default:
			case NORTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, 0, 1))).isAir()) coords.add(0, 0, 1);
			case SOUTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, 0, -1))).isAir()) coords.add(0, 0, -1);
			case EAST :
				if (level.getBlockState(vec3ToBlockPos(coords.add(-1, 0, 0))).isAir()) coords.add(-1, 0, 0);
			case WEST:
				if (level.getBlockState(vec3ToBlockPos(coords.add(1, 0, 0))).isAir()) coords.add(1, 0, 0);
			};

			// test down
			if (level.getBlockState(vec3ToBlockPos(coords.add(0, 1, 0))).isAir()) coords.add(0, 1, 0);

			// test right
			switch (direction) {
			default:
			case NORTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(1, 0, 0))).isAir()) coords.add(1, 0, 0);
			case SOUTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(-1, 0, 0))).isAir()) coords.add(-1, 0, 0);
			case EAST :
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, 0, 1))).isAir()) coords.add(0, 0, 1);
			case WEST:
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, 0, -1))).isAir()) coords.add(0, 0, -1);
			};

			// test right+down
			switch (direction) {
			default:
			case NORTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(1, -1, 0))).isAir()) coords.add(1, -1, 0);
			case SOUTH:
				if (level.getBlockState(vec3ToBlockPos(coords.add(-1, -1, 0))).isAir()) coords.add(-1, -1, 0);
			case EAST :
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, -1, 1))).isAir()) coords.add(0, -1, 1);
			case WEST:
				if (level.getBlockState(vec3ToBlockPos(coords.add(0, -1, -1))).isAir()) coords.add(0, -1, -1);
			};
		}
		return coords;
	}

	private BlockPos vec3ToBlockPos(Vec3 vec3) {
		return new BlockPos((int)vec3.x, (int)vec3.y, (int)vec3.z);
	}
}
