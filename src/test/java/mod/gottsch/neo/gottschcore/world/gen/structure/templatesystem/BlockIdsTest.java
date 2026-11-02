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

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link BlockIds}, and the vanilla behaviour it exists to work around.
 *
 * @author Mark Gottschling on Aug 11, 2026
 */
class BlockIdsTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static DataResult<Block> decode(String id) {
        return BlockIds.CODEC.parse(JsonOps.INSTANCE, new com.google.gson.JsonPrimitive(id));
    }

    /**
     * <strong>Why {@link BlockIds} exists on 1.21.1, asserted rather than asserted-in-prose.</strong>
     * Here {@code byNameCodec()} looks the id up with {@code getHolder()}, so an unknown id is a
     * hard decode error -- one that would fail a whole datapack over a block from a mod the player
     * does not have. If Mojang ever makes it lenient again, this test fails and the class notes
     * need revisiting.
     */
    @Test
    void vanillasByNameCodecRejectsAnUnknownId() {
        DataResult<Block> result = BuiltInRegistries.BLOCK.byNameCodec()
                .parse(JsonOps.INSTANCE, new com.google.gson.JsonPrimitive("dungeonblocks:mosy_bricks"));

        assertTrue(result.error().isPresent(), "expected vanilla to reject an unregistered id");
    }

    /** The replacement keeps that lenient result -- GottschCore is shared, see the class notes. */
    @Test
    void anUnknownIdStillResolvesToAirRatherThanFailingTheLoad() {
        DataResult<Block> result = decode("dungeonblocks:mosy_bricks");
        assertTrue(result.error().isEmpty(), "a shared library must not fail another mod's pack");
        assertSame(Blocks.AIR, result.result().orElseThrow());
    }

    @Test
    void aRealIdDecodesToItsBlock() {
        assertSame(Blocks.STONE_BRICK_STAIRS, decode("minecraft:stone_brick_stairs")
                .result().orElseThrow());
    }

    /** Air authored on purpose is a legitimate decay target and must not be warned about. */
    @Test
    void airItselfDecodesToAir() {
        assertSame(Blocks.AIR, decode("minecraft:air").result().orElseThrow());
    }

    /** A malformed id is a different fault, and that one vanilla does reject. */
    @Test
    void aMalformedIdIsStillAHardError() {
        assertTrue(decode("Not An Id").error().isPresent());
    }

    /** Round-trips, so an encoded processor list still names its blocks. */
    @Test
    void aBlockRoundTripsThroughItsId() {
        var json = BlockIds.CODEC.encodeStart(JsonOps.INSTANCE, Blocks.MOSSY_STONE_BRICKS)
                .result().orElseThrow();
        assertEquals("minecraft:mossy_stone_bricks", json.getAsString());
        assertSame(Blocks.MOSSY_STONE_BRICKS, BlockIds.CODEC.parse(JsonOps.INSTANCE, json)
                .result().orElseThrow());
    }
}
