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


import mod.gottsch.neo.gottschcore.spatial.Coords;
import mod.gottsch.neo.gottschcore.spatial.ICoords;
import mod.gottsch.neoforge.mageflame.core.util.LangUtil;
import mod.gottsch.neoforge.mageflame.core.util.SpawnUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
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
 * @author Mark Gottschling Jan 22, 2023
 *
 */
public abstract class SummonFlyingScrollItem extends Item implements ISummonScrollItem {

	/**
	 * 
	 * @param properties
	 */
	public SummonFlyingScrollItem(Properties properties) {

		super(properties);
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable(this.getDescriptionId(stack)).withStyle(ChatFormatting.AQUA);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
		appendBaseText(stack, context, tooltip, type);
		LangUtil.appendAdvancedHoverText(tooltip, tt -> {
			appendAdvancedText(stack, context, tooltip, type);
		});
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack heldStack = player.getItemInHand(hand);
		if (level.isClientSide) {
			return InteractionResultHolder.pass(heldStack);
		}
		Direction direction = player.getDirection();
		Vec3 playerPos = getByPlayerPos(player);
		Vec3 spawnVec3 = selectSpawnPos(level, playerPos, direction);
//		BlockPos spawnPos = new BlockPos((int)spawnVec3.x, (int)spawnVec3.y, (int)spawnVec3.z);
		ICoords coords = Coords.of(SpawnUtil.vec3ToBlockPos(spawnVec3));
		// spawn entity
		// MageFlame.LOGGER.info("using summon flame item...");
		Optional<?> mob = spawn((ServerLevel) level, level.random, player, getSummonFlameEntity(), coords);
		if (mob.isPresent()) {
			// MageFlame.LOGGER.info("summon flame is present...");
			// reduce scroll stack size ie consume
			heldStack.shrink(1);
			return InteractionResultHolder.consume(heldStack);
		}
		return super.use(level, player, hand);
	}
}
