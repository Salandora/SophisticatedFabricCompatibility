package com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.network;

import com.github.salandora.sophisticatedfabricintegrations.SophisticatedFabricIntegrations;
import com.github.salandora.sophisticatedfabricintegrations.network.PacketHandler;

public class LitematicaPacketHandler extends PacketHandler {
	public static final LitematicaPacketHandler INSTANCE = new LitematicaPacketHandler(SophisticatedFabricIntegrations.MOD_ID, "main");

	protected LitematicaPacketHandler(String modId, String channelName) {
		super(modId, channelName);
	}
}
