package org.github.salandora.sophisticatedfabriccompat.compat.audioplayer.mixin;

import de.maxhenkel.audioplayer.AudioManager;
import de.maxhenkel.audioplayer.CustomSound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.init.ModCoreDataComponents;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeWrapperBase;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeWrapper;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.ServerStorageSoundHandler;
import org.github.salandora.sophisticatedfabriccompat.compat.audioplayer.AudioPlayerCompat;
import org.github.salandora.sophisticatedfabriccompat.compat.audioplayer.AudioPlayerSoundHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.UUID;
import java.util.function.Consumer;

@Mixin(JukeboxUpgradeWrapper.class)
public abstract class JukeboxUpgradeWrapperMixin extends UpgradeWrapperBase<JukeboxUpgradeWrapper, JukeboxUpgradeItem> implements ITickableUpgrade {
	@Shadow public abstract ItemStack getDisc();
	@Shadow @Nullable private Entity entityPlaying;
	@Shadow @Nullable private BlockPos posPlaying;
	@Shadow @Final private Runnable onFinishedCallback;

	protected JukeboxUpgradeWrapperMixin(IStorageWrapper storageWrapper, ItemStack upgrade, Consumer<ItemStack> upgradeSaveHandler) {
		super(storageWrapper, upgrade, upgradeSaveHandler);
	}

	@Inject(
			method = "lambda$playDisc$0",
			at = @At(value = "HEAD"),
			cancellable = true
	)
	private void sophisticatedfabriccompat$playDisc(ServerLevel serverLevel, UUID storageUuid, Level level, Holder<JukeboxSong> song, CallbackInfo ci) {
		CustomSound sound = CustomSound.of(getDisc());
		if (sound != null) {
			AudioPlayerSoundHandler.storageUUIDToDisc.put(storageUuid, new WeakReference<>(getDisc()));
			if (entityPlaying != null) {
				ServerStorageSoundHandler.startPlayingDisc(serverLevel, entityPlaying.position(), storageUuid, entityPlaying.getId(), song, onFinishedCallback);
			} else {
				//noinspection DataFlowIssue
				ServerStorageSoundHandler.startPlayingDisc(serverLevel, posPlaying, storageUuid, song, onFinishedCallback);
			}

			long audioLength;
			try {
				audioLength = (long)(20 * AudioManager.getLengthSeconds(AudioManager.getSound(serverLevel.getServer(), sound.getSoundId())));
			} catch (Exception e) {
				audioLength = song.value().lengthInTicks();
			}
			upgrade.sophisticatedCore_set(AudioPlayerCompat.DISC_LENGTH, audioLength);
			upgrade.sophisticatedCore_set(ModCoreDataComponents.DISC_FINISH_TIME, level.getGameTime() + audioLength);
			ci.cancel();
		} else {
			AudioPlayerSoundHandler.storageUUIDToDisc.remove(storageUuid);
		}
	}
}
