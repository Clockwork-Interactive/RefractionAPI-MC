package net.refractionapi.refraction.mixin;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.refractionapi.refraction.client.ClientData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin extends Input {

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    public void tick(boolean p_234118_, float p_234119_, CallbackInfo ci) {
        if (!ClientData.canMove) {
            this.forwardImpulse = 0.0F;
            this.leftImpulse = 0.0F;
            this.jumping = false;
            ci.cancel();
        }
    }

}
