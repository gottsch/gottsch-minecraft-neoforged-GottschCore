/*
 * This file is part of Mage Flame.
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
package mod.gottsch.neoforge.mageflame.core.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.util.SpawnUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;

/**
 * Created by Mark Gottschling on 1/19/2025
 */
public class DispellCommand {
    private static final SuggestionProvider<CommandSourceStack> TYPES = (source, builder) -> {
        // get the players entities
        Player player = source.getSource().getPlayerOrException();
        List<EntityType<?>> entityTypeList = SpawnUtil.getAllSummonedEntities(source.getSource().getLevel(), player);

        List<String> list = new ArrayList<>();
        list.add("all");

        list.addAll(entityTypeList.stream()
                .distinct().map(entityType ->
                        entityType.getDescription().getString()
                ).toList());
        return SharedSuggestionProvider.suggest(list, builder);
    };

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher
                .register(Commands.literal("mageflame")
                        .then(Commands.literal("dispel")
                                .then(Commands.argument("entity", StringArgumentType.greedyString())
                                        .suggests(TYPES)
                                        .executes(context -> killSummonedEntities(context.getSource(), getString(context, "entity")))
                                )
                        )
                );
    }

    public static int killSummonedEntities(CommandSourceStack source, String entityName) {

        try {
            if ("all".equalsIgnoreCase(entityName)) {
                SpawnUtil.killAllSummonedEntities(source.getLevel(), source.getPlayerOrException());
            } else {
                // cycle through each entity and on first match, remove
                SpawnUtil.killSummonedEntity(source.getLevel(), source.getPlayerOrException(), entityName);
            }
        } catch(Exception e) {
            MageFlame.LOGGER.error("error while generating deed:", e);
            source.sendSuccess(() -> error("An error occurred."), false);

        }
        return Command.SINGLE_SUCCESS; // Success
    }

    private static Component error(String message) {
        return Component.literal(message).withStyle(ChatFormatting.RED);
    }
}
