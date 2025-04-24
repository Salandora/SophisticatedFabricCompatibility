package org.github.salandora.sophisticatedfabriccompat.compat.audioplayer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import org.github.salandora.sophisticatedfabriccompat.compat.ICompat;

import java.util.function.Supplier;

import static org.github.salandora.sophisticatedfabriccompat.init.ModDataComponents.DATA_COMPONENT_TYPES;

public class AudioPlayerCompat implements ICompat {
	public static final Supplier<DataComponentType<Long>> DISC_LENGTH = DATA_COMPONENT_TYPES.register("disc_length",
			() -> new DataComponentType.Builder<Long>().networkSynchronized(ByteBufCodecs.VAR_LONG).build());

	@Override
	public void setup() {
		ServerTickEvents.END_WORLD_TICK.register(AudioPlayerSoundHandler::tick);
	}
}
