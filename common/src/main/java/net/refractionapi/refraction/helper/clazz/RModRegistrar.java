package net.refractionapi.refraction.helper.clazz;

import net.minecraft.resources.ResourceLocation;

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
        modIDMap.put(getSignature(walker.getCallerClass()), modID);
    }

    public static void registerMod(String signature, String modID) {
        modIDMap.put(signature, modID);
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
        return "%s:%s".formatted(id[1], id[2]);
    }

}
