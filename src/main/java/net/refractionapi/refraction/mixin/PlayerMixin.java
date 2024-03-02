package net.refractionapi.refraction.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.refractionapi.refraction.cutscenes.OLDCutscene;
import net.refractionapi.refraction.cutscenes.OLDCutsceneHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
    // Using a mixin instead of LivingHurtEvent for no knockback and no hurt anim.
    public void hurtInject(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir) {
        if (!OLDCutsceneHandler.QUEUE.containsKey((Player) this.self())) return;
        OLDCutscene current = OLDCutsceneHandler.QUEUE.getOrDefault((Player) this.self(), new ArrayList<>()).get(0);
        if (current != null) {
            if (current.invulnerable) cir.setReturnValue(false);
        }
    }

}
