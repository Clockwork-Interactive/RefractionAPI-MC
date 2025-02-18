package net.refractionapi.refraction.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderBuffers;
import net.refractionapi.refraction.events.LevelRenderContext;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.mixininterfaces.IAccessor;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin implements IAccessor {
    @Shadow @Final private RenderBuffers renderBuffers;
    @Shadow @Nullable private ClientLevel level;
    @Unique
    public LevelRenderContext context = new LevelRenderContext();
    
    @Inject(
            method = "renderLevel",
            at = @At(value = "HEAD")
    )
    private void prepare(DeltaTracker pDeltaTracker, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pFrustumMatrix, Matrix4f pProjectionMatrix, CallbackInfo ci) {
        context.prepare((LevelRenderer) (Object) this, pDeltaTracker, pCamera, pGameRenderer, pLightTexture, pFrustumMatrix, pProjectionMatrix, renderBuffers.bufferSource(), level);
    }

    @ModifyExpressionValue(method = "renderLevel", at = @At(value = "NEW", target = "()Lcom/mojang/blaze3d/vertex/PoseStack;"))
    private PoseStack setMatrixStack(PoseStack pose) {
        context.setPoseStack(pose);
        return pose;
    }
    
    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    ordinal =10
            )
    )
    private void beforeEntities(CallbackInfo ci) {
        RefractionClientEvents.BEFORE_ENTITIES.invoker().onRender(context);
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V",
                    ordinal = 2
            )
    )
    private void postAll(DeltaTracker pDeltaTracker, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pFrustumMatrix, Matrix4f pProjectionMatrix, CallbackInfo ci) {
        RefractionClientEvents.POST_ALL.invoker().onRender(context);
    }

    @Override
    public LevelRenderContext getLevelRenderContext() {
        return context;
    }
}
