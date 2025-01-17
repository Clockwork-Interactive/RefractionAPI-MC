package net.refractionapi.refraction.feature.atda;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.examples.atda.AtdaExampleData;
import net.refractionapi.refraction.feature.examples.atda.AtdaExampleProvider;
import net.refractionapi.refraction.feature.examples.atda.AtdaExampleRegistry;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Atda (Attached Data) is a data attachment system made for entities <br>
 * in the future, it will be able to attach data to levels, chunks, blocks, etc <br>
 * See classes for registry help: <br>
 * {@link AtdaExampleRegistry} <br>
 * {@link AtdaExampleData} <br>
 * {@link AtdaExampleProvider} <br>
 * {@link AtdaUtils}
 */
public class Atda<E, D extends AtdaData<D>> {

    static final ConcurrentHashMap<ResourceLocation, Atda<?, ?>> data = new ConcurrentHashMap<>();
    final ConcurrentHashMap<E, List<IAtdaProvider>> providers = new ConcurrentHashMap<>();
    final HashMap<String, AtdaData<?>> clientLookup = new HashMap<>();
    final ResourceLocation id;

    private Atda(ResourceLocation identifier) {
        this.id = identifier;
    }

    public void add(E obj, IAtdaProvider provider) {
        this.providers.computeIfAbsent(obj, k -> new ArrayList<>()).add(provider);
        getRaw(obj);
    }

    public static <O, D extends AtdaData<D>> Atda<O, D> register(String id) {
        return register(RModRegistrar.getCallerModID(2), id);
    }

    public static <O, D extends AtdaData<D>> Atda<O, D> register(String modId, String id) {
        return register(ResourceLocation.tryBuild(modId, id));
    }

    @SuppressWarnings("unchecked")
    public static <O, D extends AtdaData<D>> Atda<O, D> register(ResourceLocation location) {
        if (data.containsKey(location))
            throw new RuntimeException("Atda already registered %s".formatted(location));
        return (Atda<O, D>) data.computeIfAbsent(location, Atda::new);
    }

    public static <T> void registerProvider(Class<T> clazz, Consumer<T> consumer) {
        RefractionEvents.REGISTER_ATDA.register((provider) -> {
            if (clazz.isInstance(provider))
                consumer.accept(clazz.cast(provider));
        });
    }

    @SuppressWarnings("unchecked")
    public static <O, D extends AtdaData<D>> D getRaw(Atda<O, D> holder, O lookup) {
        Atda<?, ?> proper = data.get(holder.id);
        if (proper == null) throw new IllegalArgumentException("Atda not registered %s".formatted(holder.id));
        return (D) proper.internalGet(lookup);
    }

    public static <O, D extends AtdaData<D>> Optional<D> get(Atda<O, D> holder, O look) {
        return Optional.ofNullable(getRaw(holder, look));
    }

    public Optional<D> get(E lookup) {
        return get(this, lookup);
    }

    public D getRaw(E lookup) {
        return getRaw(this, lookup);
    }

    @SuppressWarnings("unchecked")
    private <O> D internalGet(O lookup) {
        if (!(lookup instanceof IAtdaProvider lookupProvider))
            throw new RuntimeException("Invalid lookup called for non-IAdtaProvider class %s".formatted(lookup.getClass().toString()));
        if (lookupProvider.getLevel().isClientSide) {
            AtomicReference<D> dAtomicReference = new AtomicReference<>();
            this.clientLookup.forEach((id, data) -> {
                if (id.equals(lookupProvider.getSyncID() + data.getClass().getName())) {
                    dAtomicReference.set((D) data);
                }
            });
            return dAtomicReference.get();
        }
        for (IAtdaProvider iAtdaProvider : safeGet(lookup)) {
            Optional<D> data = iAtdaProvider.getAtda(this);
            if (data.isPresent()) {
                data.get().setSyncables(lookupProvider, this);
                return iAtdaProvider instanceof AtdaProvider<?, ?> provider ? provider.readOnly(lookup) ? (D) provider.copyData() : data.get() : data.get();
            }
        }
        return null;
    }

    public static Atda<?, ?> fromMap(ResourceLocation id) {
        return data.get(id);
    }

    public static <O> void tickProviders(O lookup) {
        safeGet(lookup).stream().filter((provider -> provider instanceof AtdaProvider<?, ?>)).forEach((provider -> ((AtdaProvider<?, ?>) provider).tickInternal(lookup)));
    }

    public static <O> List<IAtdaProvider> safeGet(O lookup) {
        return data.values().stream().map(atda -> atda.providers.getOrDefault(lookup, new ArrayList<>())).reduce(new ArrayList<>(), (a, b) -> {
            a.addAll(b);
            return a;
        });
    }

    public static <O> CompoundTag serializeAll(O lookup) {
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        data.forEach(((resourceLocation, atda) -> {
            if (atda.providers.containsKey(lookup)) {
                CompoundTag atdaData = new CompoundTag();
                atdaData.putString("refraction_atda_reserved_data_fragment", atda.id.toString());
                for (IAtdaProvider iAtdaProvider : atda.providers.get(lookup)) {
                    CompoundTag serialized = new CompoundTag();
                    iAtdaProvider.serialize(serialized);
                    atdaData.put(iAtdaProvider.getClass().getName(), serialized);
                }
                listTag.add(atdaData);
            }
        }));
        tag.put("refraction_atda", listTag);
        return tag;
    }

    public static <O> void deserializeAll(O lookup, CompoundTag tag) {
        if (tag == null || !tag.contains("refraction_reserved_atda")) return;
        CompoundTag serializedData = tag.getCompound("refraction_reserved_atda");
        ListTag listTag = serializedData.getList("refraction_atda", Tag.TAG_COMPOUND);
        if (listTag == null || listTag.isEmpty()) return;
        for (Tag t : listTag) {
            CompoundTag compoundTag = (CompoundTag) t;
            ResourceLocation location = ResourceLocation.tryParse(compoundTag.getString("refraction_atda_reserved_data_fragment"));
            Atda<?, ?> atda = data.get(location);
            if (atda == null) {
                Refraction.LOGGER.warn("Atda not registered {}", location);
                continue;
            }
            for (IAtdaProvider iAtdaProvider : atda.providers.get(lookup)) {
                iAtdaProvider.deserialize(compoundTag.getCompound(iAtdaProvider.getClass().getName()));
            }
        }
    }

}
