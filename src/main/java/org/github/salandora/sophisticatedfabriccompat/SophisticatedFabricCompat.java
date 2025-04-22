package org.github.salandora.sophisticatedfabriccompat;

import net.fabricmc.api.ModInitializer;
import org.github.salandora.sophisticatedfabriccompat.compat.CompatRegistry;
import org.github.salandora.sophisticatedfabriccompat.init.ModCompat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SophisticatedFabricCompat implements ModInitializer {
    public static final String MOD_ID = "sophisticatedfabriccompat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModCompat.register();

        CompatRegistry.initCompats();
        CompatRegistry.setupCompats();
    }
}
