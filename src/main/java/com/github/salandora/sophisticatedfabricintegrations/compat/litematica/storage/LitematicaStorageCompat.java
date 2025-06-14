package com.github.salandora.sophisticatedfabricintegrations.compat.litematica.storage;

import com.github.salandora.sophisticatedfabricintegrations.Config;
import com.github.salandora.sophisticatedfabricintegrations.compat.ICompat;
import net.p3pp3rf1y.sophisticatedstorage.block.ItemContentsStorage;
import net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedstorage.item.CapabilityStorageWrapper;
import net.p3pp3rf1y.sophisticatedstorage.network.StorageContentsMessage;

import static com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.LitematicaCompat.LITEMATICA_CAPABILITY;
import static com.github.salandora.sophisticatedfabricintegrations.compat.litematica.core.LitematicaCompat.LitematicaWrapper;

public class LitematicaStorageCompat implements ICompat {
	@Override
	public void setup() {
		if (!Config.Litematica.enableCompat || !Config.Litematica.enableStorageCompat) {
			return;
		}

		LITEMATICA_CAPABILITY.registerForItems(
				(stack, context) ->
						stack.sophisticatedLibrary_getLazyCapability(CapabilityStorageWrapper.getCapabilityInstance())
								.map(wrapper ->
										new LitematicaWrapper(
												wrapper,
												uuid -> new StorageContentsMessage(uuid, ItemContentsStorage.get().getOrCreateStorageContents(uuid))
										)
								).orElse(null),
				ModBlocks.ALL_STORAGECONTAINER_ITEMS);
	}
}
