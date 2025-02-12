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

import mod.gottsch.neoforge.mageflame.core.entity.creature.ISummonedEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

/**
 * @author Mark Gottschling on 1/25/2025
 */
public record LifespanUpdateC2S(String uuid, int id) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<LifespanUpdateC2S> TYPE = new CustomPacketPayload.Type<>(ModNetwork.LIFESPAN_UPDATE_C2S_ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, LifespanUpdateC2S> CODEC =
			StreamCodec.composite(ByteBufCodecs.STRING_UTF8, LifespanUpdateC2S::uuid,
			ByteBufCodecs.INT, LifespanUpdateC2S::id,
			LifespanUpdateC2S::new);

	public static void handleDataOnMain(final LifespanUpdateC2S data, final IPayloadContext context) {//        MageFlame.LOGGER.debug("server received packet: uuid ->{}, id -> {}", uuid, id);

		// get the entity by uuid
		UUID entityUuId = UUID.fromString(data.uuid());
		Entity entity = ((ServerPlayer)context.player()).serverLevel().getEntity(entityUuId);
		if (entity == null) {
			entity = ((ServerPlayer)context.player()).serverLevel().getEntity(data.id());
		}
		if (entity instanceof ISummonedEntity) {
			// send a message back to the client with the lifespan
			LifespanUpdateS2C payload = new LifespanUpdateS2C(data.id(), ((ISummonedEntity)entity).getLifespan());
			PacketDistributor.sendToPlayer((ServerPlayer) context.player(), payload);
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
