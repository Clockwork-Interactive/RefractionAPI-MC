package net.refractionapi.refraction.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.events.RefractionEvents;
import net.refractionapi.refraction.feature.atda.Atda;
import net.refractionapi.refraction.feature.atda.AtdaData;
import net.refractionapi.refraction.feature.atda.IAtdaProvider;
import net.refractionapi.refraction.feature.quest.QuestHandler;
import net.refractionapi.refraction.feature.quest.points.InteractionPoint;
import net.refractionapi.refraction.mixininterfaces.IEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntity {

    @Shadow public abstract int getId();

    @Shadow public abstract Level level();

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
        Atda.tickProviders(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C, D extends IAtdaProvider> void addData(Atda<C, ?> registry, D providers) {
        if (registry == null) throw new UnsupportedOperationException("Registry can't be null");
        if (providers == null) throw new UnsupportedOperationException("Provider can't be null");
        registry.add((C) this, providers);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull <O, D extends AtdaData<D>> Optional<D> getAtda(Atda<O, D> holder) {
        return Atda.get(holder, (O) this);
    }

    @Override
    public String getSyncID() {
        return "" + this.getId();
    }

    @Override
    public Level getLevel() {
        return this.level();
    }
}
