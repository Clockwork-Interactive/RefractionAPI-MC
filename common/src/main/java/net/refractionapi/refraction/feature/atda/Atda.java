package net.refractionapi.refraction.feature.atda;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Atda (Attached Data) is a Refraction alternative to Capabilities <br>
 */
public class Atda<E, T extends AtdaData> {

    static final Map<ResourceLocation, Atda<?, ?>> data = new HashMap<>();
    final Map<E, List<IAtdaProvider<T>>> providers = new HashMap<>();
    final ResourceLocation id;

    private Atda(ResourceLocation identifier) {
        this.id = identifier;
    }

    public static <O, D extends AtdaData> Atda<O, D> register(String id) {
        return register(RModRegistrar.getCallerModID(2), id);
    }

    public static <O, D extends AtdaData> Atda<O, D> register(String modId, String id) {
        return register(ResourceLocation.tryBuild(modId, id));
    }

    @SuppressWarnings("unchecked")
    public static <O, D extends AtdaData> Atda<O, D> register(ResourceLocation location) {
        return (Atda<O, D>) data.computeIfAbsent(location, Atda::new);
    }

    @SuppressWarnings("unchecked")
    public static <O, D extends AtdaData> Optional<D> get(Atda<O, D> holder, O lookup) {
        Atda<?, ?> proper = data.get(holder.id);
        if (proper == null) throw new IllegalArgumentException("Atda not registered %s".formatted(holder.id));
        for (IAtdaProvider<?> iAtdaProvider : proper.providers.getOrDefault(lookup, List.of())) {
            Optional<D> data = (Optional<D>) iAtdaProvider.getAtda(holder);
            if (data.isPresent())
                return data;
        }
        return Optional.empty();
    }

}
