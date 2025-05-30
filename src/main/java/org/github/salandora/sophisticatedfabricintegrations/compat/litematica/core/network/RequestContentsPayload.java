package org.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.network.PacketDistributor;
import net.p3pp3rf1y.sophisticatedcore.util.InventoryHelper;
import net.p3pp3rf1y.sophisticatedcore.util.StreamCodecHelper;
import org.github.salandora.sophisticatedfabricintegrations.SophisticatedFabricIntegrations;
import org.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.LitematicaCompat;
import org.github.salandora.sophisticatedfabricintegrations.util.InventoryProvider;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.LitematicaCompat.LITEMATICA_CAPABILITY;

public record RequestContentsPayload() implements CustomPacketPayload {
	public static final Type<RequestContentsPayload> TYPE = new Type<>(SophisticatedFabricIntegrations.getRL("litematica_request_contents"));
	public static final StreamCodec<ByteBuf, RequestContentsPayload> STREAM_CODEC = StreamCodecHelper.singleton(RequestContentsPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handlePayload(RequestContentsPayload payload, ServerPlayNetworking.Context context) {
		ServerPlayer player = context.player();

		List<ItemStack> stacks = Lists.newArrayList();
		InventoryProvider.INSTANCE.runOnInventory(player, stacks::add);

		requestContents(stacks, packet -> context.responseSender().sendPacket(packet));
	}

	public static void requestContents(List<ItemStack> stacks, Consumer<CustomPacketPayload> consumer) {
		for (ItemStack stack : stacks) {
			LitematicaCompat.LitematicaWrapper litematicaWrapper = LITEMATICA_CAPABILITY.find(stack, null);
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
		CustomData data = stackShulkerBox.get(DataComponents.BLOCK_ENTITY_DATA);
		if (data != null && data.contains("Items")) {
			return !data.copyTag().getList("Items", Tag.TAG_COMPOUND).isEmpty();
		}

		return false;
	}

	public static NonNullList<ItemStack> getStoredItems(ItemStack stackIn) {
		ItemContainerContents container = stackIn.getComponents().get(DataComponents.CONTAINER);
		if (container == null) {
			return NonNullList.create();
		}

		NonNullList<ItemStack> items = NonNullList.createWithCapacity((int)container.nonEmptyStream().count());
		container.nonEmptyStream().forEach(items::add);
		return items;
	}
}
