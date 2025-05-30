package org.github.salandora.sophisticatedfabricintegrations.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;

import javax.annotation.Nullable;

public record CompatInfo(String modId, @Nullable VersionPredicate supportedVersionRange) {
	public static CompatInfo ALWAYS = new CompatInfo(null, null);

	public boolean isLoaded() {
		if (modId == null) {
			return true;
		}

		return FabricLoader.getInstance().getModContainer(modId())
				.map(container -> supportedVersionRange() == null || supportedVersionRange().test(container.getMetadata().getVersion()))
				.orElse(false);
	}
}
