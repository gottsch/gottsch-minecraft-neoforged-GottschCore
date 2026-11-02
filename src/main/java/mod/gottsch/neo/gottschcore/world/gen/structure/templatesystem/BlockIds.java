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
package mod.gottsch.neo.gottschcore.world.gen.structure.templatesystem;

import com.mojang.serialization.Codec;
import mod.gottsch.neo.gottschcore.GottschCore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * The block-id codec the processors in this package decode with, and the reason they no longer use
 * {@code BuiltInRegistries.BLOCK.byNameCodec()} directly.
 *
 * <h2>What vanilla does instead</h2>
 * <p>On 1.21.1 {@code byNameCodec()} is strict: it looks the id up with {@code getHolder()}, and an
 * unregistered id such as {@code "dungeonblocks:mosy_bricks"} is a decode error
 * ({@code "Unknown registry key in minecraft:block: ..."}) that fails the whole datapack file.
 * (Forge 1.20.1 was the opposite &mdash; its {@code DefaultedMappedRegistry} lookup handed back air
 * silently &mdash; which is where this class started.)</p>
 *
 * <h2>Why it warns rather than failing the load</h2>
 * <p>GottschCore is shared. A hard load error would be right for a mod whose block dependency is
 * mandatory, and wrong for one whose datapack legitimately names blocks from a mod the player may
 * not have installed &mdash; that pack would stop loading entirely rather than losing one rule. So
 * this is <strong>deliberately more lenient than vanilla</strong>: the id resolves to air, and the
 * warning below names it so a typo is still visible in the log. It also keeps unknown ids behaving
 * the same as the Forge 1.20.1 GottschCore.</p>
 *
 * <p>A consumer that wants strictness for its own content should assert it in a test over its own
 * shipped files, where it can fail the build instead of a player's world. That is the split
 * Dungeons2 uses.</p>
 *
 * <h2>Expect noise in unit tests</h2>
 * <p>A bare {@code Bootstrap.bootStrap()} has no mod registries, so <em>every</em> modded id
 * warns there &mdash; and decodes to air, which is why an offline test cannot validate modded ids
 * at all and must check them another way. The warnings are honest: in that environment those blocks
 * genuinely do not exist.</p>
 *
 * @author Mark Gottschling on Aug 11, 2026
 */
public final class BlockIds {

    private BlockIds() {}

    private static final ResourceLocation AIR = ResourceLocation.withDefaultNamespace("air");

    /**
     * Replacement for {@code BuiltInRegistries.BLOCK.byNameCodec()} that resolves an unregistered
     * id to air with a warning naming it, where vanilla would fail the decode.
     *
     * <p>Not deduplicated, deliberately. Datapack codecs decode on load and reload rather than per
     * chunk, so the volume is bounded by the pack's own content &mdash; and a warning that stops
     * repeating on reload would look like a problem that had been fixed.</p>
     */
    public static final Codec<Block> CODEC =
            ResourceLocation.CODEC.xmap(BlockIds::resolve, BuiltInRegistries.BLOCK::getKey);

    private static Block resolve(ResourceLocation id) {
        // containsKey, not a null check on get(): BLOCK is a defaulted registry, so get() hands
        // back air for an unknown id and never returns null.
        if (!BuiltInRegistries.BLOCK.containsKey(id)) {
            if (!AIR.equals(id)) {
                GottschCore.LOGGER.warn(
                        "block id '{}' is not registered; it resolves to minecraft:air, so whatever"
                        + " rule or palette names it will do nothing. Check the spelling, and that"
                        + " the mod providing it is installed.", id);
            }
            return Blocks.AIR;
        }
        return BuiltInRegistries.BLOCK.get(id);
    }
}
