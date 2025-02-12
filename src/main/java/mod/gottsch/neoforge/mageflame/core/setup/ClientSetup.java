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
package mod.gottsch.neoforge.mageflame.core.setup;

import mod.gottsch.neoforge.mageflame.core.MageFlame;
import mod.gottsch.neoforge.mageflame.core.client.model.entity.*;
import mod.gottsch.neoforge.mageflame.core.client.renderer.entity.*;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/**
 * Client only event bus subscriber.
 * @author Mark Gottschling on Apr 2, 2022
 *
 */
@EventBusSubscriber(modid = MageFlame.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
    	
    }
    
	/**
	 * register layers
	 * @param event
	 */
	@SubscribeEvent
	public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(FlameBallModel.LAYER_LOCATION, FlameBallModel::createBodyLayer);
		event.registerLayerDefinition(LargeFlameBallModel.LAYER_LOCATION, LargeFlameBallModel::createBodyLayer);
		event.registerLayerDefinition(WingedTorchModel.LAYER_LOCATION, WingedTorchModel::createBodyLayer);
		event.registerLayerDefinition(BubbleFlameModel.LAYER_LOCATION, BubbleFlameModel::createBodyLayer);
		event.registerLayerDefinition(EmberHoundModel.LAYER_LOCATION, EmberHoundModel::createBodyLayer);
		event.registerLayerDefinition(GlowglobModel.LAYER_LOCATION, GlowglobModel::createBodyLayer);
	}

	/**
	 * register renderers
	 * @param event
	 */
	@SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(Registration.MAGE_FLAME_ENTITY.get(), MageFlameRenderer::new);
		event.registerEntityRenderer(Registration.LESSER_REVELATION_ENTITY.get(), LesserRevelationRenderer::new);
		event.registerEntityRenderer(Registration.GREATER_REVELATION_ENTITY.get(), GreaterRevelationRenderer::new);
		event.registerEntityRenderer(Registration.WINGED_TORCH_ENTITY.get(), WingedTorchRenderer::new);
		event.registerEntityRenderer(Registration.BUBBLE_FLAME_ENTITY.get(), BubbleFlameRenderer::new);
		event.registerEntityRenderer(Registration.EMBER_HOUND_ENTITY.get(), EmberHoundRenderer::new);
		event.registerEntityRenderer(Registration.GLOWGLOB_ENTITY.get(), GlowglobRenderer::new);
		event.registerEntityRenderer(Registration.GLOWGLOB_BALL_ENTITY.get(), ThrownItemRenderer::new);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerFactories(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(Registration.REVELATION_PARTICLE.get(), FlameParticle.Provider::new);
		event.registerSpriteSet(Registration.GREATER_REVELATION_PARTICLE.get(), FlameParticle.Provider::new);
		event.registerSpriteSet(Registration.BUBBLE_FLAME_PARTICLE.get(), FlameParticle.Provider::new);
	}
		
}
