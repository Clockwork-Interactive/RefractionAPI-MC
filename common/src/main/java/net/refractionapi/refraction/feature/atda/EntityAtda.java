package net.refractionapi.refraction.feature.atda;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Optional;
import java.util.UUID;

public class EntityAtda<E extends Entity, D extends AtdaData<D>> extends Atda<E, D> {
    protected EntityAtda(Class<E> clazz, ResourceLocation identifier) {
        super(clazz, identifier);
    }

    public Optional<D> getClient(UUID uuid) {
        return Optional.ofNullable(clientLookup.get(uuid.toString()));
    }
}
