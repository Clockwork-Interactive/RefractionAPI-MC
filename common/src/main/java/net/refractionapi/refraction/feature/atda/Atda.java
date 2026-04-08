package net.refractionapi.refraction.feature.atda;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.feature.examples.atda.AtdaExampleData;
import net.refractionapi.refraction.feature.examples.atda.AtdaExampleProvider;
import net.refractionapi.refraction.feature.examples.atda.AtdaExampleRegistry;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Atda (Attached Data) is a data attachment system made for entities <br>
 * in the future, it will be able to attach data to levels, chunks, blocks, etc <br>
 * See classes for registry help: <br>
 * {@link AtdaExampleRegistry} <br>
 * {@link AtdaExampleData} <br>
 * {@link AtdaExampleProvider} <br>
 * {@link AtdaUtils}
 */
// tldr: very fancy synced serializable hash map --Zeus
public abstract class Atda<E, D extends AtdaData<D>> {
    final HashMap<String, D> clientLookup = new HashMap<>();
    final ResourceLocation id;
    final Class<E> clazz;
    private TriConsumer<MinecraftServer, E, D> attachHook = (server, obj, rawData) -> {
    };

    protected Atda(Class<E> clazz, ResourceLocation identifier) {
        this.clazz = clazz;
        this.id = identifier;
    }

    public static <O, D extends AtdaData<D>> void attachHook(Atda<O, D> atda, TriConsumer<MinecraftServer, O, D> hook) {
        atda.attachHook = hook;
    }

    public static <O extends Entity, D extends AtdaData<D>> void syncOnLoad(Atda<O, D> atda) {
        attachHook(atda, (server, obj, raw) -> raw.sync(obj));
    }

    // E needs to be an FragmentHolder! --Zeus
    public void add(E obj, IAtdaProvider provider) {
        if (!this.clazz.isInstance(obj)) return;
        if (provider instanceof AtdaProvider<?, ?> atdaProvider)
            atdaProvider.setType(this.clazz);
        if (!(obj instanceof FragmentHolder fragmentHolder)) return;
        if (fragmentHolder.getLevel() == null || fragmentHolder.getLevel().isClientSide) return;
        fragmentHolder.addFragment(this.id, provider);
        var raw = getRaw(obj);
        var server = fragmentHolder.getLevel().getServer();
        attachHook.accept(server, obj, raw);
        raw.onAttach(obj, server);
    }

    @SuppressWarnings("unchecked")
    public <T extends AtdaData<T>> void saveForClient(String syncID, AtdaData<T> datum) {
        if (datum == null) return;
        if (!datum.getClass().isAssignableFrom(datum.getClass()))
            throw new RuntimeException("Invalid atda data class for client storage %s".formatted(datum.getClass().toString()));
        clientLookup.put(syncID, (D) datum);
    }

    @SuppressWarnings("unchecked")
    public static <O, D extends AtdaData<D>> D getRaw(Atda<O, D> holder, O lookup) {
        if (!(lookup instanceof FragmentHolder lookupProvider))
            throw new RuntimeException("Invalid lookup called for non-FragmentHolder class %s".formatted(lookup.getClass().toString()));
        // check for registration
        // even with the holder present
        // we want to make sure it's registered --Zeus
        Atda<?, ?> proper = AtdaRegistrar.get(holder.id);
        if (proper == null) throw new IllegalArgumentException("Atda not registered %s".formatted(holder.id));
        return (D) proper.internalGet(lookupProvider);
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

    @SuppressWarnings("all")
    private <O extends FragmentHolder> D internalGet(O lookup) {
        if (lookup.getLevel().isClientSide) return internalClientGet(lookup);
        if (!clazz.isInstance(lookup)) return null;
        // this is safe because of the above check --Zeus
        var holder = lookup.getFragment(id);
        if (holder == null) return null;
        Optional<D> data = holder.getAtda(this);
        if (data.isEmpty()) return null;
        data.get().setSyncables(lookup, this);
        return holder instanceof AtdaProvider<?, ?> provider ? (provider.readOnly(lookup) ? (D) provider.copyData() : data.get()) : data.get();
    }

    public <O> boolean isRegistered(O lookup) {
        if (!(lookup instanceof FragmentHolder holder)) return false;
        return holder.getFragment(id) != null;
    }

    private <O extends FragmentHolder> D internalClientGet(O lookupProvider) {
        return clientLookup.get(lookupProvider.getSyncID());
    }

    public static Atda<?, ?> fromMap(ResourceLocation id) {
        return AtdaRegistrar.get(id);
    }

    public static <O> void tickAllFor(O lookup) {
        getAllProvidersFor(lookup).stream()
                .filter((provider -> provider instanceof AtdaProvider<?, ?>))
                .map((atda) -> (AtdaProvider<?, ?>) atda)
                .forEach((provider -> {
                    if (provider.isValid(lookup))
                        provider.tickInternal(lookup);
                }));
    }

    public static <O> List<IAtdaProvider> getAllProvidersFor(O lookup) {
        return lookup instanceof FragmentHolder holder ? holder.getFragments() : List.of();
    }

    @SuppressWarnings("unchecked")
    private static <O> List<Atda<O, ?>> filterBy(O lookup) {
        return AtdaRegistrar.REGISTRY.values().stream()
                .filter(atda -> atda.isRegistered(lookup))
                .map(atda -> (Atda<O, ?>) atda)
                .collect(Collectors.toList());
    }

    @FunctionalInterface
    public interface AtdaConstructor<O, D extends AtdaData<D>> {
        Atda<O, D> create(Class<O> clazz, ResourceLocation id);
    }

    public static <O extends FragmentHolder> CompoundTag serializeAll(O lookup) {
        CompoundTag tag = new CompoundTag();
        if (lookup == null) return tag;
        ListTag listTag = new ListTag();
        filterBy(lookup).forEach(((atda) -> {
            if (!atda.isRegistered(lookup)) return;
            var atdaData = new CompoundTag();
            atdaData.putString("refraction_atda_reserved_data_fragment", atda.id.toString());
            var provider = lookup.getFragment(atda.id);
            var serialized = new CompoundTag();
            provider.serialize(serialized);
            atdaData.put(provider.getClass().getName(), serialized);
            listTag.add(atdaData);
        }));
        tag.put("refraction_atda", listTag);
        return tag;
    }

    public static <O extends FragmentHolder> void deserializeAll(O lookup, CompoundTag serialized) {
        if (serialized == null || !serialized.contains("refraction_reserved_atda")) return;
        var serializedData = serialized.getCompound("refraction_reserved_atda");
        var listTag = serializedData.getList("refraction_atda", Tag.TAG_COMPOUND);
        if (listTag.isEmpty()) return;
        for (var tag : listTag) {
            var compoundTag = (CompoundTag) tag;
            var location = ResourceLocation.tryParse(compoundTag.getString("refraction_atda_reserved_data_fragment"));
            var atda = AtdaRegistrar.get(location);
            if (atda == null) {
                Refraction.LOGGER.warn("Atda not registered {}", location);
                continue;
            }
            var provider = lookup.getFragment(atda.id);
            if (provider == null) continue;
            provider.deserialize(compoundTag.getCompound(provider.getClass().getName()));
        }
    }
}
