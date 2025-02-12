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
import mod.gottsch.neoforge.mageflame.core.setup.Registration;
import mod.gottsch.neoforge.mageflame.core.util.LangUtil;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/**
 * 
 * @author Mark Gottschling on Apr 6, 2022
 *
 */
public class LanguageGen extends LanguageProvider {

    public LanguageGen(PackOutput gen, String locale) {
        super(gen, MageFlame.MOD_ID, locale);
    }
    
    @Override
    protected void addTranslations() {
        // scrolls
        add(ModItems.MAGE_FLAME_SCROLL.get(), "Mage Flame Scroll");
        add(ModItems.LESSER_REVELATION_SCROLL.get(), "Lesser Revelation Scroll");
        add(ModItems.GREATER_REVELATION_SCROLL.get(), "Greater Revelation Scroll");
        add(ModItems.WINGED_TORCH_SCROLL.get(), "Winged Torch Scroll");
        add(ModItems.BUBBLE_FLAME_SCROLL.get(), "Bubble Flame Scroll");
        add(ModItems.EMBER_HOUND_SCROLL.get(), "Ember Hound Scroll");
        add(ModItems.GLOWGLOB_BALL.get(), "Glowglob Ball");

        // entities
        add(Registration.MAGE_FLAME_ENTITY.get(), "Mage Flame");
        add(Registration.LESSER_REVELATION_ENTITY.get(), "Lesser Revelation");
        add(Registration.GREATER_REVELATION_ENTITY.get(), "Greater Revelation");
        add(Registration.WINGED_TORCH_ENTITY.get(), "Winged Torch");
        add(Registration.BUBBLE_FLAME_ENTITY.get(), "Bubble Flame");
        add(Registration.EMBER_HOUND_ENTITY.get(), "Ember Hound");
        add(Registration.GLOWGLOB_ENTITY.get(), "Glowglob");
        add(Registration.GLOWGLOB_BALL_ENTITY.get(), "Glowglob Ball");

        /*
         *  Util.tooltips
         */
        // general
        add(LangUtil.tooltip("hold_shift"), "Hold [SHIFT] to expand");
        add(LangUtil.tooltip("light_level"), "Light Level: %s");
        add(LangUtil.tooltip("lifespan"), "Lifespan: %s");
        add(LangUtil.tooltip("infinite"), "Infinite");

        add(LangUtil.tooltip("mage_flame.desc"), "Allows the spellcaster to create a small ball of flames.");
        add(LangUtil.tooltip("mage_flame.lore"), "The weakest of the summoned flames,~well-suited for the apprentice spellcaster.~It will allow you to see, but not as bright~as a regular torch.");

        add(LangUtil.tooltip("lesser_revelation.desc"), "A more powerful version of Mage Flame.");
        add(LangUtil.tooltip("lesser_revelation.lore"), "A ball of magic-green fire. It has a more~powerful light and increased lifespan than Mage Flame.");

        add(LangUtil.tooltip("greater_revelation.desc"), "The most powerful of the magical flames.");
        add(LangUtil.tooltip("greater_revelation.lore"), "The spellcaster is able to channel a great~amount of power to generate a large ball~of magic-blue fire. Brighter than a torch~and has staying power.");

        add(LangUtil.tooltip("winged_torch.desc"), "Allows the spellcaster to summon a Winged Torch.");
        add(LangUtil.tooltip("winged_torch.lore"), "The spellcaster is able reach into the astral~plane and summon a Winged Torch. The torch~will remain under your charge until you~release it or it perishes.");

        add(LangUtil.tooltip("ember_hound.desc"), "Allows the spellcaster to summon an Ember Hound.");
        add(LangUtil.tooltip("ember_hound.lore"), "The spellcaster is able reach into the nether~plane and summon an Ember Hound.~The Ember Hound will remain under your charge until~you release it or it perishes (default).");

        add(LangUtil.tooltip("bubble_flame.desc"), "Allows the spellcaster to conjure a Bubble Flame.");
        add(LangUtil.tooltip("bubble_flame.lore"), "A bright flame within its own air bubble.~Able to travel underwater.");

        add(LangUtil.tooltip("glowglob.desc"), "Can be thrown to create a glowglob.");
        add(LangUtil.tooltip("glowglob.lore"), "Creates a weak stationary source of light.~A glowglob ball can be thrown to cast light in~hard to reach places.");

    }
}
