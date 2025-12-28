package com.github.salandora.sophisticatedfabricintegrations.client;

import com.github.salandora.sophisticatedfabricintegrations.compat.CompatRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;

public class SophisticatedFabricIntegrationsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CompatRegistry.initClientCompats();
        ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft client) -> CompatRegistry.setupCompats());
    }
}
