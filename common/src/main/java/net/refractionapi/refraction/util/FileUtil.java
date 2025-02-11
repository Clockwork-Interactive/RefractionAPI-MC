package net.refractionapi.refraction.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;
import net.refractionapi.refraction.Refraction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
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

    public static String[] getFiles(String path) {
        return Path.of(URI.create(path)).toFile().list();
    }

    public static boolean createPath(String path) {
        Path p = Path.of(URI.create(path));
        return p.toFile().exists() || p.toFile().mkdirs();
    }

    public static boolean deleteFile(String path) {
        return Path.of(URI.create(path)).toFile().delete();
    }

    public static boolean exists(String path) {
        return Path.of(URI.create(path)).toFile().exists();
    }

    public static LevelResource createResource(String name) {
        try {
            return LevelResource.class.getDeclaredConstructor(String.class).newInstance(name);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Failed to create LevelResource directory!", e);
        }
    }

    public static boolean saveCompound(String path, CompoundTag tag) {
        if (!createPath(getDirectory(path))) {
            Refraction.LOGGER.error("Failed to create path {}", getDirectory(path));
            return false;
        }
        try {
            NbtIo.write(tag, Path.of(URI.create(path)));
            return true;
        } catch (IOException e) {
            Refraction.LOGGER.error("Failed to save compound to {}", path, e);
            return false;
        }
    }

    public static CompoundTag loadCompound(String path) {
        if (!createPath(getDirectory(path))) {
            Refraction.LOGGER.error("Failed to load path {}", getDirectory(path));
            return null;
        }
        try {
            return NbtIo.read(Path.of(URI.create(path)));
        } catch (IOException e) {
            Refraction.LOGGER.error("Failed to load compound from {}", path, e);
            return null;
        }
    }
}