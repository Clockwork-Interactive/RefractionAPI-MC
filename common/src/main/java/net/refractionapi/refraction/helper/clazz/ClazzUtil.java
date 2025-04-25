package net.refractionapi.refraction.helper.clazz;

import net.refractionapi.refraction.Refraction;

public class ClazzUtil {
    public static Class<?>[] formClassArray(Object... objects) {
        Class<?>[] classes = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            classes[i] = objects[i].getClass();
        }
        return classes;
    }

    public static <T> T create(Class<T> clazz, Object... args) {
        try {
            return clazz.getConstructor(formClassArray(args)).newInstance(args);
        } catch (Exception e) {
            Refraction.LOGGER.error("Failed to create screen", e);
            return null;
        }
    }
}
