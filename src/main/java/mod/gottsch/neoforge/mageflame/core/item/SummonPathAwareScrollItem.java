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

import mod.gottsch.neoforge.mageflame.core.entity.creature.ISummonedEntity;
import mod.gottsch.neoforge.mageflame.core.util.LangUtil;
import mod.gottsch.neoforge.mageflame.core.util.SpawnUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;
import java.util.Objects;

/**
 * Created by Mark Gottschling on 1/14/2025
 */
public abstract class SummonPathAwareScrollItem extends Item implements ISummonScrollItem {

    public SummonPathAwareScrollItem(Item.Properties properties) {

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
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!(world instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack itemStack = context.getItemInHand();
            BlockPos blockPos = context.getClickedPos();
            Direction direction = context.getClickedFace();
            BlockState blockState = world.getBlockState(blockPos);

            BlockPos blockPos2;
            if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
                blockPos2 = blockPos;
            } else {
                blockPos2 = blockPos.relative(direction);
            }

            EntityType entityType = getSummonFlameEntity();
            Entity mob = entityType.spawn((ServerLevel)world, itemStack, context.getPlayer(), blockPos2, MobSpawnType.MOB_SUMMONED, true, !Objects.equals(blockPos, blockPos2) && direction == Direction.UP);
            if (mob != null) {
                itemStack.shrink(1);
                world.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);

                Player owner = context.getPlayer();
                postSpawn((ServerLevel)world, (Mob) mob, entityType, owner);
            }

            return InteractionResult.CONSUME;
        }
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(world, user, ClipContext.Fluid.SOURCE_ONLY);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemStack);
        } else if (!(world instanceof ServerLevel)) {
            return InteractionResultHolder.success(itemStack);
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!(world.getBlockState(blockPos).getBlock() instanceof LiquidBlock)) {
                return InteractionResultHolder.pass(itemStack);
            } else if (world.mayInteract(user, blockPos) && user.mayUseItemAt(blockPos, blockHitResult.getDirection(), itemStack)) {

                EntityType entityType = getSummonFlameEntity();
                Entity mob = entityType.spawn((ServerLevel)world, itemStack, user, blockPos, MobSpawnType.MOB_SUMMONED, false, false);
                if (mob == null) {
                    return InteractionResultHolder.pass(itemStack);
                } else {
                    if (!user.getAbilities().instabuild) {
                        itemStack.shrink(1);
                    }
                    user.awardStat(Stats.ITEM_USED.get(this));

                    itemStack.shrink(1);
                    world.gameEvent(user, GameEvent.ENTITY_PLACE, blockPos);
                    postSpawn((ServerLevel)world, (Mob) mob, entityType, user);

                    return InteractionResultHolder.consume(itemStack);
                }
            } else {
                return InteractionResultHolder.fail(itemStack);
            }
        }
    }

    public <T extends Mob & ISummonedEntity>void postSpawn(ServerLevel world, Mob mob, EntityType<T> entityType, LivingEntity owner) {
        ((ISummonedEntity)mob).setOwner(owner);

        // registry entity
        SpawnUtil.register(world, entityType, mob, owner);

        // cast effects
        doCastEffects(world, owner);
    }

}
