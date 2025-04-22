package org.github.salandora.sophisticatedfabriccompat.compat;

public interface ICompat {
	default void init() {
		//noop
	}

	void setup();
}
