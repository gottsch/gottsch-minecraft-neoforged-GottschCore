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
package mod.gottsch.neoforge.mageflame.core.setup;


import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.config.Config;
import mod.gottsch.neoforge.mageflame.core.entity.creature.*;
import mod.gottsch.neoforge.mageflame.core.item.ModItems;
import mod.gottsch.neoforge.mageflame.core.network.ModNetwork;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

/**
 * 
 * @author Mark Gottschling Jan 19, 2023
 *
 */
@EventBusSubscriber(modid = MageFlame.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CommonSetup {
	/**
	 * 
	 * @param event
	 */
	public static void common(final FMLCommonSetupEvent event) {
		// add mod specific logging
		Config.instance.addRollingFileAppender(MageFlame.MOD_ID);

		// treasure2 integration (needs to be registered BEFORE LevelEvent.Load)
//		Integrations.registerTreasure2Integration();
	}
	
	/**
	 * attach defined attributes to the entity.
	 * @param event
	 */
	@SubscribeEvent
	public static void onAttributeCreate(EntityAttributeCreationEvent event) {
		event.put(Registration.MAGE_FLAME_ENTITY.get(), MageFlameEntity.createAttributes().build());
		event.put(Registration.LESSER_REVELATION_ENTITY.get(), LesserRevelationEntity.createAttributes().build());
		event.put(Registration.GREATER_REVELATION_ENTITY.get(), GreaterRevelationEntity.createAttributes().build());
		event.put(Registration.WINGED_TORCH_ENTITY.get(), WingedTorchEntity.createAttributes().build());
		event.put(Registration.EMBER_HOUND_ENTITY.get(), EmberHoundEntity.createWolfAttributes().build());
		event.put(Registration.BUBBLE_FLAME_ENTITY.get(), BubbleFlameEntity.createAttributes().build());
		event.put(Registration.GLOWGLOB_ENTITY.get(), GlowglobEntity.createGlobAttributes().build());
	}

	@SubscribeEvent
	public static void registemItemsToTab(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(ModItems.MAGE_FLAME_SCROLL.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.accept(ModItems.LESSER_REVELATION_SCROLL.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.accept(ModItems.GREATER_REVELATION_SCROLL.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.accept(ModItems.WINGED_TORCH_SCROLL.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.accept(ModItems.BUBBLE_FLAME_SCROLL.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.accept(ModItems.EMBER_HOUND_SCROLL.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
			event.accept(ModItems.GLOWGLOB_BALL.get(), TabVisibility.PARENT_AND_SEARCH_TABS);
		}
	}
}
