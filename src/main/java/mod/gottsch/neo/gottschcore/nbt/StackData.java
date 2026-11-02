/*
 * This file is part of  GottschCore.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * GottschCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GottschCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GottschCore.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neo.gottschcore.nbt;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import javax.annotation.Nullable;

/**
 * {@code ItemStack.get/has/set/remove/getOrDefault} for a {@link TagComponent}, kept in the stack's
 * {@code minecraft:custom_data} component. This is the 1.21 side of the Forge 1.20.1 GottschCore's {@code StackData},
 * with the same signatures, so code shared between the two versions calls {@code StackData.get(stack, X)} on both.
 * <p>
 * Why {@code custom_data}: it is where vanilla's data fixer moves a 1.20.1 stack's root NBT when a world is upgraded to
 * 1.21, so a value the Forge build wrote under {@code "modid:name"} reads back here under the same key. New 1.21-only
 * item data is better as a real {@code DataComponentType}.
 * <p>
 * Components sync to clients by themselves, so nothing here needs a packet. Same rules as {@link TagData}, which this
 * delegates to; in addition, removing the last value removes the {@code custom_data} component, so a stack whose data
 * is all removed stacks with a fresh one again.
 *
 * @author Mark Gottschling on 10/16/2026
 */
public final class StackData {
    private StackData() {}

    public static boolean has(ItemStack stack, TagComponent<?> component) {
        return TagData.has(peek(stack), component);
    }

    /** the value, or null when the stack has none or it doesn't decode; never writes to the stack */
    @Nullable
    public static <T> T get(ItemStack stack, TagComponent<T> component) {
        return TagData.get(peek(stack), component);
    }

    public static <T> T getOrDefault(ItemStack stack, TagComponent<T> component, T defaultValue) {
        return TagData.getOrDefault(peek(stack), component, defaultValue);
    }

    /** sets the value; null removes it, as 1.21's {@code set} does */
    public static <T> void set(ItemStack stack, TagComponent<T> component, @Nullable T value) {
        if (value == null) {
            remove(stack, component);
            return;
        }
        // CustomData.update works on a copy and removes the component when the result is empty, so a value that fails
        // to encode leaves no empty custom_data behind.
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> TagData.set(tag, component, value));
    }

    public static void remove(ItemStack stack, TagComponent<?> component) {
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> TagData.remove(tag, component));
        }
    }

    /**
     * The stack's custom data tag for reading only, or null when it has none. Not a copy: every caller here only reads
     * it, which is what keeps {@link #get} cheap enough for tooltip and render code.
     */
    @Nullable
    @SuppressWarnings("deprecation")
    private static CompoundTag peek(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? null : data.getUnsafe();
    }
}
