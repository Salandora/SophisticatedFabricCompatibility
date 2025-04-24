package org.github.salandora.sophisticatedfabriccompat.compat.audioplayer;

import de.maxhenkel.audioplayer.AudioPlayer;
import de.maxhenkel.audioplayer.CustomSound;
import de.maxhenkel.audioplayer.PlayerType;
import de.maxhenkel.audioplayer.Plugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import de.maxhenkel.voicechat.api.audiochannel.LocationalAudioChannel;
import de.maxhenkel.voicechat.plugins.impl.PositionImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.ServerStorageSoundHandler;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AudioPlayerSoundHandler {
	private static final int SOUND_STOP_CHECK_INTERVAL = 10;

	private static long lastPlaybackChecked = 0;
	private static final Map<UUID, UUID> storageUUIDToChannelUUID = new HashMap<>();

	public static final Map<UUID, WeakReference<ItemStack>> storageUUIDToDisc = new HashMap<>();

	@SuppressWarnings("SameParameterValue")
	@Nullable
	private static UUID play(ServerLevel serverLevel, Vec3 pos, PlayerType type, CustomSound sound) {
		VoicechatServerApi api = Plugin.voicechatServerApi;
		if (api == null) {
			return null;
		}

		if (sound.isStaticSound() && AudioPlayer.SERVER_CONFIG.allowStaticAudio.get()) {
			return SCPlayerManager.instance().playStatic(
					api,
					serverLevel,
					pos,
					sound.getSoundId(),
					sound.getRange(type),
					type.getCategory(),
					type.getMaxDuration().get()
			);
		} else {
			return SCPlayerManager.instance().playLocational(
					api,
					serverLevel,
					pos,
					sound.getSoundId(),
					sound.getRange(type),
					type.getCategory(),
					type.getMaxDuration().get()
			);
		}
	}

	public static boolean play(ServerLevel level, BlockPos position, UUID storageUuid, ItemStack discItemStack) {
		CustomSound sound = CustomSound.of(discItemStack);
		if (sound != null) {
			stop(storageUuid);

			UUID channel = play(level, Vec3.atCenterOf(position), PlayerType.MUSIC_DISC, sound);
			if (channel != null) {
				storageUUIDToChannelUUID.put(storageUuid, channel);
				return true;
			}
		}
		return false;
	}

	public static boolean play(ServerLevel level, Vec3 position, UUID storageUuid, ItemStack discItemStack) {
		CustomSound sound = CustomSound.of(discItemStack);
		if (sound != null) {
			stop(storageUuid);

			UUID channel = play(level, position, PlayerType.MUSIC_DISC, sound);
			if (channel != null) {
				storageUUIDToChannelUUID.put(storageUuid, channel);
				return true;
			}
		}
		return false;
	}

	public static void stop(UUID storageUuid) {
		if (!storageUUIDToChannelUUID.containsKey(storageUuid)) {
			return;
		}

		storageUUIDToDisc.remove(storageUuid);
		UUID channelID = storageUUIDToChannelUUID.remove(storageUuid);
		SCPlayerManager.instance().stop(channelID);
	}

	public static void update(UUID storageUuid, Vec3 position) {
		if (!storageUUIDToChannelUUID.containsKey(storageUuid)) {
			return;
		}

		UUID channelID = storageUUIDToChannelUUID.get(storageUuid);
		AudioChannel channel = SCPlayerManager.instance().getAudioChannel(channelID);
		if (channel instanceof LocationalAudioChannel locationalAudioChannel) {
			locationalAudioChannel.updateLocation(new PositionImpl(position));
		}
	}

	public static void tick(ServerLevel level) {
		if (!storageUUIDToChannelUUID.isEmpty() && lastPlaybackChecked < level.getGameTime() - SOUND_STOP_CHECK_INTERVAL) {
			lastPlaybackChecked = level.getGameTime();
			storageUUIDToChannelUUID.entrySet().removeIf(entry -> {
				if (!SCPlayerManager.instance().isPlaying(entry.getValue())) {
					storageUUIDToDisc.remove(entry.getKey());
					ServerStorageSoundHandler.onSoundFinished(level, entry.getKey());
					return true;
				}
				return false;
			});
		}
	}
}
