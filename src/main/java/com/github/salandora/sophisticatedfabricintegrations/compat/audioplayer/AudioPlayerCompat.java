package com.github.salandora.sophisticatedfabricintegrations.compat.audioplayer;

import com.github.salandora.sophisticatedfabricintegrations.compat.ICompat;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class AudioPlayerCompat implements ICompat {
	@Override
	public void setup() {
		ServerTickEvents.END_WORLD_TICK.register(AudioPlayerSoundHandler::tick);
	}
}
