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
import mod.gottsch.neoforge.mageflame.core.entity.creature.ISummonedEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * @author Mark Gottschling on 1/24/2025
 */
public record LifespanUpdateS2C(int entityId, int lifespan) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<LifespanUpdateS2C> TYPE = new CustomPacketPayload.Type<>(ModNetwork.LIFESPAN_UPDATE_S2C_ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, LifespanUpdateS2C> CODEC =
			StreamCodec.composite(ByteBufCodecs.INT, LifespanUpdateS2C::entityId,
			ByteBufCodecs.INT, LifespanUpdateS2C::lifespan,
			LifespanUpdateS2C::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handleDataOnMain(final LifespanUpdateS2C data, final IPayloadContext context) {
		MageFlame.LOGGER.debug("client received packet");

		// get the entity by uuid
		Entity entity = context.player().level().getEntity(data.entityId);
		if (entity instanceof ISummonedEntity) {
			((ISummonedEntity)entity).setLifespan(data.lifespan());
		}
	}
}
