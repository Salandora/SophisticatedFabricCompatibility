package org.github.salandora.sophisticatedfabricintegrations.compat.carpetmod.mixin;

import carpet.CarpetSettings;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class Item_missingToolsMixin {
	@Inject(
			method = "isCorrectToolForDrops",
			at = @At("HEAD"),
			cancellable = true
	)
	public void sophisticatedfabriccompat$isCorrectToolForDrops(ItemStack itemStack, BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
		if (CarpetSettings.missingTools && blockState.getSoundType() == SoundType.GLASS && itemStack.is(ItemTags.PICKAXES)) {
			cir.setReturnValue(true);
		}
	}
}
