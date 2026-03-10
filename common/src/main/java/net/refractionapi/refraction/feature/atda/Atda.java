package net.refractionapi.refraction.feature.atda;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RRuntimeConfig;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.examples.atda.AtdaExampleData;
import net.refractionapi.refraction.feature.examples.atda.AtdaExampleProvider;
import net.refractionapi.refraction.feature.examples.atda.AtdaExampleRegistry;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
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
public class Atda<E, D extends AtdaData<D>> {
    static final ConcurrentHashMap<ResourceLocation, Atda<?, ?>> REGISTRY = new ConcurrentHashMap<>();
    final ConcurrentHashMap<E, IAtdaProvider> providers = new ConcurrentHashMap<>();
    final HashMap<String, D> clientLookup = new HashMap<>();
    final HashSet<E> markedDiscarded = new HashSet<>();
    final ResourceLocation id;
    final Class<E> clazz;
    private TriConsumer<MinecraftServer, E, D> attachHook = (server, obj, rawData) -> {
    };

    private Atda(Class<E> clazz, ResourceLocation identifier) {
        this.clazz = clazz;
        this.id = identifier;
    }

    public static <O, D extends AtdaData<D>> void attachHook(Atda<O, D> atda, TriConsumer<MinecraftServer, O, D> hook) {
        atda.attachHook = hook;
    }

    public static <O extends Entity, D extends AtdaData<D>> void syncOnLoad(Atda<O, D> atda) {
        attachHook(atda, (server, obj, raw) -> raw.sync(obj));
    }

    // E needs to be an IAtdaProvider! --Zeus
    public void add(E obj, IAtdaProvider provider) {
        if (!this.clazz.isInstance(obj)) return;
        if (provider instanceof AtdaProvider<?, ?> atdaProvider)
            atdaProvider.setType(this.clazz);
        if (!(obj instanceof IAtdaProvider atdaProvider)) return;
        if (atdaProvider.getLevel() == null || atdaProvider.getLevel().isClientSide) return;
        this.providers.put(obj, provider);
        var raw = getRaw(obj);
        var server = atdaProvider.getLevel().getServer();
        attachHook.accept(server, obj, raw);
        raw.onAttach(obj, server);
    }

    private void tryDereferenceObj(E obj) {
        if (!markedDiscarded.contains(obj)) return;
        markedDiscarded.remove(obj);
        providers.remove(obj);
    }

    public void unregisterSelf(E obj) {
        markedDiscarded.add(obj);
    }

