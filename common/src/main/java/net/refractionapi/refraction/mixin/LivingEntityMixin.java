package net.refractionapi.refraction.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.feature.cutscenes.Cutscene;
import net.refractionapi.refraction.feature.cutscenes.CutsceneHandler;
import net.refractionapi.refraction.mixininterfaces.ILivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntity {

    @Unique
    private boolean refractionAPI_MC$canMove = true;

    @Override
    public void refractionAPI_MC$enableMovement(boolean canMove) {
        if (!canMove) {
            ((LivingEntity) (Object) this).setDeltaMovement(0, 0, 0);
        }
        this.refractionAPI_MC$canMove = canMove;
    }

    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    // Using a mixin instead of LivingHurtEvent for no knockback and no hurt anim.
    public void hurtInject(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir) {
        List<Cutscene> cutscenes = CutsceneHandler.QUEUE.getOrDefault((LivingEntity) (Object) this, new ArrayList<>());
        if (cutscenes.isEmpty()) return;
        Cutscene current = cutscenes.get(0);
        if (current != null) {
            if (current.invulnerable) cir.setReturnValue(false);
        }
    }

    @Override
    public boolean refractionAPI_MC$canMove() {
        return refractionAPI_MC$canMove;
    }


}
