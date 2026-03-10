package net.refractionapi.refraction.init;

import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.scheme.RScreen;
import net.refractionapi.refraction.feature.scheme.RegisterScreen;
import net.refractionapi.refraction.feature.scheme.ScreenRegistry;
import net.refractionapi.refraction.platform.RefractionServices;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dynamic annotation processor and registry dispatcher.
 * --Zeus
 */
public class Reprocessor {
    private static volatile Reprocessor instance;
    private final Queue<Registry> registryDispatch = new ArrayDeque<>();
    private final Set<Class<?>> dispatchableClasses = new HashSet<>();
    private final HashMap<Class<? extends Annotation>, Config> processes = new HashMap<>();
    // created object cache --Zeus
    private final HashMap<Class<? extends Annotation>, Set<Object>> cache = new HashMap<>();

    public Reprocessor() {
        if (RefractionServices.PLATFORM.isClient()) registerClient();
    }

    private void registerNext() {
        var next = registryDispatch.poll();
        if (next == null) return;
        next.register(this);
    }

    private void registerAll() {
        while (!registryDispatch.isEmpty()) registerNext();
    }

    private void dispatchAll(Object... params) {
        processes.forEach((key, value) -> dispatchRegistry(key, params));
    }

    private Config getConfig(Class<? extends Annotation> annotation) {
        return processes.get(annotation);
    }

    private Set<Annotation> getAnnotations(Class<?> clazz) {
        return Set.of(clazz.getAnnotations());
    }

    private Set<Class<?>> getAnnotatedClasses(Class<? extends Annotation> annotation) {
        return dispatchableClasses.stream()
                .filter(cl -> cl.getAnnotation(annotation) != null)
                .collect(Collectors.toSet());
    }

    public void dispatchRegistry(Class<? extends Annotation> annotation, Object... params) {
        var config = getConfig(annotation);
        if (config == null) {
            Refraction.LOGGER.warn("No process registered for annotation: {}", annotation.getName());
            return;
        }
        for (var cl : getAnnotatedClasses(annotation)) config.processor.process(cl, params);
    }

    public static Reprocessor queueClient(Registry registry) {
        var instance = instance();
        if (!RefractionServices.PLATFORM.isClient()) return instance;
        instance.queueRegistry(registry);
        return instance;
    }

    public static <T> Reprocessor queueServer(Registry registry) {
        var instance = instance();
        instance.queueRegistry(registry);
        return instance;
    }

    private void queueRegistry(Registry registry) {
        registryDispatch.add(registry);
    }

    public void registerClient(Class<?> clazz) {
        registerFor(clazz);
    }


    public void registerServer(Class<?> clazz) {
        registerFor(clazz);
    }

    private void registerFor(Class<?> clazz) {
        dispatchableClasses.add(clazz);
        for (var annotation : getAnnotations(clazz)) {
            var config = getConfig(annotation.annotationType());
            if (config == null) {
                Refraction.LOGGER.warn("No annotation processor for {}", annotation.annotationType().getName());
                continue;
            }
            if (config.dispatchOnRegister) config.process(clazz);
        }
    }

    private Config registerProcessor(Class<? extends Annotation> annotation, Processor process) {
        var config = new Config(process);
        this.processes.put(annotation, config);
        return config;
    }

    private void registerClient() {
        registerClientProcessors();
        registerClientHandlers();
    }

    private void registerClientHandlers() {
        RefractionClientEvents.CLIENT_PLAYER_LEAVE.register(this::clear);
        RefractionClientEvents.CLIENT_PLAYER_JOIN.register(() -> {
            registerClientProcessors();
            registerAll();
            dispatchAll();
        });
    }

    private void clear() {
    }

    private void registerServerProcessors() {

    }

    @SuppressWarnings("unchecked")
    private void registerClientProcessors() {
        registerProcessor(RegisterScreen.class, (cl, params) -> {
            ScreenRegistry.register(
                    cl.getAnnotation(RegisterScreen.class).value(),
                    (Class<? extends RScreen>) cl
            );
        }).dispatchOnRegister();
    }

    public static Reprocessor instance() {
        if (instance == null) safeCreateInstance();
        return instance;
    }

    private static void safeCreateInstance() {
        synchronized (Reprocessor.class) {
            if (instance == null) instance = new Reprocessor();
        }
    }

    static {
        RefractionEvents.SERVER_STARTED.register(ms -> {
            var instance = instance();
            instance.registerServerProcessors();
            instance.registerAll();
            instance.dispatchAll(ms);
        });
    }

    @FunctionalInterface
    public interface Registry {
        void register(Reprocessor reprocessor);
    }

    @FunctionalInterface
    public interface Processor {
        void process(Class<?> clazz, Object... params);
    }

    public static class Config {
        boolean dispatchOnRegister = false;
        final Processor processor;

        public Config(Processor processor) {
            this.processor = processor;
        }

        public void process(Class<?> clazz, Object... params) {
            processor.process(clazz, params);
        }

        public Config dispatchOnRegister() {
            this.dispatchOnRegister = true;
            return this;
        }
    }
}