    public static void markDiscarded(IAtdaProvider obj) {
        var registeredTo = filterBy(obj);
        for (Atda<IAtdaProvider, ?> atda : registeredTo) {
            atda.unregisterSelf(obj);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends AtdaData<T>> void saveForClient(String syncID, AtdaData<T> datum) {
        if (datum == null) return;
        if (!datum.getClass().isAssignableFrom(datum.getClass()))
            throw new RuntimeException("Invalid atda data class for client storage %s".formatted(datum.getClass().toString()));
        clientLookup.put(syncID, (D) datum);
    }

    public static <O, D extends AtdaData<D>> Atda<O, D> register(Class<O> clazz, String id) {
        return register(clazz, RModRegistrar.getCallerModID(2), id);
    }

    public static <O, D extends AtdaData<D>> Atda<O, D> register(Class<O> clazz, String id, AtdaProvider<?, ?> provider) {
        Atda<O, D> atda = register(clazz, id);
        register(clazz, atda, provider);
        return atda;
    }

    public static <O, D extends AtdaData<D>> Atda<O, D> register(Class<O> clazz, String modId, String id) {
        return register(clazz, ResourceLocation.fromNamespaceAndPath(modId, id));
    }

    @SuppressWarnings("unchecked")
    public static <O, D extends AtdaData<D>> Atda<O, D> register(Class<O> clazz, ResourceLocation location) {
        // although we really don't need this
        // we want to make sure atda gets registered during mod construction
        // just in-case someone decides to try to make runtime atda?
        // --Zeus
        if (RRuntimeConfig.serverStarted) throw new RuntimeException("Atda must be registered during mod construction phase %s".formatted(location));
        if (REGISTRY.containsKey(location))
            throw new RuntimeException("Atda already registered %s".formatted(location));
        return (Atda<O, D>) REGISTRY.computeIfAbsent(location, (rl) -> new Atda<>(clazz, rl));
    }

    public static <T> void registerProvider(Class<T> clazz, Consumer<T> consumer) {
        RefractionEvents.REGISTER_ATDA.register((provider) -> {
            if (clazz.isInstance(provider)) consumer.accept(clazz.cast(provider));
        });
    }

    public static <T> void register(Class<T> clazz, Atda<T, ?> atda, AtdaProvider<?, ?> provider) {
        Atda.registerProvider(clazz, (type) -> AtdaUtils.attachAtda(type, atda, provider));
    }

    public static void registerCloning(Atda<Player, ?> atda) {
        RefractionEvents.PLAYER_CLONE.register((current, old) -> AtdaUtils.onClone(old, current, atda));
    }

    @SuppressWarnings("unchecked")
    public static <O, D extends AtdaData<D>> D getRaw(Atda<O, D> holder, O lookup) {
        if (!(lookup instanceof IAtdaProvider lookupProvider))
            throw new RuntimeException("Invalid lookup called for non-IAtdaProvider class %s".formatted(lookup.getClass().toString()));
        // check for registration
        // even with the holder present
        // we want to make sure it's registered --Zeus
        Atda<?, ?> proper = REGISTRY.get(holder.id);
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
    private <O extends IAtdaProvider> D internalGet(O lookup) {
        if (lookup.getLevel().isClientSide) return internalClientGet(lookup);
        if (!clazz.isInstance(lookup)) return null;
        // this is safe because of the above check --Zeus
        var holder = providers.get(lookup);
        if (holder == null) return null;
        Optional<D> data = holder.getAtda(this);
        if (data.isEmpty()) return null;
        data.get().setSyncables(lookup, this);
        return holder instanceof AtdaProvider<?, ?> provider ? (provider.readOnly(lookup) ? (D) provider.copyData() : data.get()) : data.get();
    }

    private <O extends IAtdaProvider> D internalClientGet(O lookupProvider) {
        return clientLookup.get(lookupProvider.getSyncID());
    }

    public static Atda<?, ?> fromMap(ResourceLocation id) {
        return REGISTRY.get(id);
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
        return filterBy(lookup)
                .stream()
                .map((atda) -> atda.providers.getOrDefault(lookup, null))
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static <O> List<Atda<O, ?>> filterBy(O lookup) {
        return REGISTRY.values().stream()
                .filter(atda -> atda.providers.containsKey(lookup))
                .map(atda -> (Atda<O, ?>) atda)
                .collect(Collectors.toList());
    }

    public static <O extends IAtdaProvider> CompoundTag serializeAll(O lookup) {
        CompoundTag tag = new CompoundTag();
        if (lookup == null) return tag;
        ListTag listTag = new ListTag();
        filterBy(lookup).forEach(((atda) -> {
            if (!atda.providers.containsKey(lookup)) return;
            CompoundTag atdaData = new CompoundTag();
            atdaData.putString("refraction_atda_reserved_data_fragment", atda.id.toString());
            var provider = atda.providers.get(lookup);
            CompoundTag serialized = new CompoundTag();
            provider.serialize(serialized);
            atdaData.put(provider.getClass().getName(), serialized);
            listTag.add(atdaData);
            atda.tryDereferenceObj(lookup);
        }));
        tag.put("refraction_atda", listTag);
        return tag;
    }

    public static <O extends IAtdaProvider> void deserializeAll(O lookup, CompoundTag tag) {
        if (tag == null || !tag.contains("refraction_reserved_atda")) return;
        CompoundTag serializedData = tag.getCompound("refraction_reserved_atda");
        ListTag listTag = serializedData.getList("refraction_atda", Tag.TAG_COMPOUND);
        if (listTag.isEmpty()) return;
        for (Tag t : listTag) {
            CompoundTag compoundTag = (CompoundTag) t;
            ResourceLocation location = ResourceLocation.tryParse(compoundTag.getString("refraction_atda_reserved_data_fragment"));
            Atda<?, ?> atda = REGISTRY.get(location);
            if (atda == null) {
                Refraction.LOGGER.warn("Atda not registered {}", location);
                continue;
            }
            var provider = atda.providers.get(lookup);
            if (provider == null) continue;
            provider.deserialize(compoundTag.getCompound(provider.getClass().getName()));
        }
    }
}
