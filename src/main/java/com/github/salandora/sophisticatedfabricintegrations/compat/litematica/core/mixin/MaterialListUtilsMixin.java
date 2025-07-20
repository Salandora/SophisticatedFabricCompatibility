package com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.mixin;

import com.github.salandora.sophisticatedfabricintegrations.compat.CompatModIds;
import com.github.salandora.sophisticatedfabricintegrations.util.InventoryProvider;
import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.litematica.materials.MaterialListUtils;
import fi.dy.masa.malilib.util.ItemType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.LitematicaCompat.getWrapper;

@Mixin(MaterialListUtils.class)
public class MaterialListUtilsMixin {
	@ModifyVariable(method = "getInventoryItemCounts", at = @At("HEAD"), argsOnly = true)
	private static Container sophisticatedfabriccompat$wrapContainer(Container inv) {
		if (!(inv instanceof Inventory playerInv))
			return inv;

		Container trinketInventory = sophisticatedfabriccompat$getTrinketInventories(playerInv.player);
		return new CompoundContainer(inv, trinketInventory);
	}

	@Unique
	private static Container sophisticatedfabriccompat$getTrinketInventories(Player player) {
		List<ItemStack> stacks = Lists.newArrayList();
		InventoryProvider.INSTANCE.runOnInventory(CompatModIds.TRINKETS, player, stacks::add);
		return new SimpleContainer(stacks.toArray(ItemStack[]::new));
	}

	@Inject(method="getInventoryItemCounts", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;", ordinal = 0))
	private static void sophisticatedfabriccompat$injectAdditionalChecks(Container inv, CallbackInfoReturnable<Object2IntOpenHashMap<ItemType>> cir,
			@Local Object2IntOpenHashMap<ItemType> map, @Local ItemStack stack) {
		getWrapper(stack).ifPresent(litematicaWrapper -> sophisticatedfabriccompat$processItemStack(map, litematicaWrapper.wrapper()));
	}

	@Inject(method="getStoredItemCounts", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/Object2IntOpenHashMap;addTo(Ljava/lang/Object;I)I"))
	private static void sophisticatedfabriccompat$injectAdditionalChecks(ItemStack stackShulkerBox, CallbackInfoReturnable<Object2IntOpenHashMap<ItemType>> cir,
			@Local Object2IntOpenHashMap<ItemType> map, @Local(ordinal = 1) ItemStack boxStack) {
		getWrapper(boxStack).ifPresent(litematicaWrapper -> sophisticatedfabriccompat$processItemStack(map, litematicaWrapper.wrapper()));
	}

	@Unique
	private static void sophisticatedfabriccompat$processItemStack(Object2IntOpenHashMap<ItemType> map, IStorageWrapper wrapper) {
		// Don't trust the contents nbt as it might have been updated through our custom packet
		wrapper.onContentsNbtUpdated();

		InventoryHandler invHandler = wrapper.getInventoryHandler();
		int slots = invHandler.getSlotCount();
		for (int slot = 0; slot < slots; ++slot) {
			ItemStack invStack = invHandler.getStackInSlot(slot);
			if (!invStack.isEmpty()) {
				map.addTo(new ItemType(invStack, true, false), invStack.getCount());
				getWrapper(invStack).ifPresent(litematicaWrapper -> sophisticatedfabriccompat$processItemStack(map, litematicaWrapper.wrapper()));
			}
		}
	}
}
