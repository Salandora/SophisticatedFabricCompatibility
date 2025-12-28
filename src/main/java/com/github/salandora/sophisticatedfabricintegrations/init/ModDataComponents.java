package com.github.salandora.sophisticatedfabricintegrations.init;

import com.github.salandora.sophisticatedfabricintegrations.SophisticatedFabricIntegrations;
import io.github.fabricators_of_create.porting_lib.util.DeferredRegister;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModDataComponents {
	public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, SophisticatedFabricIntegrations.MOD_ID);

	public static void register() {
		DATA_COMPONENT_TYPES.register();
	}
}
