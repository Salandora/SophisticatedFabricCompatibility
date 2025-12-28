package com.github.salandora.sophisticatedfabricintegrations.config;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;

public class ConfigManager {
	private static Logger LOGGER = LogUtils.getLogger();
	private final Map<String, Field> options = new LinkedHashMap<>();
	private final String version;
	private Path configFile;

	public ConfigManager(String version) {
		this.version = version;
	}

	public void loadConfig(Class<?> configClass, Path configFile) {
		this.configFile = configFile;
		loadAnnotations("", configClass);

		Map<String, String> configEntries = this.readConfigFile();
		String version = configEntries.remove("version");
		boolean correctlyLoaded = true;
		for (Map.Entry<String, String> entry : configEntries.entrySet()) {
			Field field = this.options.get(entry.getKey());
			Class<?> type = field.getType();
			try {
				if (type.equals(Boolean.class) || type.equals(boolean.class)) {
					boolean value = Boolean.parseBoolean(entry.getValue());
					field.setBoolean(null, value);
				} else {
					correctlyLoaded = false;
				}
			} catch (IllegalAccessException e) {
				LOGGER.error("Could not access field for option: " + field.getName(), e);
				correctlyLoaded = false;
			}
		}

		// If the config was loaded correctly but has the wrong version we save it to have the updated keys
		if (correctlyLoaded && !this.version.equals(version)) {
			writeConfigFile();
		}
	}

	private void loadAnnotations(String path,Class<?> clazz) {
		String basePath = path + (!path.isEmpty() ? "." : "");
		for (Field field : clazz.getDeclaredFields()) {
			Option annotation = field.getAnnotation(Option.class);
			if (annotation == null)  {
				continue;
			}

			this.options.put(basePath + field.getName(), field);
		}

		for (Class<?> category : clazz.getDeclaredClasses()) {
			String categoryPath = basePath + category.getSimpleName().toLowerCase();
			loadAnnotations(categoryPath, category);
		}
	}

	private Map<String, String> readConfigFile() {
		try (BufferedReader reader = Files.newBufferedReader(this.configFile)) {
			String line;
			Map<String, String> result = new HashMap<>();

			while ((line = reader.readLine()) != null) {
				// replace all carriage return and carriage new lines
				line = line.replaceAll("[\\r\\n]", "");
				if (line.startsWith("#")) {
					continue;
				}

				String[] fields = line.split("=");
				if (fields.length > 1) {
					String key = fields[0].trim();
					if (this.options.containsKey(key) || key.equals("version")) {
						String value = fields[1];
						if (value.contains("#")) {
							value = value.substring(0, value.indexOf("#"));
						}
						result.put(key, value.trim());
					} else {
						LOGGER.error("Option " + fields[0] + " is not a valid option!");
					}
				}
			}

			return result;
		} catch (NoSuchFileException e) {
			writeConfigFile();
			return readConfigFile();
		} catch (IOException e) {
			LOGGER.error("Exception while loading config file", e);
			return new HashMap<>();
		}
	}

	private void writeConfigFile() {
		try (BufferedWriter writer = Files.newBufferedWriter(this.configFile)) {
			writer.write("# Version of this config");
			writer.newLine();
			writer.write("version = " + this.version);
			writer.newLine();

			List<String> keys = new ArrayList<>(this.options.keySet());
			Collections.reverse(keys);
			for (String key : keys) {
				Field field = this.options.get(key);

				Option annotation = field.getAnnotation(Option.class);
				for (String comment : annotation.comment()) {
					writer.write("# " + comment);
					writer.newLine();
				}
				writer.write(key + " = " + field.get(null).toString());
				writer.newLine();
			}
		} catch (IOException | IllegalAccessException e) {
			LOGGER.error("Exception while saving config file", e);
		}
	}
}
