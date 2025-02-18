package net.refractionapi.refraction.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
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
    private void prepare(PoseStack pose, float partial, long $$2, boolean outline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projection, CallbackInfo ci) {
        context.prepare((LevelRenderer) (Object) this, camera, gameRenderer, lightTexture, projection, renderBuffers.bufferSource(), level);
        context.setPoseStack(pose);
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
    private void postAll(PoseStack $$0, float $$1, long $$2, boolean $$3, Camera $$4, GameRenderer $$5, LightTexture $$6, Matrix4f $$7, CallbackInfo ci) {
        RefractionClientEvents.POST_ALL.invoker().onRender(context);
    }

    @Override
    public LevelRenderContext getLevelRenderContext() {
        return context;
    }
}
