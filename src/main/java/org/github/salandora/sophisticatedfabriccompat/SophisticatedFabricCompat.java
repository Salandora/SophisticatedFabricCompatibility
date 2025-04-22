package org.github.salandora.sophisticatedfabriccompat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.github.salandora.sophisticatedfabriccompat.compat.CompatRegistry;
import org.github.salandora.sophisticatedfabriccompat.init.ModCompat;
import org.github.salandora.sophisticatedfabriccompat.init.ModPayloads;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SophisticatedFabricCompat implements ModInitializer {
    public static final String MOD_ID = "sophisticatedfabriccompat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModPayloads.registerPayloads();
        ModCompat.register();

        CompatRegistry.initCompats();
        ServerLifecycleEvents.SERVER_STARTING.register((MinecraftServer server) -> CompatRegistry.setupCompats());
    }

    public static ResourceLocation getRL(String regName) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, regName);
    }
}
