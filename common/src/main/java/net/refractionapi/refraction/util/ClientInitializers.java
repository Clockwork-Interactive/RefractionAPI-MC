package net.refractionapi.refraction.util;

import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionClientEvents;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ClientInitializers {
    // purely for keeping track of initializers and clearing them --Zeus
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final List<Object> initializers = new ArrayList<>();

    public static void init(String path) {
        for (Class<?> declaredClass : new RScanner(path).withType(InitSelf.class).scan()) {
            try {
                declaredClass.getConstructor().setAccessible(true);
                initializers.add(declaredClass.getConstructor().newInstance());
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                     NoSuchMethodException e) {
                Refraction.LOGGER.error("Failed to initialize {} with @InitSelf", declaredClass.getName(), e);
            }
        }
        RefractionClientEvents.CLIENT_PLAYER_LEAVE.register(ClientInitializers::clear);
    }

    public static void clear() {
        initializers.clear();
    }
}
