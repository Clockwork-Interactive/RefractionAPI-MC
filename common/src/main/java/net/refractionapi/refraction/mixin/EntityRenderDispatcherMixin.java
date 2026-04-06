package net.refractionapi.refraction.mixin;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.refractionapi.refraction.events.RefractionClientEvents;
import net.refractionapi.refraction.feature.rendering.ArmorRegister;
import net.refractionapi.refraction.feature.rendering.LayerHelper;
import net.refractionapi.refraction.feature.rendering.RefArmorRenderer;
import net.refractionapi.refraction.feature.rendering.RenderDispatcherContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow
    private Map<PlayerSkin.Model, EntityRenderer<? extends Player>> playerRenderers;

    @Shadow
    private Map<EntityType<?>, EntityRenderer<?>> renderers;

    @Shadow
    @Final
    private EntityModelSet entityModels;

    @Inject(
            method = "onResourceManagerReload",
            at = @At("TAIL"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void reload(ResourceManager resourceManager, CallbackInfo ci, EntityRendererProvider.Context context) {
        BuiltInRegistries.ITEM.forEach((item) -> {
            if (item instanceof ArmorRegister)
                RefArmorRenderer.cacheRenderer(item);
        });
        var ctx = new RenderDispatcherContext(playerRenderers, renderers);
        LayerHelper.registerOnAll(ctx, entityModels, RefArmorRenderer::new);
        RefractionClientEvents.REGISTER_LAYER.invoker().register(ctx, entityModels);
    }
}
