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
package mod.gottsch.neoforge.mageflame.core.loot.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.item.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

/**
 * 
 * @author Mark Gottschling Feb 2, 2025
 *
 */
public class AddScrolls extends LootModifier {


	public static final MapCodec<AddScrolls> CODEC =
//			Suppliers.memoize(()
//			-> RecordCodecBuilder.create(inst -> codecStart(inst)
//			.apply(inst, AddScrolls::new)));
			RecordCodecBuilder.mapCodec(inst ->
			LootModifier.codecStart(inst).apply(inst, AddScrolls::new)
		);

	protected AddScrolls(LootItemCondition[] conditionsIn) {
		super(conditionsIn);
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
		MageFlame.LOGGER.debug("executing AddScrolls");

		RandomSource random = context.getRandom();
		if (random.nextInt(100) < 75) {
			generatedLoot.add(new ItemStack(ModItems.MAGE_FLAME_SCROLL.get(), random.nextInt(3) + 1));
		}
		if (random.nextInt(100) < 55) {
			generatedLoot.add(new ItemStack(ModItems.LESSER_REVELATION_SCROLL.get(), random.nextInt(3) + 1));
		}
		if (random.nextInt(100) < 35) {
			generatedLoot.add(new ItemStack(ModItems.GREATER_REVELATION_SCROLL.get(), random.nextInt(2) + 1));
		}
		if (random.nextInt(100) < 15) {
			generatedLoot.add(new ItemStack(ModItems.WINGED_TORCH_SCROLL.get(), random.nextInt()));
		}
		if (random.nextInt(100) < 15) {
			generatedLoot.add(new ItemStack(ModItems.EMBER_HOUND_SCROLL.get(), random.nextInt()));
		}
		if (random.nextInt(100) < 25) {
			generatedLoot.add(new ItemStack(ModItems.BUBBLE_FLAME_SCROLL.get(), random.nextInt()));
		}
		if (random.nextInt(100) < 75) {
			generatedLoot.add(new ItemStack(ModItems.GLOWGLOB_BALL.get(), random.nextInt(4) + 2));
		}
		return generatedLoot;
	}
}
