package net.refractionapi.refraction.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;
import net.refractionapi.refraction.events.RefractionEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Final
    @Shadow
    private LayeredDraw layers;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(Minecraft pMinecraft, CallbackInfo ci) {
        RefractionEvents.REGISTER_LAYERS.invoker().register(layers);
    }

}
