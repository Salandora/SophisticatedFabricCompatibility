package org.github.salandora.sophisticatedfabriccompat.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class InventoryProvider {
	public static final Set<String> SINGLE_IDENTIFIER = Collections.singleton("");

	public static final String FULL_INVENTORY = "fullPlayerInventory";

	public static final InventoryProvider INSTANCE = new InventoryProvider();


	private final Map<String, InventoryHandler> inventoryHandlers = new LinkedHashMap<>();

	private InventoryProvider() {
		registerInventory(FULL_INVENTORY,
				(player, gameTime) -> SINGLE_IDENTIFIER,
				(player, identifier) -> player.getInventory().getContainerSize(),
				(player, identifier, slot) -> player.getInventory().getItem(slot));
	}

	public void registerInventory(String name, BiFunction<Player,Long, Set<String>> identifiersGetter, SlotCountGetter slotCountGetter, SlotStackGetter slotStackGetter) {
		inventoryHandlers.put(name, new InventoryHandler(identifiersGetter, slotCountGetter, slotStackGetter));
	}

	public Optional<InventoryHandler> get(String name) {
		return Optional.ofNullable(inventoryHandlers.get(name));
	}

	public void runOnInventory(Player player, Consumer<ItemStack> consumer) {
		inventoryHandlers.keySet().forEach(name -> runOnInventory(name, player, consumer));
	}

	public void runOnInventory(String name, Player player, Consumer<ItemStack> consumer) {
		Optional<InventoryProvider.InventoryHandler> handler = get(name);
		handler.ifPresent(invHandler -> {
			Set<String> identifiers = new HashSet<>(invHandler.getIdentifiers(player, player.level().getGameTime()));
			for (String identifier : identifiers) {
				int slotCount = invHandler.getSlotCount(player, identifier);
				for (int slot = 0; slot < slotCount; slot++) {
					ItemStack stack = invHandler.getStackInSlot(player, identifier, slot);
					if (!stack.isEmpty()) {
						consumer.accept(stack);
					}
				}
			}
		});
	}

	public record InventoryHandler(BiFunction<Player,Long, Set<String>> identifiersGetter, SlotCountGetter slotCountGetter, SlotStackGetter slotStackGetter) {
		public int getSlotCount(Player player, String identifier) {
			return slotCountGetter.getSlotCount(player, identifier);
		}

		public ItemStack getStackInSlot(Player player, String identifier, int slot) {
			return slotStackGetter.getStackInSlot(player, identifier, slot);
		}

		public Set<String> getIdentifiers(Player player, long gameTime) {
			return identifiersGetter.apply(player, gameTime);
		}
	}

	@FunctionalInterface
	public interface SlotCountGetter {
		int getSlotCount(Player player, String identifier);
	}

	@FunctionalInterface
	public interface SlotStackGetter {
		ItemStack getStackInSlot(Player player, String identifier, int slot);
	}
}
