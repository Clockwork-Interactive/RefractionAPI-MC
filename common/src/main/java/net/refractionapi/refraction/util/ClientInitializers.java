package net.refractionapi.refraction.util;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionClientEvents;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ClientInitializers {
    // purely for keeping track of initializers and clearing them --Zeus
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final List<Object> initializers = new ArrayList<>();
    private static final RefractionClientEvents.Generic leave = RefractionClientEvents.CLIENT_PLAYER_LEAVE.register(ClientInitializers::clear);

    public static void init(String path) {
        Class<?> annotation = InitSelf.class;
        ClassGraph.CIRCUMVENT_ENCAPSULATION = ClassGraph.CircumventEncapsulationMethod.NARCISSUS; // need this past java 16 --Zeus
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(path)
                .rejectPackages("mixins", "mixin") // sometimes prematurely loading mixins causes issues --Zeus
                .disableModuleScanning() // throws ''moduleReader.list()'' if not present --Zeus
                .scan()) {
            for (ClassInfo classInfo : scanResult.getClassesWithAnnotation(annotation.getName())) {
                Class<?> declaredClass = classInfo.loadClass();
                try {
                    declaredClass.getConstructor().setAccessible(true);
                    initializers.add(declaredClass.getConstructor().newInstance());
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    Refraction.LOGGER.error("Failed to initialize {} with @InitSelf", declaredClass.getName(), e);
                }
            }
        }
    }

    public static void clear() {
        initializers.clear();
    }
}