package net.refractionapi.refraction.helper.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class RegistryHelper {

    public static <T> Holder<T> getRegistry(HolderLookup.Provider provider, ResourceKey<Registry<T>> registry, ResourceKey<T> key) {
        return provider.lookupOrThrow(registry).getOrThrow(key);
    }

    public static <T> Holder<T> getRegistry(Level level, ResourceKey<Registry<T>> registry, ResourceKey<T> key) {
        return getRegistry(level.registryAccess(), registry, key);
    }

    public static <T> Holder<T> getRegistry(Entity entity, ResourceKey<Registry<T>> registry, ResourceKey<T> key) {
        return getRegistry(entity.level(), registry, key);
    }

}