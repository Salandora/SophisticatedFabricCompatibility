package com.github.salandora.sophisticatedfabricintegrations.init;

import com.github.salandora.sophisticatedfabricintegrations.compat.CompatInfo;
import com.github.salandora.sophisticatedfabricintegrations.compat.CompatModIds;
import com.github.salandora.sophisticatedfabricintegrations.compat.CompatRegistry;
import com.github.salandora.sophisticatedfabricintegrations.compat.audioplayer.AudioPlayerCompat;
import com.github.salandora.sophisticatedfabricintegrations.compat.litematica.backpacks.LitematicaBackpacksCompat;
import com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.LitematicaCompat;
import com.github.salandora.sophisticatedfabricintegrations.compat.litematica.storage.LitematicaStorageCompat;
import com.github.salandora.sophisticatedfabricintegrations.compat.trinkets.TrinketsCompat;

public class ModCompat {
	public static void register() {
		// Trinkets
		CompatRegistry.registerCompat(new CompatInfo(CompatModIds.TRINKETS, null), TrinketsCompat::new);

		// Litematica
		CompatRegistry.registerCompat(CompatInfo.ALWAYS, LitematicaCompat::new); // Must always be loaded for the packets to be registered
		CompatRegistry.registerCompat(new CompatInfo(CompatModIds.SOPHISTICATED_BACKPACKS, null), LitematicaBackpacksCompat::new);
		CompatRegistry.registerCompat(new CompatInfo(CompatModIds.SOPHISTICATED_STORAGE, null), LitematicaStorageCompat::new);

		// AudioPlayer
		CompatRegistry.registerCompat(new CompatInfo(CompatModIds.AUDIOPLAYER, null), AudioPlayerCompat::new);
	}
}
