package net.refractionapi.refraction.feature.rendering;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public record RenderDispatcherContext(Map<PlayerSkin.Model, EntityRenderer<? extends Player>> playerRenderers, Map<EntityType<?>, EntityRenderer<?>> renderers) {
}
