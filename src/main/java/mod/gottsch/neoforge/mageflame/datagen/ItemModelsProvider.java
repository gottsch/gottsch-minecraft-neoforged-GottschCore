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
package mod.gottsch.neoforge.mageflame.datagen;

import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 
 * @author Mark Gottschling Jan 20, 2023
 *
 */
public class ItemModelsProvider extends ItemModelProvider {

	public ItemModelsProvider(PackOutput generator, ExistingFileHelper existingFileHelper) {
		super(generator, MageFlame.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		singleTexture(
				ModItems.MAGE_FLAME_SCROLL.getId().getPath(),
				mcLoc("item/generated"), "layer0", modLoc("item/mage_flame_scroll"));
		singleTexture(
				ModItems.LESSER_REVELATION_SCROLL.getId().getPath(),
				mcLoc("item/generated"), "layer0", modLoc("item/lesser_revelation_scroll"));
		singleTexture(
				ModItems.GREATER_REVELATION_SCROLL.getId().getPath(),
				mcLoc("item/generated"), "layer0", modLoc("item/greater_revelation_scroll"));
		singleTexture(
				"winged_torch_scroll",
				mcLoc("item/generated"), "layer0", modLoc("item/winged_torch_scroll"));
		singleTexture(
				"bubble_flame_scroll",
				mcLoc("item/generated"), "layer0", modLoc("item/bubble_flame_scroll"));
		singleTexture(
				"ember_hound_scroll",
				mcLoc("item/generated"), "layer0", modLoc("item/ember_hound_scroll"));
		singleTexture(
				"glowglob_ball",
				mcLoc("item/generated"), "layer0", modLoc("item/glowglob_ball"));
	}
}
