package com.github.salandora.sophisticatedfabricintegrations.compat.litematica.storage;

import com.github.salandora.sophisticatedfabricintegrations.Config;
import com.github.salandora.sophisticatedfabricintegrations.compat.ICompat;
import net.minecraft.world.item.BlockItem;
import net.p3pp3rf1y.sophisticatedstorage.block.ItemContentsStorage;
import net.p3pp3rf1y.sophisticatedstorage.item.StackStorageWrapper;
import net.p3pp3rf1y.sophisticatedstorage.network.StorageContentsPayload;

import java.util.function.Supplier;

import static com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.LitematicaCompat.LITEMATICA_CAPABILITY;
import static com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.LitematicaCompat.LitematicaWrapper;
import static net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks.ALL_STORAGECONTAINER_ITEMS;

public class LitematicaStorageCompat implements ICompat {
	@Override
	public void setup() {
		if (!Config.CONFIG.LITEMATICA.enableCompat() || !Config.CONFIG.LITEMATICA.enableStorageCompat()) {
			return;
		}

		LITEMATICA_CAPABILITY.registerForItems(
				(stack, context) ->
						new LitematicaWrapper(
								StackStorageWrapper.fromStack(null, stack),
								uuid -> new StorageContentsPayload(uuid, ItemContentsStorage.get().getOrCreateStorageContents(uuid))
						),
				ALL_STORAGECONTAINER_ITEMS.stream().map(Supplier::get).toArray(BlockItem[]::new));
	}
}
