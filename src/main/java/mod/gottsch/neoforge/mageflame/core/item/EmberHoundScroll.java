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

import mod.gottsch.neoforge.mageflame.core.config.Config;
import mod.gottsch.neoforge.mageflame.core.setup.DynamicLights;
import mod.gottsch.neoforge.mageflame.core.setup.Registration;
import mod.gottsch.neoforge.mageflame.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;


/**
 *
 * @author Mark Gottschling Jan 11, 2025
 *
 */
public class EmberHoundScroll extends SummonPathAwareScrollItem {

	public EmberHoundScroll(Item.Properties properties) {

		super(properties);
	}

	@Override
	public EntityType<? extends Mob> getSummonFlameEntity() {

		return Registration.EMBER_HOUND_ENTITY.get();
	}

	@Override
	public void appendBaseText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {

		tooltip.add(Component.translatable(LangUtil.tooltip("ember_hound.desc")).withStyle(ChatFormatting.YELLOW));
		tooltip.add(Component.literal(" "));
		tooltip.add(Component.translatable(LangUtil.tooltip("light_level"), DynamicLights.EMBER_HOUND_LUMINANCE));
		if (!Config.SERVER.isEmberHoundLifespanInfinite.get()) {
			tooltip.add(Component.translatable(LangUtil.tooltip("lifespan"), ticksToTime(Config.SERVER.emberHoundLifespan.get())));
		}
	}

	@Override
	public void appendAdvancedText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
		appendLore(stack, context, tooltip, type,"ember_hound.lore");
	}
}
