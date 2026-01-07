package com.github.salandora.sophisticatedfabricintegrations.compat.litematica.backpacks;

import com.github.salandora.sophisticatedfabricintegrations.Config;
import com.github.salandora.sophisticatedfabricintegrations.compat.ICompat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackContentsPayload;
import net.p3pp3rf1y.sophisticatedcore.inventory.InventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.upgrades.UpgradeHandler;

import java.util.UUID;
import java.util.function.Supplier;

import static com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.LitematicaCompat.LITEMATICA_CAPABILITY;
import static com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.LitematicaCompat.LitematicaWrapper;

public class LitematicaBackpacksCompat implements ICompat {
	private static CompoundTag getBackpackTag(UUID backpackUuid) {
		CompoundTag backpackContents = BackpackStorage.get().getOrCreateBackpackContents(backpackUuid);

		CompoundTag inventoryContents = new CompoundTag();
		Tag inventoryNbt = backpackContents.get(InventoryHandler.INVENTORY_TAG);
		if (inventoryNbt != null) {
			inventoryContents.put(InventoryHandler.INVENTORY_TAG, inventoryNbt);
		}
		Tag upgradeNbt = backpackContents.get(UpgradeHandler.UPGRADE_INVENTORY_TAG);
		if (upgradeNbt != null) {
			inventoryContents.put(UpgradeHandler.UPGRADE_INVENTORY_TAG, upgradeNbt);
		}

		return inventoryContents;
	}

	@Override
	public void init() {
		if (!Config.Litematica.enableCompat || !Config.Litematica.enableBackpacksCompat) {
			return;
		}

		LITEMATICA_CAPABILITY.registerForItems(
				(stack, context) ->
						new LitematicaWrapper(
								BackpackWrapper.fromStack(stack),
								uuid -> new BackpackContentsPayload(uuid, getBackpackTag(uuid))
						),
				ModItems.BACKPACKS.stream().map(Supplier::get).toArray(BackpackItem[]::new)
		);
	}

	@Override
	public void setup() {
	}
}
