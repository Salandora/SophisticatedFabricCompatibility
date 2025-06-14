package com.github.salandora.sophisticatedfabricintegrations;

import com.github.salandora.sophisticatedfabricintegrations.compat.CompatRegistry;
import com.github.salandora.sophisticatedfabricintegrations.init.ModCompat;
import com.github.salandora.sophisticatedfabricintegrations.init.ModPayloads;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SophisticatedFabricIntegrations implements ModInitializer {
    public static final String MOD_ID = "sophisticatedfabricintegrations";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
		if (!Config.isLoaded()) {
			Config.load();
		}
        ModPayloads.registerPayloads();
        ModCompat.register();

        CompatRegistry.initCompats();

        ServerLifecycleEvents.SERVER_STARTING.register((MinecraftServer server) -> CompatRegistry.setupCompats());
    }

    public static ResourceLocation getRL(String regName) {
        return new ResourceLocation(MOD_ID, regName);
    }
}
