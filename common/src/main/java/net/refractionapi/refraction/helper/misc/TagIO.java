package net.refractionapi.refraction.helper.misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.refractionapi.refraction.util.FileUtil;
import net.refractionapi.refraction.util.Pair;

import java.util.Arrays;

public class TagIO {
    private final String directoryPath;

    public TagIO(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    public TagIO(ServerLevel serverLevel, LevelResource resource) {
        this.directoryPath = serverLevel.getServer().getWorldPath(resource).toFile().toString();
    }

    public void createIfNotPresent(String fileName) {
        FileUtil.createIfNotPresent("%s/%s.nbt".formatted(directoryPath, fileName));
    }

    public void save(String fileName, CompoundTag tag) {
        FileUtil.saveCompound("%s/%s.nbt".formatted(directoryPath, fileName), tag);
    }

    public CompoundTag load(String fileName) {
        return FileUtil.loadCompound("%s/%s.nbt".formatted(directoryPath, fileName));
    }

    public void delete(String fileName) {
        FileUtil.deleteFile("%s/%s.nbt".formatted(directoryPath, fileName));
    }

    public boolean exists(String fileName) {
        return FileUtil.exists("%s/%s.nbt".formatted(directoryPath, fileName));
    }

    @SuppressWarnings("unchecked")
    public Pair<String, CompoundTag>[] list() {
        if (!FileUtil.exists(directoryPath)) return new Pair[0];
        return Arrays.stream(FileUtil.getFiles(directoryPath))
                .map(name -> new Pair<>(name, load(name.replace(".nbt", ""))))
                .toArray(Pair[]::new);
    }
}
