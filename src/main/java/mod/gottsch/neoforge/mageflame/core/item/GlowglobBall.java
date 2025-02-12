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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURCoordsE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Mage Flame.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.neoforge.mageflame.core.item;

import mod.gottsch.neoforge.mageflame.core.config.Config;
import mod.gottsch.neoforge.mageflame.core.entity.projectile.thrown.GlowglobBallEntity;
import mod.gottsch.neoforge.mageflame.core.setup.DynamicLights;
import mod.gottsch.neoforge.mageflame.core.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * @author Mark Gottschling on 1/23/2025
 */
public class GlowglobBall extends Item {

    public GlowglobBall(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        world.playSound(
                null, user.getX(), user.getY(), user.getZ(), SoundEvents.LINGERING_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (!world.isClientSide) {
            GlowglobBallEntity entity = new GlowglobBallEntity(world, user);
            entity.setItem(itemStack);
            entity.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0F, 1.5F, 1.0F);
            world.addFreshEntity(entity);
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
    }

//    @Override
//    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
//        GlowglobBallEntity entity = new GlowglobBallEntity(world, pos.getX(), pos.getY(), pos.getZ());
//        entity.setItem(stack);
//        return entity;
//    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        appendBaseText(stack, context, tooltip, type);
        LangUtil.appendAdvancedHoverText(tooltip, tt -> {
            appendAdvancedText(stack, context, tooltip, type);
        });
    }

    //	@Override
    public void appendBaseText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        // TODO need to add luminance
        tooltip.add(Component.translatable(LangUtil.tooltip("glowglob.desc")).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal(" "));
        tooltip.add(Component.translatable(LangUtil.tooltip("light_level"), DynamicLights.GLOWGLOB_LUMINANCE));
        tooltip.add(Component.translatable(LangUtil.tooltip("lifespan"), ticksToTime(Config.SERVER.glowglobLifespan.get())));
    }

    //	@Override
    public void appendAdvancedText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        appendLore(stack, context, tooltip, "glowglob.lore");
    }

    public void appendLore(ItemStack stack, TooltipContext context, List<Component> tooltip, String key) {
        MutableComponent lore = Component.translatable(LangUtil.tooltip(key));
        tooltip.add(Component.literal(" "));
        for (String s : lore.getString().split("~")) {
            tooltip.add(Component.translatable(LangUtil.INDENT2)
                    .append(Component.literal(s).withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)));
        }
    }

    // TODO probably could be static and create method for formatting
    public String ticksToTime(int ticks) {
        int secs = ticks / 20;
        int hours = secs / 3600;
        int remainder = secs % 3600;
        int minutes = remainder / 60;
        int seconds = remainder % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
