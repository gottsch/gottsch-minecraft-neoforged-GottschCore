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
package mod.gottsch.neoforge.mageflame.core.network;

import mod.gottsch.neoforge.mageflame.core.MageFlame;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 
 * @author Mark Gottschling on Jul 28, 2022
 *
 */
@EventBusSubscriber(modid = MageFlame.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetwork {
	public static final ResourceLocation LIFESPAN_UPDATE_C2S_ID = ResourceLocation.fromNamespaceAndPath(MageFlame.MOD_ID, "lifespan_c2s");
	public static final ResourceLocation LIFESPAN_UPDATE_S2C_ID = ResourceLocation.fromNamespaceAndPath(MageFlame.MOD_ID, "lifespan_s2c");

	public static final String PROTOCOL_VERSION = "1.0";

	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
		// Sets the current network version
		final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
		registrar.playToServer(
				LifespanUpdateC2S.TYPE,
				LifespanUpdateC2S.CODEC,
				new DirectionalPayloadHandler<>(
						LifespanUpdateC2S::handleDataOnMain,
						LifespanUpdateC2S::handleDataOnMain
				)
		);

		registrar.playToClient(
				LifespanUpdateS2C.TYPE,
				LifespanUpdateS2C.CODEC,
				new DirectionalPayloadHandler<>(
						LifespanUpdateS2C::handleDataOnMain,
						LifespanUpdateS2C::handleDataOnMain
				)
		);
	}


}
