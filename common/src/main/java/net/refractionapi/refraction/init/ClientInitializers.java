package net.refractionapi.refraction.init;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.feature.scheme.RScreen;
import net.refractionapi.refraction.feature.scheme.RegisterScreen;
import net.refractionapi.refraction.feature.scheme.ScreenRegistry;
import net.refractionapi.refraction.helper.clazz.ClazzUtil;
import net.refractionapi.refraction.util.InitSelf;

import java.util.ArrayList;
import java.util.List;

public class ClientInitializers {
    // purely for keeping track of initializers and clearing them --Zeus
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final List<Object> initializers = new ArrayList<>();
    private static final List<Class<?>> clientInits = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static void setup(ScanResult result) {
        getClassesWithAnnotation(result, InitSelf.class).forEach((cl) -> {
            if (clientInits.stream().noneMatch((c) -> c.getName().matches(cl.getName()))) clientInits.add(cl);
        });
        getClassesWithAnnotation(result, RegisterScreen.class).forEach((cl) -> ScreenRegistry.register(cl.getAnnotation(RegisterScreen.class).value(), (Class<? extends RScreen>) cl));
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
            setup(scanResult);
        } catch (Exception e) {
            Refraction.LOGGER.error("Couldn't load Mod Package Info [{}]", modID, e);
        }
    }

    public static List<Class<?>> getClassesWithAnnotation(ScanResult result, Class<?> annotation) {
        List<Class<?>> ret = new ArrayList<>();
        if (result == null) return ret;
        return result.getClassesWithAnnotation(annotation.getName()).loadClasses(true);
    }

    public static void clear() {
        initializers.removeIf((obj) -> obj.getClass().getAnnotation(InitSelf.class).value());
    }

    static {
        RefractionClientEvents.CLIENT_PLAYER_LEAVE.register(ClientInitializers::clear);
        RefractionClientEvents.CLIENT_PLAYER_JOIN.register(() -> clientInits.forEach((c) -> initializers.add(ClazzUtil.create(c))));
    }
}