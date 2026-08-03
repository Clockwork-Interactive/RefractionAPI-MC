package net.refractionapi.refraction.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.atda.Atda;
import net.refractionapi.refraction.feature.atda.AtdaData;
import net.refractionapi.refraction.feature.atda.FragmentHolder;
import net.refractionapi.refraction.feature.atda.IAtdaProvider;
import net.refractionapi.refraction.feature.loader.ChunkLoader;
import net.refractionapi.refraction.feature.loader.LoadedChunkTracker;
import net.refractionapi.refraction.feature.quest.QuestHandler;
import net.refractionapi.refraction.feature.quest.points.InteractionPoint;
import net.refractionapi.refraction.mixininterfaces.IEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntity, FragmentHolder {
    @Unique
    public final HashMap<ResourceLocation, IAtdaProvider> providers = new HashMap<>();

    @Shadow
    public abstract int getId();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract UUID getUUID();

    @Inject(method = "setPosRaw", at = @At("HEAD"))
    public void updateTrackerStart(double x, double y, double z, CallbackInfo ci) {
        if (!(this instanceof ChunkLoader<?> loader)) return;
        LoadedChunkTracker.notifyChanged(loader, false);
    }

    @Inject(method = "setPosRaw", at = @At("RETURN"))
    public void updateTrackerEnd(double x, double y, double z, CallbackInfo ci) {
        if (!(this instanceof ChunkLoader<?> loader)) return;
        LoadedChunkTracker.notifyChanged(loader, true);
    }

    @Inject(at = @At("RETURN"), method = "interact")
    public void interact(Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() != InteractionResult.FAIL) {
            if (QuestHandler.QUESTS.containsKey(pPlayer.getUUID())) {
                QuestHandler.QUESTS.get(pPlayer.getUUID()).getQuestPoints().forEach(questPoint -> {
                    if (questPoint instanceof InteractionPoint point) {
                        point.onInteract((Entity) (Object) this);
                    }
                });
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void initEntity(EntityType<?> pEntityType, Level pLevel, CallbackInfo ci) {
        RefractionEvents.REGISTER_ATDA.invoker().register(this);
        if (!(this instanceof ChunkLoader<?> loader)) return;
        LoadedChunkTracker.notifyChanged(loader, true);
    }

    @Inject(at = @At("TAIL"), method = "setRemoved")
    public void removeEntity(Entity.RemovalReason reason, CallbackInfo ci) {
        if (!(this instanceof ChunkLoader<?> loader)) return;
        LoadedChunkTracker.notifyChanged(loader, false);
    }

    @Inject(at = @At("RETURN"), method = "saveWithoutId", cancellable = true)
    public void addInject(CompoundTag pCompound, CallbackInfoReturnable<CompoundTag> cir) {
        CompoundTag tag = Atda.serializeAll(this);
        pCompound.put("refraction_reserved_atda", tag);
        cir.setReturnValue(pCompound);
    }

    @Inject(at = @At("RETURN"), method = "load")
    public void loadInject(CompoundTag pCompound, CallbackInfo ci) {
        Atda.deserializeAll(this, pCompound);
    }

    @Inject(at = @At("RETURN"), method = "tick")
    public void tick(CallbackInfo ci) {
        Atda.tickAllFor(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C, D extends IAtdaProvider> void addData(Atda<C, ?> registry, D provider) {
        if (registry == null) throw new UnsupportedOperationException("Registry can't be null");
        if (provider == null) throw new UnsupportedOperationException("Provider can't be null");
        registry.add((C) this, provider);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <O, D extends AtdaData<D>> Optional<D> getAtda(Atda<O, D> holder) {
        return Atda.get(holder, (O) this);
    }

    @Override
    public HashMap<ResourceLocation, IAtdaProvider> fragments() {
        return providers;
    }

    @Override
    public String getSyncID() {
        return "" + this.getUUID();
    }

    @Override
    public Level getLevel() {
        return this.level();
    }
}
