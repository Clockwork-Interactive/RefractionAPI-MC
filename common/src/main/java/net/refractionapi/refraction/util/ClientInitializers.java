package net.refractionapi.refraction.util;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.feature.scheme.RScreen;
import net.refractionapi.refraction.feature.scheme.RegisterScreen;
import net.refractionapi.refraction.feature.scheme.ScreenRegistry;
import net.refractionapi.refraction.helper.clazz.ClazzUtil;
import net.refractionapi.refraction.helper.clazz.RModRegistrar;

import java.util.ArrayList;
import java.util.List;

public class ClientInitializers {
    // purely for keeping track of initializers and clearing them --Zeus
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final List<Object> initializers = new ArrayList<>();
    private static final RefractionClientEvents.Generic leave = RefractionClientEvents.CLIENT_PLAYER_LEAVE.register(ClientInitializers::clear);

    @SuppressWarnings("unchecked")
    public static void setup(String modID) {
        getClassesWithAnnotation(modID, InitSelf.class).forEach((cl) -> initializers.add(ClazzUtil.create(cl)));
        getClassesWithAnnotation(modID, RegisterScreen.class).forEach((cl) -> ScreenRegistry.register(cl.getAnnotation(RegisterScreen.class).value(), (Class<? extends RScreen>) cl));
    }

    public static void scanMod(String modID, String path) {
        ClassGraph.CIRCUMVENT_ENCAPSULATION = ClassGraph.CircumventEncapsulationMethod.NARCISSUS; // need this past java 16 --Zeus
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(path)
                .rejectPackages("mixins", "mixin") // sometimes prematurely loading mixins causes issues --Zeus
                .disableModuleScanning() // throws ''moduleReader.list()'' if not present --Zeus
                .scan()) {
            RModRegistrar.getRetainer(modID).result().set(scanResult);
            setup(modID);
        }
    }

    public static List<Class<?>> getClassesWithAnnotation(Class<?> annotation) {
        return RModRegistrar.retainers().stream()
                .map(mod -> getClassesWithAnnotation(mod.modID(), annotation))
                .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    }

    public static List<Class<?>> getClassesWithAnnotation(String modID, Class<?> annotation) {
        ScanResult result = RModRegistrar.getRetainer(modID).result().value;
        List<Class<?>> ret = new ArrayList<>();
        if (result == null) return ret;
        return result.getClassesWithAnnotation(annotation.getName()).loadClasses(true);
    }

    public static void clear() {
        initializers.clear();
    }
}