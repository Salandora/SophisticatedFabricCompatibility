package com.github.salandora.sophisticatedfabricintegrations.init;

import com.github.salandora.sophisticatedfabricintegrations.SophisticatedFabricIntegrations;
import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ModPayloads {
	private static int index = 0;
	public static final ResourceLocation CHANNEL_NAME = SophisticatedFabricIntegrations.getRL("channel");
	private static final SimpleChannel channel = new SimpleChannel(CHANNEL_NAME);

	private ModPayloads() {
	}

	public static void registerPayloads() {
		channel.initServerListener();
	}

	public static void registerClientPayloads() {
	}

	public static SimpleChannel getChannel() {
		return channel;
	}

	public static <T extends C2SPacket> void registerC2S(Class<T> type, Function<FriendlyByteBuf, T> factory) {
		channel.registerC2SPacket(type, index++, factory);
	}
}
