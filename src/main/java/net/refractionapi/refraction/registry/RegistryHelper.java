package net.refractionapi.refraction.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class RegistryHelper {

    public static <T> Holder<T> getRegistry(HolderLookup.Provider provider, ResourceKey<Registry<T>> registry, ResourceKey<T> key) {
        return provider.lookupOrThrow(registry).getOrThrow(key);
    }

}
