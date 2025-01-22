package net.refractionapi.refraction.helper.clazz;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.platform.RefractionServices;
import net.refractionapi.refraction.util.ClientInitializers;

import java.util.HashMap;

// TODO Auto register mods
public class RModRegistrar {
    private static final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private static final HashMap<String, String> modIDMap = new HashMap<>();

    public static String getCallerModID() {
        return modIDMap.get(getSignature(walker.getCallerClass()));
    }

    public static String getCallerModID(int depth) {
        return modIDMap.get(getSignature(walker.walk(frames -> frames.skip(depth).findFirst().get().getDeclaringClass())));
    }

    public static void registerSelf(String modID) {
        String sig = getSignature(walker.getCallerClass());
        modIDMap.put(sig, modID);
        if (RefractionServices.PLATFORM.isClient())
            ClientInitializers.init(sig);
    }

    public static void registerMod(String signature, String modID) {
        modIDMap.put(signature, modID);
    }

    public static String[] getPaths() {
        return modIDMap.values().toArray(new String[0]);
    }

    public static ResourceLocation id(String id, int depth) {
        return ResourceLocation.tryBuild(getCallerModID(depth), id);
    }

    public static ResourceLocation id(String id) {
        return id(id, 1);
    }

    public static ResourceLocation id(Class<?> caller, String id) {
        return ResourceLocation.tryBuild(modIDMap.get(getSignature(caller)), id);
    }

    private static String getSignature(Class<?> clazz) {
        String[] id = clazz.getPackageName().split("[.]");
        return "%s.%s.%s".formatted(id[0], id[1], id[2]);
    }
}
