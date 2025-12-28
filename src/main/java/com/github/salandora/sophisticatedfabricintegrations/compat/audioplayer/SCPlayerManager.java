package com.github.salandora.sophisticatedfabricintegrations.compat.audioplayer;

import com.github.salandora.sophisticatedfabricintegrations.SophisticatedFabricIntegrations;
import de.maxhenkel.audioplayer.AudioManager;
import de.maxhenkel.audioplayer.StaticAudioPlayer;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.audiochannel.AudioChannel;
import de.maxhenkel.voicechat.api.audiochannel.AudioPlayer;
import de.maxhenkel.voicechat.api.audiochannel.LocationalAudioChannel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SCPlayerManager {
	private final Map<UUID, PlayerReference> players;
	private final ExecutorService executor;

	public SCPlayerManager() {
		this.players = new ConcurrentHashMap<>();
		this.executor = Executors.newSingleThreadExecutor(r -> {
			Thread thread = new Thread(r, "SC-AudioPlayerThread");
			thread.setDaemon(true);
			return thread;
		});
	}

	@Nullable
	public UUID playLocational(VoicechatServerApi api, ServerLevel level, Vec3 pos, UUID sound, float distance, @Nullable String category, int maxLengthSeconds) {
		UUID channelID = UUID.randomUUID();

		LocationalAudioChannel channel = api.createLocationalAudioChannel(channelID, api.fromServerLevel(level), api.createPosition(pos.x, pos.y, pos.z));
		if (channel == null) {
			return null;
		}
		channel.setCategory(category);
		channel.setDistance(distance);

		api.getPlayersInRange(api.fromServerLevel(level), channel.getLocation(), distance + 1F, serverPlayer -> {
			VoicechatConnection connection = api.getConnectionOf(serverPlayer);
			return connection == null || connection.isDisabled();
		}).stream()
				.map(p -> (ServerPlayer) p.getPlayer())
				.forEach(player -> player.displayClientMessage(Component.literal("You need to enable voice chat to hear custom audio"), true));

		AtomicBoolean stopped = new AtomicBoolean();
		AtomicReference<AudioPlayer> player = new AtomicReference<>();

		players.put(channelID, new PlayerReference(() -> {
			synchronized (stopped) {
				stopped.set(true);
				AudioPlayer audioPlayer = player.get();
				if (audioPlayer != null) {
					audioPlayer.stopPlaying();
				}
			}
		}, player, sound, channel));

		executor.execute(() -> {
			AudioPlayer audioPlayer = playChannel(api, channel, level, sound, maxLengthSeconds);
			if (audioPlayer == null) {
				players.remove(channelID);
				return;
			}
			audioPlayer.setOnStopped(() -> players.remove(channelID));
			synchronized (stopped) {
				if (!stopped.get()) {
					player.set(audioPlayer);
				} else {
					audioPlayer.stopPlaying();
				}
			}
		});
		return channelID;
	}

	@Nullable
	public UUID playStatic(VoicechatServerApi api, ServerLevel level, Vec3 pos, UUID sound, float distance, @Nullable String category, int maxLengthSeconds) {
		UUID channelID = UUID.randomUUID();

		api.getPlayersInRange(api.fromServerLevel(level), api.createPosition(pos.x, pos.y, pos.z), distance + 1F, serverPlayer -> {
			VoicechatConnection connection = api.getConnectionOf(serverPlayer);
			return connection == null || connection.isDisabled();
		}).stream()
				.map(p -> (ServerPlayer) p.getPlayer())
				.forEach(player -> player.displayClientMessage(Component.literal("You need to enable voice chat to hear custom audio"), true));

		StaticAudioPlayer staticAudioPlayer = StaticAudioPlayer.create(api, level, sound, null, maxLengthSeconds, category, pos, channelID, distance);

		AtomicBoolean stopped = new AtomicBoolean();
		AtomicReference<AudioPlayer> player = new AtomicReference<>();

		players.put(channelID, new PlayerReference(() -> {
			synchronized (stopped) {
				stopped.set(true);
				AudioPlayer audioPlayer = player.get();
				if (audioPlayer != null) {
					audioPlayer.stopPlaying();
				}
			}
		}, player, sound, null));

		executor.execute(() -> {
			if (staticAudioPlayer == null) {
				players.remove(channelID);
				return;
			}
			staticAudioPlayer.setOnStopped(() -> players.remove(channelID));
			synchronized (stopped) {
				if (!stopped.get()) {
					player.set(staticAudioPlayer);
				} else {
					staticAudioPlayer.stopPlaying();
				}
			}
		});
		return channelID;
	}

	@Nullable
	private AudioPlayer playChannel(VoicechatServerApi api, AudioChannel channel, ServerLevel level, UUID sound, int maxLengthSeconds) {
		try {
			short[] audio = AudioManager.getSound(level.getServer(), sound);

			if (AudioManager.getLengthSeconds(audio) > maxLengthSeconds) {
				SophisticatedFabricIntegrations.LOGGER.error("Audio {} was too long to play", sound);
				return null;
			}

			AudioPlayer player = api.createAudioPlayer(channel, api.createEncoder(), audio);
			player.startPlaying();
			return player;
		} catch (Exception e) {
			SophisticatedFabricIntegrations.LOGGER.error("Failed to play audio", e);
			return null;
		}
	}

	public void stop(UUID channelID) {
		PlayerReference player = players.get(channelID);
		if (player != null) {
			player.onStop.stop();
		}
		players.remove(channelID);
	}

	public boolean isPlaying(UUID channelID) {
		PlayerReference player = players.get(channelID);
		if (player == null) {
			return false;
		}
		AudioPlayer p = player.player.get();
		if (p == null) {
			return true;
		}
		return p.isPlaying();
	}

	@Nullable
	public AudioChannel getAudioChannel(UUID channelID) {
		PlayerReference player = players.get(channelID);
		if (player == null) {
			return null;
		}
		return player.channel();
	}

	private static SCPlayerManager instance;

	public static SCPlayerManager instance() {
		if (instance == null) {
			instance = new SCPlayerManager();
		}
		return instance;
	}

	private interface Stoppable {
		void stop();
	}

	private record PlayerReference(Stoppable onStop, AtomicReference<AudioPlayer> player, UUID sound, @Nullable AudioChannel channel) {}
}
