package org.github.salandora.sophisticatedfabriccompat.compat.litematica.storage;

import net.minecraft.world.item.BlockItem;
import net.p3pp3rf1y.sophisticatedstorage.block.ItemContentsStorage;
import net.p3pp3rf1y.sophisticatedstorage.item.StackStorageWrapper;
import net.p3pp3rf1y.sophisticatedstorage.network.StorageContentsPayload;
import org.github.salandora.sophisticatedfabriccompat.compat.ICompat;
import org.github.salandora.sophisticatedfabriccompat.config.Config;

import java.util.function.Supplier;

import static net.p3pp3rf1y.sophisticatedstorage.init.ModBlocks.ALL_STORAGECONTAINER_ITEMS;
import static org.github.salandora.sophisticatedfabriccompat.compat.litematica.core.LitematicaCompat.LITEMATICA_CAPABILITY;
import static org.github.salandora.sophisticatedfabriccompat.compat.litematica.core.LitematicaCompat.LitematicaWrapper;

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
