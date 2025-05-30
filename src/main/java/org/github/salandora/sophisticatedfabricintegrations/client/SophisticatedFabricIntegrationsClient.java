package org.github.salandora.sophisticatedfabricintegrations.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import org.github.salandora.sophisticatedfabricintegrations.compat.CompatRegistry;
import org.github.salandora.sophisticatedfabricintegrations.init.ModPayloads;

public class SophisticatedFabricIntegrationsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModPayloads.registerClientPayloads();
        CompatRegistry.initClientCompats();
        ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft client) -> CompatRegistry.setupCompats());
    }
}
