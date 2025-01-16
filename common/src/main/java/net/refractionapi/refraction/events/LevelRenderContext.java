package net.refractionapi.refraction.events;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;

public class LevelRenderContext {
    LevelRenderer worldRenderer;
    PoseStack poseStack;
    DeltaTracker tickCounter;
    Camera camera;
    GameRenderer gameRenderer;
    LightTexture lightmapTextureManager;
    Matrix4f projectionMatrix;
    Matrix4f positionMatri;
    MultiBufferSource source;
    Level level;

    public void prepare(LevelRenderer worldRenderer, DeltaTracker tickCounter, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f projectionMatrix, Matrix4f positionMatrix, MultiBufferSource source, Level level) {
        this.worldRenderer = worldRenderer;
        this.poseStack = null;
        this.tickCounter = tickCounter;
        this.camera = camera;
        this.gameRenderer = gameRenderer;
        this.lightmapTextureManager = lightmapTextureManager;
        this.projectionMatrix = projectionMatrix;
        this.positionMatri = positionMatrix;
        this.source = source;
        this.level = level;
    }

    public void setPoseStack(PoseStack pose) {
        this.poseStack = pose;
    }

    public LevelRenderer getWorldRenderer() {
        return worldRenderer;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public DeltaTracker getTickCounter() {
        return tickCounter;
    }

    public Camera getCamera() {
        return camera;
    }

    public GameRenderer getGameRenderer() {
        return gameRenderer;
    }

    public LightTexture getLightmapTextureManager() {
        return lightmapTextureManager;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getPositionMatrix() {
        return positionMatri;
    }

    public MultiBufferSource getSource() {
        return source;
    }

    public Level getLevel() {
        return level;
    }
}
