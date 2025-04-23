package org.github.salandora.sophisticatedfabriccompat.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import org.github.salandora.sophisticatedfabriccompat.compat.CompatRegistry;
import org.github.salandora.sophisticatedfabriccompat.init.ModPayloads;

public class SophisticatedFabricCompatClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModPayloads.registerClientPayloads();
        CompatRegistry.initClientCompats();
        ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft client) -> CompatRegistry.setupCompats());
    }
}
