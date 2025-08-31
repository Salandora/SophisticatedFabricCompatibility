package com.github.salandora.sophisticatedfabricintegrations.client;

import com.github.salandora.sophisticatedfabricintegrations.compat.CompatRegistry;
import com.github.salandora.sophisticatedfabricintegrations.init.ModPayloads;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;

public class SophisticatedFabricIntegrationsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModPayloads.registerClientPayloads();
		CompatRegistry.initCompats();
        CompatRegistry.initClientCompats();
        ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft client) -> CompatRegistry.setupCompats());
    }
}
