package org.github.salandora.sophisticatedfabriccompat.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class ModPayloads {
	private ModPayloads() {
	}

	public static void registerPayloads() {
	}

	public static void registerClientPayloads() {
	}

	public static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
		PayloadTypeRegistry.playC2S().register(id, codec);
		ServerPlayNetworking.registerGlobalReceiver(id, handler);
	}
}
