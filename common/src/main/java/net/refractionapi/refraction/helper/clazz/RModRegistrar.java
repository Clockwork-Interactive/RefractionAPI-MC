package net.refractionapi.refraction.helper.clazz;

import net.minecraft.resources.ResourceLocation;
import net.refractionapi.refraction.init.ModSpec;
import net.refractionapi.refraction.platform.RefractionServices;
import net.refractionapi.refraction.init.ClientInitializers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

// TODO Auto register mods
public class RModRegistrar {
    private static final StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
    private static final HashMap<String, String> modIDMap = new HashMap<>();
    protected static final HashMap<String, ModSpec> retainers = new HashMap<>();

    public static String getCallerModID() {
        return modIDMap.get(getSignature(walker.getCallerClass()));
    }

    public static String getCallerModID(int depth) {
        return modIDMap.get(getSignature(walker.walk(frames -> {
            List<StackWalker.StackFrame> frameList = frames.toList();
            return frameList.stream().skip(Math.min(frameList.size(), depth)).findFirst().get().getDeclaringClass();
        })));
    }

    public static void registerSelf(String modID) {
        String sig = getSignature(walker.getCallerClass());
        modIDMap.put(sig, modID);
        retainers.put(modID, new ModSpec(modID, sig));
        if (RefractionServices.PLATFORM.isClient())
            ClientInitializers.scanMod(modID, sig);
    }

    /**
     * You might want to use this if dealing with static inits. --Zeus
     */
    public static void registerSelfNoScan(String modID) {
        String sig = getSignature(walker.getCallerClass());
        modIDMap.put(sig, modID);
        retainers.put(modID, new ModSpec(modID, sig));
    }

    public static ModSpec getSpec(String modID) {
        return retainers.get(modID);
    }

    public static ModSpec getSpec() {
        return retainers.get(getCallerModID());
    }

    public static Collection<ModSpec> specs() {
        return retainers.values();
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

    public static String[] mods() {
        return modIDMap.entrySet().stream().map((e) -> "%s | %s".formatted(e.getValue(), e.getKey())).toArray(String[]::new);
    }

    private static String getSignature(Class<?> clazz) {
        String[] id = clazz.getPackageName().split("[.]");
        assert id.length >= 3 : "Class package id is not a length of 3 %s".formatted(clazz.toString());
        return "%s.%s.%s".formatted(id[0], id[1], id[2]);
    }
}
