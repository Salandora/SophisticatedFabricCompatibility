package org.github.salandora.sophisticatedfabriccompat.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;

// TODO: This is ugly at best, we need a better config system that can be used within a mixin plugin
public class Config {
	private static File configFile = FabricLoader.getInstance().getConfigDir().resolve("sophisticatedfabriccompat.toml").toFile();
	public static Config CONFIG;

	private CommentedConfigSpec configSpec;
	private com.electronwill.nightconfig.core.Config config;

	public Litematica LITEMATICA;

	public Config() {
		configSpec = new CommentedConfigSpec();

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

	public class Litematica {
		public Litematica(CommentedConfigSpec spec) {
			spec.comment("Enable Litematica compat").define("litematica.enableCompat", true);
			spec.comment("Enable Litematica compat for Sophisticated Backpacks").define("litematica.enableBackpacksCompat", true);
			spec.comment("Enable Litematica compat for Sophisticated Storage").define("litematica.enableStorageCompat", true);
		}

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
