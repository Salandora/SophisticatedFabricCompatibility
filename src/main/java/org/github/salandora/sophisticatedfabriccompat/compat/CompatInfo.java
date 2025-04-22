package org.github.salandora.sophisticatedfabriccompat.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;

import javax.annotation.Nullable;

public record CompatInfo(String modId, @Nullable VersionPredicate supportedVersionRange) {
	public boolean isLoaded() {
		return FabricLoader.getInstance().getModContainer(modId())
				.map(container -> supportedVersionRange() == null || supportedVersionRange().test(container.getMetadata().getVersion()))
				.orElse(false);
	}
}
