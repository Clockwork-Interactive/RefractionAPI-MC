package net.refractionapi.refraction.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.debug.debuggers.RAABBRenderer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class RDebugRenderer {

    protected final Minecraft minecraft;
    protected static final HashMap<String, RDebugRenderer> renderers = new HashMap<>();
    public static final Set<String> enabled = new HashSet<>();
    protected final String id;

    /** Start of registries */
    public static RAABBRenderer aabbRenderer;

    public RDebugRenderer(String id) {
        this.minecraft = Minecraft.getInstance();
        this.id = id;
        renderers.put(id, this);
    }

    protected abstract void render(PoseStack poseStack, MultiBufferSource multiBufferSource);

    protected abstract void tick(boolean post);

    protected abstract void fromPacket(CompoundTag tag);

    protected void renderBox(AABB aabb, float red, float green, float blue, float alpha, PoseStack stack, MultiBufferSource source) {
        LevelRenderer.renderLineBox(stack, source.getBuffer(RenderType.LINES), aabb, red, green, blue, alpha);
    }

    protected void renderText(String string, Vec3 vec3, PoseStack stack, MultiBufferSource source) {
        DebugRenderer.renderFloatingText(stack, source, string, (float) vec3.x, (float) vec3.y, (float) vec3.z, -1);
    }

    protected Vec3 cameraPosition() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
    }

    public boolean isEnabled() {
        return isEnabled(this.id);
    }

    public static boolean isEnabled(String id) {
        return enabled.contains(id);
    }

    public static void toggle(String id) {
        if (enabled.contains(id)) {
            enabled.remove(id);
        } else {
            enabled.add(id);
        }
    }

    public static void renderAll(PoseStack poseStack, MultiBufferSource multiBufferSource) {
        renderers.values().forEach(renderer -> {
            if (renderer.isEnabled())
                renderer.render(poseStack, multiBufferSource);
        });
    }

    public static void tickAll(boolean post) {
        renderers.values().forEach(renderer -> {
            if (renderer.isEnabled())
                renderer.tick(post);
        });
    }

    public static Set<String> getRenderers() {
        return renderers.keySet();
    }

    public static void route(String id, CompoundTag tag) {
        RDebugRenderer renderer = renderers.get(id);
        if (renderer != null) {
            renderer.fromPacket(tag);
        } else {
            Refraction.LOGGER.warn("Router not found {}", id);
        }
    }

    public static void init() {
        aabbRenderer = new RAABBRenderer();
    }

}
