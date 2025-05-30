package org.github.salandora.sophisticatedfabricintegrations.compat.audioplayer;

import net.fabricmc.loader.api.FabricLoader;
import org.github.salandora.sophisticatedfabricintegrations.compat.CompatModIds;
import org.github.salandora.sophisticatedfabricintegrations.config.Config;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class AudioPlayerMixinPlugin implements IMixinConfigPlugin {
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return Config.CONFIG.AUDIOPLAYER.enableCompat() && FabricLoader.getInstance().isModLoaded(CompatModIds.AUDIOPLAYER);
	}

	@Override
	public void onLoad(String mixinPackage) {
		if (Config.CONFIG != null) {
			return;
		}

		try {
			Config.CONFIG = Config.load();
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to load config for SophisticatedFabricCompat", e);
		}
	}

	@Override
	@Nullable
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	@Nullable
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}
