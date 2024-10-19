package net.refractionapi.refraction.helper.registry;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.Refraction;

import java.util.function.Supplier;

public class RRegister<T> implements Supplier<T> {

    private final Supplier<T> value;
    private final ResourceLocation id;

    public RRegister(String id, Supplier<T> value) {
        this.value = value;
        this.id = Refraction.id(id);
    }

    @Override
    public T get() {
        return value.get();
    }

    public ResourceLocation getId() {
        return id;
    }

}
