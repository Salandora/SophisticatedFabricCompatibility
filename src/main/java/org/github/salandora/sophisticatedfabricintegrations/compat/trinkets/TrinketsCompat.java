package org.github.salandora.sophisticatedfabricintegrations.compat.trinkets;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import org.github.salandora.sophisticatedfabricintegrations.compat.CompatModIds;
import org.github.salandora.sophisticatedfabricintegrations.compat.ICompat;
import org.github.salandora.sophisticatedfabricintegrations.util.InventoryProvider;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;

public class TrinketsCompat implements ICompat {
	private final int TAGS_REFRESH_COOLDOWN = 100;

	private final Set<String> backpackTrinketIdentifiers = new CopyOnWriteArraySet<>();
	private long lastTagsRefresh = -1;

	private <T> T getFromTrinketInventory(Player player, String identifier, Function<TrinketInventory, T> getFromHandler, T defaultValue) {
		return TrinketsApi.getTrinketComponent(player).map(comp -> {
			String[] identifiers = identifier.split("/");
			if (identifiers.length == 2) {
				if (comp.getInventory().containsKey(identifiers[0])) {
					Map<String, TrinketInventory> group = comp.getInventory().get(identifiers[0]);
					if (group.containsKey(identifiers[1])) {
						return getFromHandler.apply(group.get(identifiers[1]));
					}
				}
			}
			return defaultValue;
		}).orElse(defaultValue);
	}

	private Set<String> getTrinketTags(Player player, long gameTime) {
		if (lastTagsRefresh + TAGS_REFRESH_COOLDOWN < gameTime) {
			lastTagsRefresh = gameTime;

			backpackTrinketIdentifiers.clear();
			TrinketsApi.getTrinketComponent(player).ifPresent(comp -> {
				ItemStack backpack = new ItemStack(ModItems.BACKPACK.get());
				for (Map.Entry<String, Map<String, TrinketInventory>> group : comp.getInventory().entrySet()) {
					for (Map.Entry<String, TrinketInventory> inventory : group.getValue().entrySet()) {
						TrinketInventory trinketInventory = inventory.getValue();
						SlotType slotType = trinketInventory.getSlotType();

						for (int i = 0; i < trinketInventory.getContainerSize(); i++) {
							SlotReference ref = new SlotReference(trinketInventory, i);
							if (TrinketsApi.evaluatePredicateSet(slotType.getValidatorPredicates(), backpack, ref, player)) {
								backpackTrinketIdentifiers.add(group.getKey() + "/" + inventory.getKey());
							}
						}
					}
				}
			});
		}
		return backpackTrinketIdentifiers;
	}

	@Override
	public void setup() {
		InventoryProvider.INSTANCE.registerInventory(CompatModIds.TRINKETS, this::getTrinketTags,
				(player, identifier) -> getFromTrinketInventory(player, identifier, TrinketInventory::getContainerSize, 0),
				(player, identifier, slot) -> getFromTrinketInventory(player, identifier, ti -> ti.getItem(slot), ItemStack.EMPTY));
	}
}
