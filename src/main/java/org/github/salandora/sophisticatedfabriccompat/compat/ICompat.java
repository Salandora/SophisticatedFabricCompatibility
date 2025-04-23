package org.github.salandora.sophisticatedfabriccompat.compat;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface ICompat {
	default void init() {
		//noop
	}

	@Environment(EnvType.CLIENT)
	default void initClient() {
		//noop
	}

	void setup();
}
