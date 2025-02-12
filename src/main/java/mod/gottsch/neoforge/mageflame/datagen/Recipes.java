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

import mod.gottsch.neoforge.mageflame.core.item.ModItems;
import mod.gottsch.neoforge.mageflame.core.setup.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

/**
 * 
 * @author Mark Gottschling Jan 20, 2023
 *
 */
public class Recipes extends RecipeProvider {

		public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
			super(output, provider);
		}

		@Override
		protected void buildRecipes(RecipeOutput recipe) {
			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GLOWGLOB_BALL.get(), 4)
					.requires(Items.GUNPOWDER)
					.requires(Items.MUD)
					.requires(Items.PAPER)
					.unlockedBy("has", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GUNPOWDER, Items.MUD, Items.PAPER))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MAGE_FLAME_SCROLL.get())
					.requires(Items.TORCH)
					.requires(Items.PAPER)
					.unlockedBy("has_torch", InventoryChangeTrigger.TriggerInstance.hasItems(
							Items.TORCH, Items.PAPER))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LESSER_REVELATION_SCROLL.get())
					.requires(Items.TORCH)
					.requires(Items.FLINT_AND_STEEL)
					.requires(Items.PAPER)
					.unlockedBy("has_torch", InventoryChangeTrigger.TriggerInstance.hasItems(
							Items.TORCH, Items.PAPER, Items.FLINT_AND_STEEL))
					.save(recipe);

			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.GREATER_REVELATION_SCROLL.get())
					.requires(Items.TORCH)
					.requires(Items.FLINT_AND_STEEL)
					.requires(Items.GLOWSTONE_DUST)
					.requires(Items.PAPER)
					.unlockedBy("has_torch", InventoryChangeTrigger.TriggerInstance.hasItems(
							Items.TORCH, Items.FLINT_AND_STEEL, Items.GLOWSTONE_DUST, Items.PAPER))
					.save(recipe);

			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WINGED_TORCH_SCROLL.get())
					.pattern(" e ")
					.pattern("ftf")
					.pattern("spb")
					.define('e', Items.SPIDER_EYE)
					.define('f', Items.FEATHER)
					.define('t', Items.TORCH)
					.define('s', Items.FLINT_AND_STEEL)
					.define('p', Items.PAPER)
					.define('b', Items.BLAZE_POWDER)
					.unlockedBy("has_torch", InventoryChangeTrigger.TriggerInstance.hasItems(
							Items.TORCH, Items.SPIDER_EYE, Items.FEATHER, Items.FLINT_AND_STEEL, Items.PAPER, Items.BLAZE_POWDER))
					.save(recipe);

			ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EMBER_HOUND_SCROLL.get())
					.pattern(" d ")
					.pattern("ltl")
					.pattern("spb")
					.define('d', Items.DIAMOND)
					.define('l', Items.LEATHER)
					.define('t', Items.TORCH)
					.define('s', Items.FLINT_AND_STEEL)
					.define('p', Items.PAPER)
					.define('b', Items.BLAZE_POWDER)
					.unlockedBy("has_torch", InventoryChangeTrigger.TriggerInstance.hasItems(
							Items.TORCH, Items.DIAMOND, Items.LEATHER, Items.FLINT_AND_STEEL, Items.PAPER, Items.BLAZE_POWDER))
					.save(recipe);

			// bubble flame
			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BUBBLE_FLAME_SCROLL.get(), 1)
					.requires(Items.TORCH)
					.requires(Items.PAPER)
					.requires(Items.FLINT_AND_STEEL)
					.requires(Items.TURTLE_EGG)
					.requires(Items.GLOWSTONE_DUST)
					.unlockedBy("has_torch", InventoryChangeTrigger.TriggerInstance.hasItems(
							Items.TORCH, Items.PAPER, Items.TURTLE_EGG, Items.FLINT_AND_STEEL, Items.GLOWSTONE_DUST))
					.save(recipe);
		}
}
