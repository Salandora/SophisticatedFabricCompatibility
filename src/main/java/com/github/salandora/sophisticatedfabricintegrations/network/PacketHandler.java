package com.github.salandora.sophisticatedfabricintegrations.network;

import com.github.salandora.sophisticatedlibrary.network.api.v0.NetworkEvent;
import com.github.salandora.sophisticatedlibrary.network.api.v0.NetworkRegistry;
import com.github.salandora.sophisticatedlibrary.network.api.v0.SimpleChannel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PacketHandler {
	private final SimpleChannel networkWrapper;
	private int idx = 0;

	protected PacketHandler(String modId, String channelName) {
		// The other parameters don't do anything
		networkWrapper = NetworkRegistry.newSimpleChannel(new ResourceLocation(modId, channelName),
				null, null, null);
	}

	public void initServerListener() {
		networkWrapper.initServerListener();
	}

	@Environment(EnvType.CLIENT)
	public void initClientListener() {
		networkWrapper.initClientListener();
	}

	public <M> void registerMessage(Class<M> messageType, BiConsumer<M, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, M> decoder, BiConsumer<M, Supplier<NetworkEvent.Context>> messageConsumer) {
		networkWrapper.registerMessage(idx++, messageType, encoder, decoder, messageConsumer);
	}

	public <M> void sendToServer(M message) {
		networkWrapper.sendToServer(message);
	}

	public <M> void sendToClient(ServerPlayer player, M message) {
		networkWrapper.sendToClient(player, message);
	}
}
