package net.refractionapi.refraction.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;
import net.refractionapi.refraction.Refraction;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

public class FileUtil {
    public static String getExtension(String path) {
        int i = path.lastIndexOf('.');
        return i > 0 ? path.substring(i + 1) : "";
    }

    public static String getName(String path) {
        int i = path.lastIndexOf('/');
        return i > 0 ? path.substring(i + 1) : path;
    }

    public static String getFileName(String path) {
        int i = path.lastIndexOf('/');
        int j = path.lastIndexOf('.');
        return i > 0 ? path.substring(i + 1, j) : path.substring(0, j);
    }

    public static String getDirectory(String path) {
        int i = path.lastIndexOf('/');
        return i > 0 ? path.substring(0, i) : "";
    }

    public static String wrapDirectory(String path) {
        return new File(path).isAbsolute() ? path : "./%s".formatted(path);
    }

    public static String[] getFiles(String path) {
        return new File(path).list();
    }

    public static boolean createPath(String path) {
        File file = new File(path);
        return file.exists() || file.mkdirs();
    }

    public static boolean deleteFile(String path) {
        return new File(path).delete();
    }

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static LevelResource createResource(String name) {
        try {
            return LevelResource.class.getDeclaredConstructor(String.class).newInstance(name);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Failed to create LevelResource %s directory!".formatted(name), e);
        }
    }

    public static boolean createIfNotPresent(String path) {
        return exists(path) || saveCompound(path, new CompoundTag());
    }

    public static String defaultDir(String path) {
        return getDirectory(wrapDirectory(path));
    }

    public static boolean saveCompound(String path, CompoundTag tag) {
        if (!createPath(defaultDir(path))) {
            Refraction.LOGGER.error("Failed to create path {}", getDirectory(path));
            return false;
        }
        try {
            NbtIo.write(tag, Path.of(new File(wrapDirectory(path)).getPath()));
            return true;
        } catch (IOException e) {
            Refraction.LOGGER.error("Failed to save compound to {}", path, e);
            return false;
        }
    }

    public static CompoundTag loadCompound(String path) {
        if (!createPath(defaultDir(path))) {
            Refraction.LOGGER.error("Failed to load path {}", getDirectory(path));
            return null;
        }
        try {
            if (!createIfNotPresent(path)) {
                throw new IOException("invalid directory");
            }
            return NbtIo.read(Path.of(new File(wrapDirectory(path)).getPath()));
        } catch (IOException e) {
            Refraction.LOGGER.error("Failed to load compound from {}", path, e);
            return null;
        }
    }
}