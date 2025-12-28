package com.github.salandora.sophisticatedfabricintegrations;

import com.github.salandora.sophisticatedfabricintegrations.config.ConfigManager;
import com.github.salandora.sophisticatedfabricintegrations.config.Option;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class Config {
	private static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("sophisticatedfabricintegrations.conf");

	private static ConfigManager manager;

	public static void load() {
		AtomicReference<String> version = new AtomicReference<>("");
		FabricLoader.getInstance().getModContainer(SophisticatedFabricIntegrations.MOD_ID).ifPresent(c -> version.set(c.getMetadata().getVersion().toString()));
		manager = new ConfigManager(version.get());
		manager.loadConfig(Config.class, configFile);
	}

	public static boolean isLoaded() {
		return manager != null;
	}

	public class AudioPlayer {
		@Option(comment = "Enable AudioPlayer compat")
		public static boolean enableCompat = true;
	}

	public class CarpetMod {
		@Option(comment = "Enable AudioPlayer compat")
		public static boolean enableCompat = true;

		@Option(comment = "Enable support for carpet's missingTools rule")
		public static boolean enableMissingTools = true;
	}

	public class Litematica {
		@Option(comment = "Enable AudioPlayer compat")
		public static boolean enableCompat = true;

		@Option(comment = "Enable Litematica compat for Sophisticated Backpacks")
		public static boolean enableBackpacksCompat = true;

		@Option(comment = "Enable Litematica compat for Sophisticated Storage")
		public static boolean enableStorageCompat = true;
	}
}