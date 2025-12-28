package com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core;

import com.github.salandora.sophisticatedfabricintegrations.SophisticatedFabricIntegrations;
import com.github.salandora.sophisticatedfabricintegrations.compat.ICompat;
import com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.network.RequestContentsPayload;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static com.github.salandora.sophisticatedfabricintegrations.init.ModPayloads.registerC2S;

public class LitematicaCompat implements ICompat {
	public record LitematicaWrapper(IStorageWrapper wrapper, Function<UUID, CustomPacketPayload> packetGenerator) {
	}

	public static final ItemApiLookup<LitematicaWrapper, HolderLookup.Provider> LITEMATICA_CAPABILITY = ItemApiLookup.get(SophisticatedFabricIntegrations.getRL("sophisticatedfabriccompat_requestcontents"), LitematicaWrapper.class, HolderLookup.Provider.class);

	public static Optional<LitematicaWrapper> getWrapper(ItemStack provider) {
		return Optional.ofNullable(LITEMATICA_CAPABILITY.find(provider, null));
	}

	@Override
	public void setup() {
	}

	@Override
	public void init() {
		registerC2S(RequestContentsPayload.TYPE, RequestContentsPayload.STREAM_CODEC, RequestContentsPayload::handlePayload);
	}
}