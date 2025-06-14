package com.github.salandora.sophisticatedfabricintegrations.compat.carpetmod.mixin;

import carpet.CarpetSettings;
import net.minecraft.core.Holder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class Item_missingToolsMixin {
	@Shadow @Deprecated public abstract Holder.Reference<Item> builtInRegistryHolder();

	@Inject(
			method = "isCorrectToolForDrops",
			at = @At("HEAD"),
			cancellable = true
	)
	public void sophisticatedfabriccompat$isCorrectToolForDrops(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
		if (CarpetSettings.missingTools && blockState.getSoundType() == SoundType.GLASS && this.builtInRegistryHolder().is(ItemTags.PICKAXES)) {
			cir.setReturnValue(true);
		}
	}
}
