package net.refractionapi.refraction.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.refractionapi.refraction.feature.misc.KillMessageController;
import net.refractionapi.refraction.feature.misc.MessageContainer;
import net.refractionapi.refraction.util.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CombatTracker.class)
public class CombatTrackerMixin {
    @Shadow
    @Final
    private LivingEntity mob;
    @Shadow
    @Final
    private List<CombatEntry> entries;

    @Inject(at = @At("HEAD"), method = "getDeathMessage", cancellable = true)
    public void deathMsg(CallbackInfoReturnable<Component> cir) {
        if (this.entries.isEmpty()) return;
        CombatEntry combatentry = this.entries.getLast();
        DamageSource damagesource = combatentry.source();
        Entity entity = damagesource.getEntity();
        if (!(entity instanceof KillMessageController controller)) return;
        MessageContainer container = new MessageContainer();
        controller.killMessages(container);
        Pair<String, String> msg = container.getMessages(damagesource.type()).get();
        cir.setReturnValue(Component.translatableWithFallback(msg.first, msg.second, this.mob.getDisplayName(), entity.getDisplayName()));
    }
}
