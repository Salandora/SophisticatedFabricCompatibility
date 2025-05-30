package org.github.salandora.sophisticatedfabricintegrations.compat;

import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.github.salandora.sophisticatedfabricintegrations.SophisticatedFabricIntegrations;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class CompatRegistry {
	private static final Map<CompatInfo, List<Supplier<ICompat>>> compatFactories = new ConcurrentHashMap<>();
	private static final Map<CompatInfo, List<ICompat>> loadedCompats = new ConcurrentHashMap<>();

	protected CompatRegistry() {
	}

	public static void registerCompat(CompatInfo info, Supplier<ICompat> factory) {
		compatFactories.computeIfAbsent(info, k -> new ArrayList<>()).add(factory);
	}

	public static void setupCompats() {
		loadedCompats.values().forEach(compats -> compats.forEach(ICompat::setup));
	}

	public static void initCompats() {
		compatFactories.forEach((compatInfo, factories) -> {
			if (compatInfo.isLoaded()) {
				factories.forEach(factory -> {
					try {
						loadedCompats.computeIfAbsent(compatInfo, k -> new ArrayList<>()).add(factory.get());
					} catch (Exception e) {
						SophisticatedFabricIntegrations.LOGGER.error("Error instantiating compatibility ", e);
					}
				});
			}
		});
		loadedCompats.values().forEach(compats -> compats.forEach(ICompat::init));
	}

	public static void initClientCompats() {
		loadedCompats.values().forEach(compats -> compats.forEach(ICompat::initClient));
	}

	@Nullable
	public static VersionPredicate fromSpec(String spec) {
		try {
			return VersionPredicate.parse(spec);
		}
		catch (VersionParsingException e) {
			return null;
		}
	}
}
