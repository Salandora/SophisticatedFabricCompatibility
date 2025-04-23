package org.github.salandora.sophisticatedfabriccompat.init;

import org.github.salandora.sophisticatedfabriccompat.compat.CompatInfo;
import org.github.salandora.sophisticatedfabriccompat.compat.CompatModIds;
import org.github.salandora.sophisticatedfabriccompat.compat.CompatRegistry;
import org.github.salandora.sophisticatedfabriccompat.compat.litematica.backpacks.LitematicaBackpacksCompat;
import org.github.salandora.sophisticatedfabriccompat.compat.litematica.core.LitematicaCompat;
import org.github.salandora.sophisticatedfabriccompat.compat.litematica.storage.LitematicaStorageCompat;
import org.github.salandora.sophisticatedfabriccompat.compat.trinkets.TrinketsCompat;

public class ModCompat {
	public static void register() {
		// Trinkets
		CompatRegistry.registerCompat(new CompatInfo(CompatModIds.TRINKETS, null), TrinketsCompat::new);

		// Litematica
		CompatRegistry.registerCompat(CompatInfo.ALWAYS, LitematicaCompat::new); // Must always be loaded for the packets to be registered
		CompatRegistry.registerCompat(new CompatInfo(CompatModIds.SOPHISTICATED_BACKPACKS, null), LitematicaBackpacksCompat::new);
		CompatRegistry.registerCompat(new CompatInfo(CompatModIds.SOPHISTICATED_STORAGE, null), LitematicaStorageCompat::new);

		// CompatRegistry.registerCompat(new CompatInfo(CompatModIds.AUDIOPLAYER, null), () -> new AudioPlayerCompat());
	}
}
