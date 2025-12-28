package com.github.salandora.sophisticatedfabricintegrations.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

// TODO: This is ugly at best, we need a better config system that can be used within a mixin plugin
public class Config {
	private static final File configFile = FabricLoader.getInstance().getConfigDir().resolve("sophisticatedfabriccompat.toml").toFile();
	public static Config CONFIG;

	private final CommentedConfigSpec configSpec;
	private com.electronwill.nightconfig.core.Config config;

	public AudioPlayer AUDIOPLAYER;
	public CarpetMod CARPETMOD;
	public Litematica LITEMATICA;

	public Config() {
		configSpec = new CommentedConfigSpec();

		AUDIOPLAYER = new AudioPlayer(configSpec);
		CARPETMOD = new CarpetMod(configSpec);
		LITEMATICA = new Litematica(configSpec);
	}

	public static Config load() {
		Config config = new Config();

		CommentedFileConfig fileConfig = CommentedFileConfig.builder(configFile).autosave().sync().build();
		fileConfig.load();

		if (!config.configSpec.isCorrect(fileConfig)) {
			config.configSpec.correct(fileConfig);
			config.configSpec.putAllComments(fileConfig);
		}

		config.config = fileConfig;

		return config;
	}

	public class AudioPlayer {
		public AudioPlayer(CommentedConfigSpec spec) {
			spec.comment("Enable AudioPlayer compat").define("audioplayer.enableCompat", true);
		}

		public boolean enableCompat() {
			return config.get("audioplayer.enableCompat");
		}
	}

	public class CarpetMod {
		public CarpetMod(CommentedConfigSpec spec) {
			spec.comment("Enable Carpetmod compat").define("carpetmod.enableCompat", true);
			spec.comment("Enable support for carpet's missingTools rule").define("carpetmod.enableCompat", true);
		}

		public boolean enableCompat() {
			return config.get("carpetmod.enableCompat");
		}

		public boolean enableMissingTools() {
			return config.get("carpetmod.enableMissingToolsCompat");
		}
	}

	public class Litematica {
		public Litematica(CommentedConfigSpec spec) {
			spec.comment("Enable Litematica compat").define("litematica.enableCompat", true);
			spec.comment("Enable Litematica compat for Sophisticated Backpacks").define("litematica.enableBackpacksCompat", true);
			spec.comment("Enable Litematica compat for Sophisticated Storage").define("litematica.enableStorageCompat", true);
		}

		@SuppressWarnings("BooleanMethodIsAlwaysInverted")
		public boolean enableCompat() {
			return config.get("litematica.enableCompat");
		}
		public boolean enableBackpacksCompat() {
			return config.get("litematica.enableBackpacksCompat");
		}
		public boolean enableStorageCompat() {
			return config.get("litematica.enableStorageCompat");
		}
	}
}
