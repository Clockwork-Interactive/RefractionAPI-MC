package net.refractionapi.refraction.feature.loader;

import net.minecraft.world.entity.Entity;

public interface ChunkLoader<T extends Entity> {
    @SuppressWarnings("unchecked")
    default T entity() {
        return (T) this;
    }
}
