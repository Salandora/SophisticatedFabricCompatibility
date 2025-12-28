package com.github.salandora.sophisticatedfabricintegrations.compat.audioplayer;

import com.github.salandora.sophisticatedfabricintegrations.compat.ICompat;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.function.Supplier;

import static com.github.salandora.sophisticatedfabricintegrations.init.ModDataComponents.DATA_COMPONENT_TYPES;

public class AudioPlayerCompat implements ICompat {
	public static final Supplier<DataComponentType<Long>> DISC_LENGTH = DATA_COMPONENT_TYPES.register("disc_length",
			() -> new DataComponentType.Builder<Long>().networkSynchronized(ByteBufCodecs.VAR_LONG).build());

	@Override
	public void setup() {
		ServerTickEvents.END_WORLD_TICK.register(AudioPlayerSoundHandler::tick);
	}
}
