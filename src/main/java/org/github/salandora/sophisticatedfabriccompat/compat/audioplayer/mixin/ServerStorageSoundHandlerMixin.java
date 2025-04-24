package org.github.salandora.sophisticatedfabriccompat.compat.audioplayer.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.ServerStorageSoundHandler;
import org.github.salandora.sophisticatedfabriccompat.compat.audioplayer.AudioPlayerSoundHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerStorageSoundHandler.class)
public abstract class ServerStorageSoundHandlerMixin {
	@Shadow
	private static void putKeepAliveInfo(ServerLevel serverLevel, UUID storageUuid, Runnable onFinishedHandler, Vec3 pos) {}

	@Inject(
			method = "updateKeepAlive",
			at = @At(
					value = "INVOKE",
					target = "Lnet/p3pp3rf1y/sophisticatedcore/upgrades/jukebox/ServerStorageSoundHandler$KeepAliveInfo;update(JLnet/minecraft/world/phys/Vec3;)V"
			)
	)
	private static void sophisticatedfabriccompat$updateKeepAlive(UUID storageUuid, Level level, Vec3 position, Runnable onNoLongerRunning, CallbackInfo ci) {
		if (AudioPlayerSoundHandler.storageUUIDToDisc.containsKey(storageUuid)) {
			AudioPlayerSoundHandler.update(storageUuid, position);
		}
	}

	@Inject(
			method = "startPlayingDisc(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Ljava/util/UUID;Lnet/minecraft/core/Holder;Ljava/lang/Runnable;)V",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private static void sophisticatedfabriccompat$startPlayingDisc(ServerLevel serverLevel, BlockPos position, UUID storageUuid, Holder<JukeboxSong> song, Runnable onFinishedHandler, CallbackInfo ci) {
		if (AudioPlayerSoundHandler.storageUUIDToDisc.containsKey(storageUuid)) {
			ItemStack disc = AudioPlayerSoundHandler.storageUUIDToDisc.get(storageUuid).get();
			if (disc == null) {
				// Item was cleared so let's remove the entry and move on
				AudioPlayerSoundHandler.storageUUIDToDisc.remove(storageUuid);
				return;
			}

			if (AudioPlayerSoundHandler.play(serverLevel, position, storageUuid, disc)) {
				putKeepAliveInfo(
						serverLevel,
						storageUuid,
						() -> {
							AudioPlayerSoundHandler.stop(storageUuid);
							onFinishedHandler.run();
						},
						Vec3.atCenterOf(position)
				);
				ci.cancel();
			}
		}
	}

	@Inject(
			method = "startPlayingDisc(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;Ljava/util/UUID;ILnet/minecraft/core/Holder;Ljava/lang/Runnable;)V",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private static void sophisticatedfabriccompat$startPlayingDisc(ServerLevel serverLevel, Vec3 position, UUID storageUuid, int entityId, Holder<JukeboxSong> song, Runnable onStopHandler, CallbackInfo ci) {
		if (AudioPlayerSoundHandler.storageUUIDToDisc.containsKey(storageUuid)) {
			ItemStack disc = AudioPlayerSoundHandler.storageUUIDToDisc.get(storageUuid).get();
			if (disc == null) {
				// Item was cleared so let's remove the entry and move on
				AudioPlayerSoundHandler.storageUUIDToDisc.remove(storageUuid);
				return;
			}

			if (AudioPlayerSoundHandler.play(serverLevel, position, storageUuid, disc)) {
				putKeepAliveInfo(
						serverLevel,
						storageUuid,
						() -> {
							AudioPlayerSoundHandler.stop(storageUuid);
							onStopHandler.run();
						},
						position
				);
				ci.cancel();
			}
		}
	}

	@Inject(
			method = "sendStopMessage",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void sophisticatedfabriccompat$sendStopMessage(Level level, Vec3 position, UUID storageUuid, CallbackInfo ci) {
		if (AudioPlayerSoundHandler.storageUUIDToDisc.containsKey(storageUuid)) {
			AudioPlayerSoundHandler.stop(storageUuid);
			ci.cancel();
		}
	}
}
