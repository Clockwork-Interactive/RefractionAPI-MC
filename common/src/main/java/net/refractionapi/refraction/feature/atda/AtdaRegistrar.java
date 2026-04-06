package net.refractionapi.refraction.feature.atda;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.config.RRuntimeConfig;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;
import net.refractionapi.refraction.util.Registry;

import java.util.function.Consumer;

public class AtdaRegistrar {
    protected static final Registry<ResourceLocation, Atda<?, ?>> REGISTRY = new Registry<>(Refraction.id("atda"));

    public static <T> void registerProvider(Class<T> clazz, Consumer<T> consumer) {
        RefractionEvents.REGISTER_ATDA.register((provider) -> {
            if (clazz.isInstance(provider)) consumer.accept(clazz.cast(provider));
        });
    }

    public static <T> void registerProvider(Class<T> clazz, Atda<T, ?> atda, AtdaProvider<?, ?> provider) {
        registerProvider(clazz, (type) -> AtdaUtils.attachAtda(type, atda, provider));
    }

    public static void registerCloning(Atda<Player, ?> atda) {
        RefractionEvents.PLAYER_CLONE.register((current, old) -> AtdaUtils.onClone(old, current, atda));
    }

    public static <O, D extends AtdaData<D>> Atda<O, D> register(
            Class<O> clazz,
            String id,
            Atda.AtdaConstructor<O, D> constructor
    ) {
        return register(clazz, RModRegistrar.getCallerModID(2), id, constructor);
    }

    public static <O, D extends AtdaData<D>, A extends Atda<O, D>> A register(
            Class<O> clazz,
            String id,
            AtdaProvider<O, D> provider,
            Atda.AtdaConstructor<O, D> constructor
    ) {
        var atda = register(clazz, id, constructor);
        registerProvider(clazz, atda, provider);
        return (A) atda;
    }

    public static <D extends AtdaData<D>> LevelAtda<D> registerLevel(String modId, String id) {
        return register(
                ServerLevel.class,
                modId, id,
                LevelAtda<D>::new
        );
    }

    public static <E extends Entity, D extends AtdaData<D>> EntityAtda<E, D> registerEntity(Class<E> clazz, String modId, String id) {
        return register(
                clazz,
                modId, id,
                EntityAtda<E, D>::new
        );
    }

    public static <O, D extends AtdaData<D>, A extends Atda<O, D>> A register(
            Class<O> clazz,
            String modId,
            String id,
            Atda.AtdaConstructor<O, D> constructor
    ) {
        return register(clazz, ResourceLocation.fromNamespaceAndPath(modId, id), constructor);
    }

    public static <O, D extends AtdaData<D>, A extends Atda<O, D>> A register(
            Class<O> clazz,
            ResourceLocation location,
            Atda.AtdaConstructor<O, D> constructor
    ) {
        if (RRuntimeConfig.serverStarted)
            throw new RuntimeException("Atda must be registered during mod construction phase %s".formatted(location));
        if (REGISTRY.containsKey(location))
            throw new RuntimeException("Atda already registered %s".formatted(location));
        var atda = constructor.create(clazz, location);
        REGISTRY.register(location, atda);
        return (A) atda;
    }

    public static Atda<?, ?> get(ResourceLocation id) {
        return REGISTRY.get(id);
    }
}
