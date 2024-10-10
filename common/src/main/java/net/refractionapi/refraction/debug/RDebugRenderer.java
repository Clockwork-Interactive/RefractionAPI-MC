package net.refractionapi.refraction.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public abstract class RDebugRenderer {

    protected final Minecraft minecraft;

    public RDebugRenderer(String id) {
        this.minecraft = Minecraft.getInstance();
    }

    protected abstract void render(PoseStack poseStack, MultiBufferSource multiBufferSource, double cameraX, double cameraY, double cameraZ);

    protected void renderBox(BlockPos pos, float red, float green, float blue, float alpha, PoseStack stack, MultiBufferSource source) {
        DebugRenderer.renderFilledBox(stack, source, pos, 1.0F, red, green, blue, alpha);
    }

    protected void renderLine(Vec3 from, Vec3 to, float red, float green, float blue, float alpha, PoseStack stack, MultiBufferSource source) {
        LevelRenderer.renderLineBox(stack, source.getBuffer(RenderType.LINES), from.x, from.y, from.z, to.x, to.y, to.z, red, green, blue, alpha);
    }

    protected void renderText(String string, Vec3 vec3, PoseStack stack, MultiBufferSource source) {
        DebugRenderer.renderFloatingText(stack, source, string, (float) vec3.x, (float) vec3.y, (float) vec3.z, -1);
    }

}
