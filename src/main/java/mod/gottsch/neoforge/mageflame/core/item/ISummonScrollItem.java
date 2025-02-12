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
package mod.gottsch.neoforge.mageflame.core.item;

import mod.gottsch.neo.gottschcore.spatial.ICoords;
import mod.gottsch.neoforge.mageflame.core.entity.creature.ISummonedEntity;
import mod.gottsch.neoforge.mageflame.core.util.LangUtil;
import mod.gottsch.neoforge.mageflame.core.util.SpawnUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * 
 * @author Mark Gottschling Jan 20, 2023
 *
 */
public interface ISummonScrollItem {

	<T extends Mob & ISummonedEntity> EntityType<T> getSummonFlameEntity();

	default public String ticksToTime(int ticks) {
		int secs = ticks / 20;
		int hours = secs / 3600;
		int remainder = secs % 3600;
		int minutes = remainder / 60;
		int seconds = remainder % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	default public void appendBaseText(ItemStack stack, Item.TooltipContext level, List<Component> tooltip, TooltipFlag type) {
	}

	default public void appendAdvancedText(ItemStack stack, Item.TooltipContext level, List<Component> tooltip, TooltipFlag type) {
	}

	default public void appendLore(ItemStack stack, Item.TooltipContext level, List<Component> tooltip, TooltipFlag type, String key) {
		MutableComponent lore = Component.translatable(LangUtil.tooltip(key));
		tooltip.add(Component.literal(" "));
		for (String s : lore.getString().split("~")) {
			tooltip.add(Component.translatable(LangUtil.INDENT2)
					.append(Component.literal(s).withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)));
		}
	}

	/**
	 * 
	 * @param player
	 * @return
	 */
	default public Vec3 getByPlayerPos(Player player) {
		Vec3 eyePos = player.getEyePosition();
		Direction direction = player.getDirection();
		return switch (direction) {
		case NORTH -> eyePos.add(new Vec3(0.5, 0, 0.35));
		case SOUTH -> eyePos.add(new Vec3(-0.5, 0, -0.35));
		case EAST -> eyePos.add(new Vec3(-0.35, 0, 0.5));
		case WEST -> eyePos.add(new Vec3(0.35, 0, -0.5));
		default -> eyePos.add(new Vec3(0.5, 0, 0.35));
		};
	}

	/**
	 * 
	 * @param level
	 * @param random
	 * @param owner
	 * @param entityType
	 * @param coords
	 * @return
	 */

	default public <T extends Mob & ISummonedEntity> Optional<?> spawn(ServerLevel level, RandomSource random, LivingEntity owner, EntityType<T> entityType, ICoords coords) {
		return SpawnUtil.spawnAtPos(level, random, owner, entityType, coords);
	}

	default public void doCastEffects(Level world, LivingEntity owner) {
		for (int p = 0; p < 20; p++) {
			double xSpeed = world.random.nextGaussian() * 0.02D;
			double ySpeed = world.random.nextGaussian() * 0.02D;
			double zSpeed = world.random.nextGaussian() * 0.02D;

			world.addParticle(ParticleTypes.POOF, owner.getX(), owner.getY() + 0.5, owner.getZ(), xSpeed, ySpeed, zSpeed);
		}
	}

	/**
	 * TODO this might need to move to a Util 
	 * @param level
	 * @param coords
	 * @param direction
	 * @return
	 */
	default public Vec3 selectSpawnPos(Level level, Vec3 coords, Direction direction) {
		return SpawnUtil.selectSpawnPos(level, coords, direction);
	}

	private BlockPos vec3ToBlockPos(Vec3 vec3) {
		return new BlockPos((int)vec3.x, (int)vec3.y, (int)vec3.z);
	}
}
