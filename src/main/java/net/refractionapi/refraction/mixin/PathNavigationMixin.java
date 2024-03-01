package net.refractionapi.refraction.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import net.refractionapi.refraction.mixininterfaces.ILivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PathNavigation.class)
public class PathNavigationMixin {

    @Shadow @Final protected Mob mob;

    @Inject(method = "moveTo(Lnet/minecraft/world/level/pathfinder/Path;D)Z", at = @At("HEAD"), cancellable = true)
    public void move(Path pPathentity, double pSpeed, CallbackInfoReturnable<Boolean> cir) {
        if (this.mob instanceof ILivingEntity ilivingEntity) {
            if (!ilivingEntity.refractionAPI_MC$canMove()) {
                cir.setReturnValue(false);
            }
        }
    }

}
