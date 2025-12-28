package com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core;

import com.github.salandora.sophisticatedfabricintegrations.SophisticatedFabricIntegrations;
import com.github.salandora.sophisticatedfabricintegrations.compat.ICompat;
import com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.network.LitematicaPacketHandler;
import com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.network.RequestContentsMessage;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class LitematicaCompat implements ICompat {
	public record LitematicaWrapper(IStorageWrapper wrapper, Function<UUID, ?> packetGenerator) {
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
		LitematicaPacketHandler.INSTANCE.registerMessage(RequestContentsMessage.class, RequestContentsMessage::encode, RequestContentsMessage::decode, RequestContentsMessage::onMessage);
		LitematicaPacketHandler.INSTANCE.initServerListener();
	}

	@Override
	public void initClient() {
		LitematicaPacketHandler.INSTANCE.initClientListener();
	}
}