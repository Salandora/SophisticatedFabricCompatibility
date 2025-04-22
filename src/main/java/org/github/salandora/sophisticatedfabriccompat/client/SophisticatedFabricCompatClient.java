package org.github.salandora.sophisticatedfabriccompat.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import org.github.salandora.sophisticatedfabriccompat.compat.CompatRegistry;

public class SophisticatedFabricCompatClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register((Minecraft client) -> CompatRegistry.setupCompats());
    }
}
