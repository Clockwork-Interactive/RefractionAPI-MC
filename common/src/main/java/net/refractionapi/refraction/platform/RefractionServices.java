package net.refractionapi.refraction.platform;

import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.helper.registry.RRegistry;
import net.refractionapi.refraction.networking.RefractionMessages;
import net.refractionapi.refraction.platform.services.IPlatformHelper;
import net.refractionapi.refraction.util.Keybindings;

import java.util.ServiceLoader;

public class RefractionServices {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final RefractionMessages MESSAGES = load(RefractionMessages.class);
    public static final RefractionEvents EVENTS = load(RefractionEvents.class);
    public static final Keybindings KEYBINDINGS = load(Keybindings.class);
    public static final RRegistry REGISTRY = load(RRegistry.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Refraction.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }

}