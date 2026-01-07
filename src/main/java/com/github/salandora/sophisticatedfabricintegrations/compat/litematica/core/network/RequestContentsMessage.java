package com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.network;

import com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.LitematicaCompat;
import com.github.salandora.sophisticatedfabriclib.network.api.v0.NetworkEvent;
import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RequestContentsMessage {
	public RequestContentsMessage() {
	}

	public static RequestContentsMessage decode(FriendlyByteBuf buffer) {
		return new RequestContentsMessage();
	}

	public static void encode(RequestContentsMessage msg, FriendlyByteBuf buffer) {
	}

	public static void onMessage(RequestContentsMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> handleMessage(contextSupplier.get().getSender(), msg));
		context.setPacketHandled(true);
	}

	private static void handleMessage(@Nullable ServerPlayer sender, RequestContentsMessage message) {
		if (sender == null) {
			return;
		}

		List<ItemStack> stacks = Lists.newArrayList();

		// Iterate over all slots cause this includes items, armor and offhand
		Container inv = sender.getInventory();
		int size = inv.getContainerSize();
		for (int slot = 0; slot < size; ++slot) {
			ItemStack stack = inv.getItem(slot);
			if (!stack.isEmpty()) {
				stacks.add(stack);
			}
		}

		requestContents(stacks, packet -> LitematicaPacketHandler.INSTANCE.sendToClient(sender, packet));
	}

	public static void requestContents(List<ItemStack> stacks, Consumer<Object> consumer) {
		for (ItemStack stack : stacks) {
			LitematicaCompat.LitematicaWrapper litematicaWrapper = LitematicaCompat.LITEMATICA_CAPABILITY.find(stack, null);
			if (litematicaWrapper != null) {
				IStorageWrapper wrapper = litematicaWrapper.wrapper();
				wrapper.getContentsUuid().ifPresent(uuid -> {
					consumer.accept(litematicaWrapper.packetGenerator().apply(uuid));

					List<ItemStack> wrapperStacks = Lists.newArrayList();
					InventoryHandler handler = wrapper.getInventoryHandler();
					for (int slot = 0; slot < handler.getSlotCount(); slot++) {
						ItemStack wrapperStack = handler.getSlotStack(slot);
						if (!wrapperStack.isEmpty()) {
							wrapperStacks.add(wrapperStack);
						}
					}
					requestContents(wrapperStacks, consumer);
				});
			} else if (stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof ShulkerBoxBlock && shulkerBoxHasItems(stack)) {
				requestContents(getStoredItems(stack), consumer);
			}
		}
	}

	public static boolean shulkerBoxHasItems(ItemStack stackShulkerBox) {
		CompoundTag nbt = stackShulkerBox.getTag();
		if (nbt != null && nbt.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
			CompoundTag tag = nbt.getCompound("BlockEntityTag");
			if (tag.contains("Items", Tag.TAG_LIST)) {
				ListTag tagList = tag.getList("Items", Tag.TAG_COMPOUND);
				return !tagList.isEmpty();
			}
		}

		return false;
	}

	public static NonNullList<ItemStack> getStoredItems(ItemStack stackIn) {
		CompoundTag nbt = stackIn.getTag();
		if (nbt != null && nbt.contains("BlockEntityTag", Tag.TAG_COMPOUND)) {
			CompoundTag tagBlockEntity = nbt.getCompound("BlockEntityTag");
			if (tagBlockEntity.contains("Items", Tag.TAG_LIST)) {
				NonNullList<ItemStack> items = NonNullList.create();
				ListTag tagList = tagBlockEntity.getList("Items", Tag.TAG_COMPOUND);

				int count = tagList.size();
				for(int i = 0; i < count; ++i) {
					ItemStack stack = ItemStack.of(tagList.getCompound(i));
					if (!stack.isEmpty()) {
						items.add(stack);
					}
				}

				return items;
			}
		}

		return NonNullList.create();
	}
}

