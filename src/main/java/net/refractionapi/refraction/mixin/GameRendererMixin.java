package net.refractionapi.refraction.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.refractionapi.refraction.cutscenes.client.ClientCutsceneData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(at = @At("HEAD"), method = "renderItemInHand", cancellable = true)
    public void renderItemInHandInject(MatrixStack p_228381_1_, ActiveRenderInfo p_228381_2_, float p_228381_3_, CallbackInfo ci) {
        if (ClientCutsceneData.inCutscene) ci.cancel();
    }

}
