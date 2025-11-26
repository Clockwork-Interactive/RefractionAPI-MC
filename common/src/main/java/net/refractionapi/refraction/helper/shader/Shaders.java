package net.refractionapi.refraction.helper.shader;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class Shaders implements ResourceManagerReloadListener {
    private final ResourceLocation shaderLocation;

    public Shaders(ResourceLocation shaderLocation) {
        this.shaderLocation = shaderLocation;
    }

    public void addPass() {

    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {

    }
}
