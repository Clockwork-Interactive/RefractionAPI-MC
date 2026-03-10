package net.refractionapi.refraction.feature.loader;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import jdk.jfr.Experimental;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.LevelResource;
import net.refractionapi.refraction.helper.misc.TagIO;
import net.refractionapi.refraction.mixininterfaces.ILevel;
import net.refractionapi.refraction.util.FileUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

// lightweight entity chunk loader --Zeus
@Experimental // yk this needs a lot of testing, so marking it as such for now --Zeus
public class LoadedChunkTracker {
    private static final HashMap<ServerLevel, LoadedChunkTracker> TRACKERS = new HashMap<>();
    private static final LevelResource RESOURCE = FileUtil.createResource("chunkTrackers");
    protected final TagIO cache;
    // chunkID to tracking map --Zeus
    protected final Long2ObjectMap<HashSet<UUID>> loaders = new Long2ObjectOpenHashMap<>();
    protected final ServerLevel level;
    protected final ILevel iLevel;

    protected LoadedChunkTracker(ServerLevel level) {
        this.level = level;
        this.iLevel = (ILevel) level;
        this.cache = new TagIO(level, RESOURCE);
        TRACKERS.put(level, this);
    }

    public void init() {
        awakenCache();
    }

    private boolean allocateChunk(long chunkID, UUID loaderID) {
        if (!loaders.containsKey(chunkID)) loaders.put(chunkID, new HashSet<>());
        if (loaders.get(chunkID).contains(loaderID)) return false;
        return loaders.get(chunkID).add(loaderID);
    }

    private boolean deallocateChunk(long chunkID, UUID loaderID) {
        if (!loaders.containsKey(chunkID)) return false;
        loaders.get(chunkID).remove(loaderID);
        if (loaders.get(chunkID).isEmpty()) loaders.remove(chunkID);
        return true;
    }

    protected void updateLoader(ChunkLoader<?> loader, boolean force) {
        var ent = loader.entity();
        var entID = loader.entity().getUUID();
        var chunk = new ChunkPos(ent.blockPosition());
        iterateNear(chunk, pos -> {
            var chunkID = pos.toLong();
            var refresh = force ? allocateChunk(chunkID, entID) : deallocateChunk(chunkID, entID);
            if (refresh) forceChunk(pos, force);
        });
        saveCache();
    }

    protected void iterateNear(ChunkPos origin, Consumer<ChunkPos> posConsumer) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                var pos = new ChunkPos(origin.x + x, origin.z + z);
                posConsumer.accept(pos);
            }
        }
    }

    protected void forceChunk(ChunkPos pos, boolean add) {
        level.setChunkForced(pos.x, pos.z, add);
    }

    protected void saveCache() {
        var data = new CompoundTag();
        loaders.forEach((chunkID, loaded) -> {
            loaded.forEach((loaderID) -> {
                var loader = loader(loaderID);
                if (loader == null) return;
                var ent = loader.entity();
                var pos = ent.blockPosition();
                var datum = new CompoundTag();
                datum.putLong("pos", pos.asLong());
                data.put(loaderID.toString(), datum);
            });
        });
        cache.save(iLevel.prettyID(), data);
    }

    protected void awakenCache() {
        var data = cache.load(iLevel.prettyID());
        var keys = data.getAllKeys();
        for (var stringKey : keys) {
            var datum = (CompoundTag) data.get(stringKey);
            assert datum != null;
            var lastPos = BlockPos.of(datum.getLong("pos"));
            var lastChunkPos = new ChunkPos(lastPos);
            forceChunk(lastChunkPos, true);
        }
    }

    public ChunkLoader<?> loader(UUID uuid) {
        var ent = level.getEntity(uuid);
        return (ent instanceof ChunkLoader<?> loader) ? loader : null;
    }

    public static void notifyChanged(ChunkLoader<?> loader, boolean force) {
        var entity = loader.entity();
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        if (force && !entity.isAlive()) return;
        var tracker = TRACKERS.get(serverLevel);
        if (tracker != null) tracker.updateLoader(loader, force);
    }

    public static void initTracker(ServerLevel serverLevel) {
        var tracker = new LoadedChunkTracker(serverLevel);
        tracker.init();
    }
}