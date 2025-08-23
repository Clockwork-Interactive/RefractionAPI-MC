package net.refractionapi.refraction.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.atda.Atda;
import net.refractionapi.refraction.feature.atda.AtdaData;
import net.refractionapi.refraction.feature.atda.IAtdaProvider;
import net.refractionapi.refraction.feature.task.LevelTasks;
import net.refractionapi.refraction.helper.misc.TagIO;
import net.refractionapi.refraction.mixininterfaces.ILevel;
import net.refractionapi.refraction.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements ILevel {
    public LevelTasks levelTasks;

    @Shadow
    @Nonnull
    public abstract MinecraftServer getServer();

    private LevelResource resource = FileUtil.createResource("atda");
    private TagIO tagIO;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void initServer(MinecraftServer server, Executor dispatcher, LevelStorageSource.LevelStorageAccess levelStorageAccess, ServerLevelData serverLevelData, ResourceKey dimension, LevelStem levelStem, ChunkProgressListener progressListener, boolean isDebug, long biomeZoomSeed, List customSpawners, boolean tickTime, RandomSequences randomSequences, CallbackInfo ci) {
        RefractionEvents.REGISTER_ATDA.invoker().register(this);
        assureTasks();
        levelTasks.loadFromDisk((ServerLevel) (Object) this);
        levelTasks.init();
        //Atda.deserializeAll(this, getIO().load(getSyncID()));
    }

    @Inject(at = @At("TAIL"), method = "save")
    public void save(ProgressListener progress, boolean flush, boolean skipSave, CallbackInfo ci) {
        assureTasks();
        levelTasks.saveToDisk();
        //getIO().save(getSyncID(), Atda.serializeAll(this));
    }

    @Override
    public <C, D extends IAtdaProvider> void addData(Atda<C, ?> registry, D providers) {
        if (registry == null) throw new UnsupportedOperationException("Registry can't be null");
        if (providers == null) throw new UnsupportedOperationException("Provider can't be null");
        registry.add((C) this, providers);
    }

    @Override
    public @NotNull <O, D extends AtdaData<D>> Optional<D> getAtda(Atda<O, D> holder) {
        return Atda.get(holder, (O) this);
    }

    @Override
    public String getSyncID() {
        return "%s".formatted(getLevel().dimensionTypeRegistration().getRegisteredName().replace(":", "-"));
    }

    public void assureTasks() {
        if (levelTasks == null) levelTasks = (LevelTasks) new LevelTasks((ServerLevel) (Object) this).init();
    }

    @Override
    public LevelTasks tasks() {
        return levelTasks;
    }

    @Override
    public Level getLevel() {
        return (Level) (Object) this;
    }

    public TagIO getIO() {
        return tagIO = tagIO == null ? new TagIO(getServer().getWorldPath(resource).toString()) : tagIO;
    }
}
