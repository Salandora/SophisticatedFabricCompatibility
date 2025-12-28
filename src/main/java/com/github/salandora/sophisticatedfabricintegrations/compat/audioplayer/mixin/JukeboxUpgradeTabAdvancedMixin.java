package com.github.salandora.sophisticatedfabricintegrations.compat.audioplayer.mixin;

import com.github.salandora.sophisticatedfabricintegrations.compat.audioplayer.AudioPlayerCompat;
import de.maxhenkel.audioplayer.CustomSound;
import net.minecraft.network.chat.Component;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.upgrades.jukebox.JukeboxUpgradeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JukeboxUpgradeTab.Advanced.class)
public class JukeboxUpgradeTabAdvancedMixin extends JukeboxUpgradeTab {
	public JukeboxUpgradeTabAdvancedMixin(JukeboxUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen, int slotsInRow, Component tabLabel, Component closedTooltip) {
		super(upgradeContainer, position, screen, slotsInRow, tabLabel, closedTooltip);
	}

	@Inject(
			method = "getPlaybackRemainingProgress",
			at = @At("HEAD"),
			cancellable = true
	)
	private void sophisticatedfabriccompat$getPlaybackRemainingProgress(CallbackInfoReturnable<Float> cir) {
		CustomSound sound = CustomSound.of(getContainer().getUpgradeWrapper().getDisc());
		if (sound != null) {
			long finishTime = getContainer().getDiscFinishTime();
			//noinspection DataFlowIssue
			int remaining = (int) (finishTime - minecraft.level.getGameTime());

			long audioLength = getContainer().getUpgradeWrapper().getUpgradeStack().sophisticatedCore_getOrDefault(AudioPlayerCompat.DISC_LENGTH, 1L);

			cir.setReturnValue(remaining / (float) audioLength);
		}
	}
}
