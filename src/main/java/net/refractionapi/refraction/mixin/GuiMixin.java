package net.refractionapi.refraction.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoader;
import net.refractionapi.refraction.event.events.RegisterGuiEvent;
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
    public void init(Minecraft pMinecraft, CallbackInfo ci) { // There has to be a default way of doing this -- Zeus
        RegisterGuiEvent event = new RegisterGuiEvent();
        ModLoader.get().postEvent(event);
        this.layers.add(event.getDraw(), () -> true);
    }

}
