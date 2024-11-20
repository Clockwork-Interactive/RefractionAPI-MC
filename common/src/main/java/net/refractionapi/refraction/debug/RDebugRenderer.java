package net.refractionapi.refraction.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.refractionapi.refraction.Refraction;
import net.refractionapi.refraction.debug.debuggers.AABBRenderer;
import net.refractionapi.refraction.debug.debuggers.RAABBRenderer;
import net.refractionapi.refraction.helper.vec3.RAAB;
import net.refractionapi.refraction.helper.vec3.Vec3Helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class RDebugRenderer {

    protected final Minecraft minecraft;
    protected static final HashMap<String, RDebugRenderer> renderers = new HashMap<>();
    public static final Set<String> enabled = new HashSet<>();
    protected final String id;
    public static boolean advancedView = true; // TODO

    /**
     * Start of registries
     */
    public static AABBRenderer aabbRenderer;
    public static RAABBRenderer raabbRenderer;

    public RDebugRenderer(String id) {
        this.minecraft = Minecraft.getInstance();
        this.id = id;
        if (renderers.containsKey(id)) {
            Refraction.LOGGER.warn("Renderer already exists {}, overwriting!", id);
        }
        renderers.put(id, this);
    }

    protected abstract void render(PoseStack poseStack, MultiBufferSource multiBufferSource);

    protected abstract void tick(boolean post);

    protected abstract void fromPacket(CompoundTag tag);

    protected void renderLineBox(AABB aabb, float red, float green, float blue, float alpha, PoseStack stack, MultiBufferSource source) {
        LevelRenderer.renderLineBox(stack, source.getBuffer(RenderType.LINES), aabb, red, green, blue, alpha);
    }

    protected void renderLineBox(RAAB raab, float red, float green, float blue, float alpha, PoseStack stack, MultiBufferSource source) {
        stack.pushPose();

        Vec3 bottomLeft = raab.positions[0][0];
        Vec3 bottomRight = raab.positions[0][1];
        Vec3 topRight = raab.positions[0][2];
        Vec3 topLeft = raab.positions[0][3];

        Vec3 bottomLeft2 = raab.positions[1][0];
        Vec3 bottomRight2 = raab.positions[1][1];
        Vec3 topRight2 = raab.positions[1][2];
        Vec3 topLeft2 = raab.positions[1][3];

        renderLine(bottomLeft, bottomRight, red, green, blue, alpha, 1.0F, stack, source);
        renderLine(bottomRight, topRight, red, green, blue, alpha, 1.0F, stack, source);
        renderLine(topRight, topLeft, red, green, blue, alpha, 1.0F, stack, source);
        renderLine(topLeft, bottomLeft, red, green, blue, alpha, 1.0F, stack, source);

        renderLine(bottomLeft2, bottomRight2, red, green, blue, alpha, 1.0F, stack, source);
        renderLine(bottomRight2, topRight2, red, green, blue, alpha, 1.0F, stack, source);
        renderLine(topRight2, topLeft2, red, green, blue, alpha, 1.0F, stack, source);
        renderLine(topLeft2, bottomLeft2, red, green, blue, alpha, 1.0F, stack, source);

        renderLine(bottomLeft, bottomLeft2, red, green, blue, alpha, 1.0F, stack, source);
        renderLine(bottomRight, bottomRight2, red, green, blue, alpha, 1.0F, stack, source);
        renderLine(topRight, topRight2, red, green, blue, alpha, 1.0F, stack, source);
        renderLine(topLeft, topLeft2, red, green, blue, alpha, 1.0F, stack, source);

        renderText("bottomLeft", bottomLeft, stack, source);
        renderText("bottomRight", bottomRight.scale(2), stack, source);
        renderText("topRight", topRight.scale(2), stack, source);
        renderText("topLeft", topLeft.scale(2), stack, source);
        renderText("center", raab.getCenter().scale(2), stack, source);

        stack.popPose();
    }

    protected void renderBox(AABB aabb, float red, float green, float blue, float alpha, PoseStack stack, MultiBufferSource source) {
        DebugRenderer.renderFilledBox(stack, source, applyTransformation(aabb), red, green, blue, alpha);
    }

    protected void renderLine(Vec3 start, Vec3 end, float red, float green, float blue, float alpha, float lineWidth, PoseStack stack, MultiBufferSource source) {
        stack.pushPose();
        RenderSystem.lineWidth(lineWidth);
        VertexConsumer builder = source.getBuffer(RenderType.LINES);
        builder.addVertex(stack.last(), (float) (start.x), (float) (start.y), (float) (start.z)).setColor(red, green, blue, alpha).setNormal(1.0F, 0.0F, 0.0F);
        builder.addVertex(stack.last(), (float) (end.x), (float) (end.y), (float) (end.z)).setColor(red, green, blue, alpha).setNormal(1.0F, 0.0F, 0.0F);
        RenderSystem.lineWidth(1.0F);
        stack.popPose();
    }

    protected void renderLine(BlockPos start, BlockPos end, float red, float green, float blue, float alpha, float lineWidth, PoseStack stack, MultiBufferSource source) {
        renderLine(start.getCenter(), end.getCenter(), red, green, blue, alpha, lineWidth, stack, source);
    }

    protected void renderText(String string, Vec3 vec3, PoseStack stack, MultiBufferSource source) {
        DebugRenderer.renderFloatingText(stack, source, string, (float) vec3.x, (float) vec3.y, (float) vec3.z, -1);
    }

    protected AABB applyTransformation(AABB aabb) {
        Vec3 camera = this.cameraPosition();
        return new AABB(aabb.minX - camera.x - 0.51F, aabb.minY - camera.y - 0.51F, aabb.minZ - camera.z - 0.51F, aabb.maxX - camera.x + 0.51F, aabb.maxY - camera.y + 0.51F, aabb.maxZ - camera.z + 0.51F);
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
        aabbRenderer = new AABBRenderer();
        raabbRenderer = new RAABBRenderer();
    }

}
